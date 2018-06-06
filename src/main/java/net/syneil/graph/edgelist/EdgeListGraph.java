package net.syneil.graph.edgelist;

import net.syneil.graph.*;
import net.syneil.graph.GraphProperties.Looping;
import net.syneil.graph.GraphProperties.Multiplicity;
import net.syneil.graph.GraphProperties.Orientation;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.syneil.graph.Edge.isBetween;
import static net.syneil.graph.Edge.isFrom;
import static net.syneil.graph.GraphProperties.Looping.PERMITTED;
import static net.syneil.graph.GraphProperties.Multiplicity.MULTIPLE;
import static net.syneil.graph.GraphProperties.Orientation.DIRECTED;
import static net.syneil.graph.GraphProperties.Orientation.UNDIRECTED;

/**
 * A graph described by a set of vertices and a set of edges
 */
public class EdgeListGraph<V, E extends Edge<V>> implements MutableGraph<V, E> {
    /**
     * The properties of this graph, with no empties
     */
    private final GraphProperties properties;

    /**
     * Produces a test for if edges are between a given two vertices; defined at construct-time for optimisation
     */
    private final BiFunction<V, V, Predicate<E>> edgeMatchPredicateSupplier;

    /**
     * The operation that tries to remove an edge and returns true if successful; defined at construct-time for
     * optimisation
     */
    private final BiPredicate<Set<E>, E> removeEdgePredicate;

    /**
     * The test for if adding an edge would violation this graph's multiplicity property; defined at construct-time for
     * optimisation
     */
    private final BiPredicate<Set<E>, E> multiplicityViolationPredicate;


    private final BiFunction<V, Graph<V, E>, Set<V>> neighboursFunction;

    /**
     * The test for if adding an edge would violate this graph's looping property; defined at construct-time for
     * optimisation
     */
    private final Predicate<E> loopingViolationPredicate;

    /**
     * The vertices of this graph
     */
    private final Set<V> vertices = new HashSet<>();

    /**
     * The edges of this graph
     */
    private final Set<E> edges = new HashSet<>();

    /**
     * Creates an empty graph with default properties. The defaults are: {@link Multiplicity#MULTIPLE multiple} edges
     * allowed between the same two nodes; {@link Orientation#DIRECTED directed} edges; and {@link Looping#PERMITTED
     * self-loops} permitted.
     */
    public EdgeListGraph() {
        this(null);
    }

    /**
     * Creates an empty graph with the specified properties. If any of the properties are {@link Optional#empty()
     * undefined}, defaults will be assumed in their place. The defaults are: {@link Multiplicity#MULTIPLE multiple}
     * edges allowed between the same two nodes; {@link Orientation#DIRECTED directed} edges; and {@link
     * Looping#PERMITTED self-loops} permitted.
     *
     * @param properties the properties of this graph; null implies defaults
     */
    @SuppressWarnings("unchecked")
    public EdgeListGraph(GraphProperties properties) {
        var props = Optional.ofNullable(properties);
        var multiplicity = props.flatMap(GraphProperties::getMultiplicity).orElse(MULTIPLE);
        var orientation = props.flatMap(GraphProperties::getOrientation).orElse(DIRECTED);
        var looping = props.flatMap(GraphProperties::getLooping).orElse(PERMITTED);
        this.properties = GraphProperties.builder()
                                         .multiplicity(multiplicity)
                                         .orientation(orientation)
                                         .looping(looping)
                                         .build();
        edgeMatchPredicateSupplier = createEdgeMatchPredicateSupplier(orientation);
        removeEdgePredicate = createRemoveEdgePredicate(orientation);
        multiplicityViolationPredicate = createMultiplicityViolationCheck(multiplicity, orientation);
        loopingViolationPredicate = createLoopingViolationPredicate(looping);
        neighboursFunction = createNeighboursFunction(orientation);
    }

    @Override
    public boolean addVertex(V v) {
        Objects.requireNonNull(v);
        return vertices.add(v);
    }

    @Override
    public boolean removeVertex(V v) {
        Objects.requireNonNull(v);
        // must NOT short-circuit the disjunction
        return vertices.remove(v) | edges.removeIf(e -> e.connects(v));
    }

    @Override
    public boolean addEdge(E edge) {
        Objects.requireNonNull(edge);
        addVertex(edge.getSource());
        addVertex(edge.getTarget());

        if (multiplicityViolationPredicate.test(edges, edge)) {
            throw new GraphPropertyViolationException("Edge would violate the multiplicity constraint of this graph");
        }
        if (loopingViolationPredicate.test(edge)) {
            throw new GraphPropertyViolationException("Edge would violate the looping constraint of this graph");
        }

        return edges.add(edge);
    }

    @Override
    public boolean hasVertex(V vertex) {
        Objects.requireNonNull(vertex);
        return vertices.contains(vertex);
    }

    @Override
    public boolean hasEdge(V source, V target) {
        return hasEdge(edges, source, target);
    }

    private static <V, E extends Edge<V>> boolean hasEdge(Set<E> edges, V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.stream().anyMatch(isBetween(source, target));
    }

    @Override
    public boolean removeEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.removeIf(edgeMatchPredicateSupplier.apply(source, target));
    }

    @Override
    public boolean removeEdge(E edge) {
        Objects.requireNonNull(edge);
        return removeEdgePredicate.test(edges, edge);
    }

    @Override
    public long numberOfVertices() {
        return vertices.size();
    }

    @Override
    public long numberOfEdges() {
        return edges.size();
    }

    @Override
    public Stream<? extends V> vertices() {
        return vertices.stream();
    }

    @Override
    public Stream<? extends E> edges() {
        return edges.stream();
    }

    @Override
    public Set<? extends V> neighbours(V v) {
        Objects.requireNonNull(v);
        return neighboursFunction.apply(v, this);
    }

    @Override
    public List<? extends E> getEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.stream().filter(isBetween(source, target)).collect(Collectors.toList());
    }

    @Override
    public List<? extends E> getEdges(V source) {
        Objects.requireNonNull(source);
        return edges.stream().filter(isFrom(source)).collect(Collectors.toList());
    }

    @Override
    public GraphProperties getProperties() {
        return properties;
    }

    /**
     * Defines a graph's strategy for testing if an edge matches a source/target vertex pair according to an {@link
     * Orientation} property. That is, for {@link Orientation#DIRECTED directed} graphs, an edge matches such a pair if
     * its {@link Edge#getSource() source} is the first of the pair and its {@link Edge#getTarget() target} is the
     * second; whereas in {@link Orientation#UNDIRECTED undirected} graphs, the roles of source and target may also be
     * reversed.
     *
     * @param orientation the orientation strategy
     * @param <V> the type used for vertices
     * @param <E> the type used for edges
     *
     * @return an operation to create tests for edges being between a given two vertices
     */
    private static <V, E extends Edge<V>> BiFunction<V, V, Predicate<E>>
    createEdgeMatchPredicateSupplier(Orientation orientation) {
        switch (orientation) {
            case DIRECTED: return (source, target) -> isBetween(source, target)::test;
            case UNDIRECTED: return (source, target) -> edge -> edge.connects(source) && edge.connects(target);
        }
        throw new Error();
    }

    /**
     * Defines a graph's strategy for removing edges and returning if the graph was updated as a result. In {@link
     * Orientation#DIRECTED directed} graphs we need only remove those edges matching the source and target of that
     * given, but in {@link Orientation#UNDIRECTED undirected} ones we must also remove those with source and target
     * reversed.
     *
     * @param orientation the orientation strategy
     * @param <V> the type used for vertices
     * @param <E> the type used for edges
     *
     * @return the strategy to remove edges and return if the graph was updated as a result
     */
    private static <V, E extends Edge<V>> BiPredicate<Set<E>, E> createRemoveEdgePredicate(Orientation orientation) {
        switch (orientation) {
            case DIRECTED: return Set::remove;
            case UNDIRECTED: return (edges, edge) -> edges.removeIf(
                    e -> e.connects(edge.getSource()) && e.connects(edge.getTarget()));
        }
        throw new Error();
    }

    /**
     * Defines a graph's strategy for validating new edges' conformance to its {@link Multiplicity} property. That is,
     * graphs permitting {@link Multiplicity#MULTIPLE multiple} edges between the same pair of vertices cannot be
     * violated, but {@link Multiplicity#SINGLE otherwise} we must ensure there are no edges with the same {@link
     * Edge#getSource() source} and {@link Edge#getTarget() target} already in the graph (or vice-versa for {@link
     * Orientation#UNDIRECTED undirected} graphs).
     *
     * @param multiplicity the multiplicity strategy
     * @param orientation the orientation strategy
     * @param <V> the type used for vertices
     * @param <E> the type used for edges
     *
     * @return the strategy for validating edges' conformance to a graph's multiplicity constraints
     */
    private static <V, E extends Edge<V>> BiPredicate<Set<E>, E> createMultiplicityViolationCheck(
            Multiplicity multiplicity,
            Orientation orientation) {
        if (multiplicity == MULTIPLE) {
            return ($1, $2) -> false;
        }
        if (orientation == UNDIRECTED) {
            return (edges, edge) -> hasEdge(edges, edge.getSource(), edge.getTarget());
        }
        return (edges, edge) -> edges.stream()
                                     .filter(e -> e.connects(edge.getSource()))
                                     .anyMatch(e -> e.connects(edge.getTarget()));
    }

    /**
     * Defines a graph's strategy for validating new edges' conformance for its {@link Looping} property. That is,
     * graphs permitting {@link Looping#PERMITTED self-loops} cannot be violated, whereas those {@link Looping#FORBIDDEN
     * forbidding} them will be violated if the {@link Edge#getSource() source} and {@link Edge#getTarget() target} are
     * the same vertex.
     *
     * @param looping the looping policy
     * @param <V> the type used for vertices
     * @param <E> the type used for edges
     *
     * @return the strategy for validating edges' conformance to a graph's looping policy
     */
    private static <V, E extends Edge<V>> Predicate<E> createLoopingViolationPredicate(Looping looping) {
        switch (looping) {
            case PERMITTED: return $ -> false;
            case FORBIDDEN: return Edge::isSelfEdge;
        }
        throw new Error();
    }

    private static <V, E extends Edge<V>> BiFunction<V, Graph<V, E>, Set<V>> createNeighboursFunction(
            final Orientation orientation) {
        switch (orientation) {
            case DIRECTED: return (v, g) -> g.edges()
                                             .filter(isFrom(v))
                                             .map(Edge::getTarget)
                                             .collect(Collectors.toSet());
            case UNDIRECTED: return (v, g) -> g.edges()
                                               .map(e -> e.other(v))
                                               .filter(Optional::isPresent)
                                               .map(Optional::get)
                                               .collect(Collectors.toSet());
        }
        throw new Error();
    }
}
