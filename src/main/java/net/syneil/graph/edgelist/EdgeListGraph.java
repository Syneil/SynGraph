package net.syneil.graph.edgelist;

import net.syneil.graph.Edge;
import net.syneil.graph.GraphProperties;
import net.syneil.graph.GraphPropertyViolationException;
import net.syneil.graph.MutableGraph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.isEqual;
import static net.syneil.graph.GraphProperties.Looping.FORBIDDEN;
import static net.syneil.graph.GraphProperties.Looping.PERMITTED;
import static net.syneil.graph.GraphProperties.Multiplicity.MULTIPLE;
import static net.syneil.graph.GraphProperties.Multiplicity.SINGLE;
import static net.syneil.graph.GraphProperties.Orientation.DIRECTED;
import static net.syneil.graph.GraphProperties.Orientation.UNDIRECTED;

/**
 *
 */
public class EdgeListGraph<V, E extends Edge<V>> implements MutableGraph<V, E> {
    private final Set<V> vertices = new HashSet<>();
    private final Set<E> edges = new HashSet<>();

    private final GraphProperties properties;

    public EdgeListGraph() {
        this(null);
    }

    public EdgeListGraph(GraphProperties properties) {
        Optional<GraphProperties> props = Optional.ofNullable(properties);
        this.properties = GraphProperties.builder()
                .multiplicity(props.flatMap(GraphProperties::getMultiplicity).orElse(MULTIPLE))
                .orientation(props.flatMap(GraphProperties::getOrientation).orElse(DIRECTED))
                .looping(props.flatMap(GraphProperties::getLooping).orElse(PERMITTED))
                .build();
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
        Objects.requireNonNull(edge.getSource());
        Objects.requireNonNull(edge.getTarget());

        if (violatesMultiplicity(edge)) {
            throw new GraphPropertyViolationException("Edge would violate the multiplicity constraint of this graph");
        }
        if (violatesLooping(edge)) {
            throw new GraphPropertyViolationException("Edge would violate the looping constraint of this graph");
        }

        return hasVertex(edge.getSource())
                && hasVertex(edge.getTarget())
                && edges.add(edge);
    }

    private boolean violatesLooping(E edge) {
        return properties.getLooping().filter(isEqual(FORBIDDEN)).isPresent() && edge.isSelfEdge();
    }

    private boolean violatesMultiplicity(E edge) {
        return  // if we allow multiple then can't be violated
                properties.getMultiplicity().filter(isEqual(SINGLE)).isPresent() && (
                        // but if we don't then there must not already be an edge here
                        hasEdge(edge.getSource(), edge.getTarget()) || (
                                // and if we're also undirected
                                properties.getOrientation().filter(isEqual(UNDIRECTED)).isPresent()
                                        // then check there's no reverse edge
                                        && hasEdge(edge.getTarget(), edge.getSource())
                        )
                );
    }

    @Override
    public boolean removeEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.removeIf(Edge.isBetween(source, target));
    }

    @Override
    public boolean removeEdge(E edge) {
        Objects.requireNonNull(edge);
        return edges.remove(edge);
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
    public Stream<V> vertices() {
        return vertices.stream();
    }

    @Override
    public Stream<E> edges() {
        return edges.stream();
    }

    @Override
    public boolean hasVertex(V vertex) {
        Objects.requireNonNull(vertex);
        return vertices.contains(vertex);
    }

    @Override
    public boolean hasEdge(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.stream().anyMatch(Edge.isBetween(source, target));
    }

    @Override
    public Set<? extends V> neighbours(V v) {
        Objects.requireNonNull(v);
        return edges.stream()
                .filter(Edge.isFrom(v))
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<? extends E> getEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.stream()
                .filter(Edge.isBetween(source, target))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<? extends E> getEdges(V source) {
        Objects.requireNonNull(source);
        return edges.stream()
                .filter(Edge.isFrom(source))
                .collect(Collectors.toSet());
    }

    @Override
    public GraphProperties getProperties() {
        return properties;
    }
}
