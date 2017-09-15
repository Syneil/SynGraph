package net.syneil.graphs.generation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import net.syneil.graphs.EdgeImpl;
import net.syneil.graphs.Graph;
import net.syneil.graphs.Graph.Edge;
import net.syneil.graphs.GraphImpl;
import net.syneil.graphs.constraints.ConstrainedGraph;
import net.syneil.graphs.constraints.ConstraintViolationException;
import net.syneil.graphs.constraints.GraphConstraint;
import net.syneil.graphs.constraints.Multiplicity;
import net.syneil.graphs.constraints.Orientation;

/**
 * Factory class to create graphs
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 */
public class GraphGenerator<V, E> {

    /** The orientation of the graph to be generated */
    private Orientation orientation;

    /** The edge-multiplicity of the graph to be generated */
    private Multiplicity multiplicity;

    /** The extra constraints to apply to the graph to be generated */
    private final Set<GraphConstraint<V, E>> constraints = new HashSet<>();

    /**
     * Initialises the graph generation for an unconstrained graph
     */
    public GraphGenerator() {
        this(Orientation.MIXED, Multiplicity.MULTI);
    }

    /**
     * Initialises the graph generation with constraints appropriate for the orientation and multiplicity provided
     *
     * @param orientation the orientation to restrict the graph to
     * @param multiplicity the multiplicity to restrict the graph to
     */
    public GraphGenerator(final Orientation orientation, final Multiplicity multiplicity) {
        this.orientation = orientation;
        this.multiplicity = multiplicity;
    }

    /**
     * @param orientation the orientation to restrict the graph to
     * @return this generator for method chaining
     */
    public GraphGenerator<V, E> orientedAs(final Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    /**
     * @param multiplicity the multiplicity to restrict the graph to
     * @return this generator for method chaining
     */
    public GraphGenerator<V, E> withMultiplicity(final Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
        return this;
    }

    /**
     * @param constraint a constraint to apply to the graph
     * @return this generator for method chaining
     */
    public GraphGenerator<V, E> withConstraint(final GraphConstraint<V, E> constraint) {
        this.constraints.add(constraint);
        return this;
    }

    /**
     * @param constraints constraints to apply to the graph
     * @return this generator for method chaining
     */
    public GraphGenerator<V, E> withConstraints(
            @SuppressWarnings("unchecked") final GraphConstraint<V, E>... constraints) {
        Stream.of(constraints).forEach(this.constraints::add);
        return this;
    }

    /**
     * @return a graph with the constraints all pre-loaded
     */
    public Graph<V, E> generate() {
        final ConstrainedGraph<V, E> graph = new ConstrainedGraph<>(this.orientation, this.multiplicity);
        this.constraints.forEach(graph::addConstraint);
        return graph;
    }

    /**
     * Constructs a complete graph of the given size. A complete graph is one in which every node is connected to every
     * other node.
     *
     * @param size the number of vertices needed
     * @param graphConstructor a graph supplier
     * @param vertexProducer a function on the graph and the {@code long n} to create the nth vertex
     * @param vertexAdder method of adding the vertex to the graph
     * @param edgeProducer a function on two vertices to create the edge between them
     * @param edgeAdder a biconsumer accepting the graph and an edge to add the edge to the graph
     * @return the constructed complete graph
     */
    public static <V, E> Graph<V, E> complete(final long size, final Supplier<Graph<V, E>> graphConstructor,
            final BiFunction<Graph<V, E>, Long, V> vertexProducer, final BiConsumer<Graph<V, E>, V> vertexAdder,
            final BiFunction<V, V, Edge<V, E>> edgeProducer, final BiConsumer<Graph<V, E>, Edge<V, E>> edgeAdder) {
        final Graph<V, E> result = graphConstructor.get();

        LongStream.range(0, size).mapToObj(num -> vertexProducer.apply(result, num))
                .forEach(v -> vertexAdder.accept(result, v));
        result.vertices() // each vertex
                .forEach(source -> result.vertices() // pair
                        .filter(target -> !Objects.equals(source, target)) // except identity pairings
                        // add an edge
                        .forEach(target -> edgeAdder.accept(result, edgeProducer.apply(source, target))));

        return result;
    }

    /** @return an immutable empty graph */
    public static <V, E> Graph<V, E> empty() {
        return new Graph<V, E>() {
            //@formatter:off
            @Override public long numberOfVertices() { return 0L; }
            @Override public long numberOfEdges() { return 0; }
            @Override public Stream<V> vertices() { return Stream.empty(); }
            @Override public Stream<EdgeImpl<V, E>> edges() { return Stream.empty(); }
            @Override public boolean hasVertex(final V vertex) { return false; }
            @Override public boolean hasEdge(final V source, final V target) { return false; }
            @Override public Set<V> neighbours(final V v) { return Collections.emptySet(); }
            @Override public boolean addVertex(final V v) { throw new ConstraintViolationException("The empty graph may not have vertices or edges"); }
            @Override public boolean removeVertex(final V v) { return false; }
            @Override public boolean addEdge(final V source, final E label, final V target, final boolean directed) { throw new ConstraintViolationException("The empty graph may not have vertices or edges"); }
            @Override public Set<EdgeImpl<V, E>> getEdges(final V source, final V target) { return Collections.emptySet(); }
            @Override public Set<EdgeImpl<V, E>> getEdges(final V source) { return Collections.emptySet(); }
            @Override public boolean removeEdges(final V source, final V target) { return false; }
            //@formatter:on
        };
    }

    /**
     * Returns an Apollonian graph at the given depth. An Apollonian graph may be formed starting from a single triangle
     * embedded in the Euclidean plane, by repeatedly selecting a triangular face of the embedding, adding a new vertex
     * inside the face, and connecting the new vertex to each vertex of the face containing it. In this way, the
     * triangle containing the new vertex is subdivided into three smaller triangles, which may in turn be subdivided in
     * the same way.
     *
     * @param depth the depth to which to construct the Apollonian graph. Depths greater than 15 are not recommended,
     *        given the size of the resultant graph, and so they are blocked (by throwing a {@link RuntimeException}).
     * @return an unlabelled Apollonian graph constructed to the given depth
     */
    // Not recommended for depth > 15
    public static Graph<Integer, Void> apollonian(final int depth) {
        if (depth > 15) {
            throw new RuntimeException("don't be a prick");
        }

        ConstrainedGraph<Integer, Void> result = (ConstrainedGraph<Integer, Void>) new GraphGenerator<Integer, Void>()
                .orientedAs(Orientation.UNDIRECTED).withMultiplicity(Multiplicity.SIMPLE).generate();
        if (depth < 1) {
            return result;
        }

        IntStream.range(0, 4).forEach(result::addVertex);
        int index = 4;

        // horizon indicates edges to add and the depth they contribute to
        final Queue<Integer[]> horizon = new LinkedList<>();
        horizon.offer(new Integer[] {
                1, 0, 1, 2
        });
        horizon.offer(new Integer[] {
                1, 0, 2, 3
        });
        horizon.offer(new Integer[] {
                1, 0, 3, 1
        });

        while (!horizon.isEmpty()) {
            final Integer[] next = horizon.poll();
            // add the missing edges
            if (!result.hasEdge(next[1], next[2])) {
                result.addEdge(next[1], null, next[2], false);
            }
            if (!result.hasEdge(next[1], next[3])) {
                result.addEdge(next[1], null, next[3], false);
            }
            if (!result.hasEdge(next[2], next[3])) {
                result.addEdge(next[2], null, next[3], false);
            }
            // if this is already the required depth then don't add to the horizon
            if (next[0] >= depth) {
                continue;
            }
            // otherwise add the new vertex in the middle of this triangle and update the horizon to create their edges
            result.addVertex(index);
            horizon.offer(new Integer[] {
                    next[0] + 1, index, next[1], next[2]
            });
            horizon.offer(new Integer[] {
                    next[0] + 1, index, next[1], next[3]
            });
            horizon.offer(new Integer[] {
                    next[0] + 1, index, next[2], next[3]
            });
            index++;
        }

        return result;
    }

    /**
     * Creates a line graph from a collection of vertices and a function to produce an edge between successive vertices.
     * The type of collection is not restricted, so ordering and uniqueness across the vertices is dependent on that
     * provided.
     *
     * @param nodes the collection of vertices
     * @param edgeProducer the function to create edges between successive vertices
     * @return a line graph of the provided nodes
     */
    public static <V, E> Graph<V, E> line(final Collection<V> nodes, final BiFunction<V, V, E> edgeProducer) {
        final Graph<V, E> result = new GraphImpl<>();
        @SuppressWarnings("unchecked")
        V[] last = (V[]) new Object[] {
                null
        };
        nodes.forEach(v -> {
            result.addVertex(v);
            if (last[0] != null) {
                result.addEdge(last[0], edgeProducer.apply(last[0], v), v, true);
            }
            last[0] = v;
        });
        return result;
    }
}
