package net.syneil.graphs;

import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A read-only view of a graph. The view can be defined with mutated vertices and/or edges in order to, for example,
 * analyse a graph with a vertex removed, or an edge with the reverse direction.
 *
 * @param <V> type used for vertices
 * @param <E> type used for edge labels
 */
public class GraphView<V, E> implements Graph<V, E> {

    /** The original graph this view is of */
    private final Graph<V, E> source;

    /** Function to mutate the original vertex stream */
    private final UnaryOperator<Stream<V>> vertexView;

    /** Function to mutate the original edge stream */
    private final UnaryOperator<Stream<? extends Edge<V, E>>> edgeView;

    /**
     * Constructor with mutators for both vertices and edges.
     *
     * @param source the graph to view
     * @param vertexView mutator for vertices
     * @param edgeView mutator for edges
     */
    public GraphView(final Graph<V, E> source, final UnaryOperator<Stream<V>> vertexView,
            final UnaryOperator<Stream<? extends Edge<V, E>>> edgeView) {
        this.source = source;
        this.vertexView = vertexView;
        this.edgeView = edgeView;
    }

    /**
     * Creates a view with only a vertex mutator, leaving edges unchanged.
     *
     * @param source the graph to view
     * @param vertexView mutator for vertices
     * @return a view of the source with its vertex stream mutated
     */
    public static <V, E> GraphView<V, E> ofVertices(final Graph<V, E> source,
            final UnaryOperator<Stream<V>> vertexView) {
        return new GraphView<>(source, vertexView, UnaryOperator.identity());
    }

    /**
     * Creates a view with only an edge mutator, leaving vertices unchanged.
     *
     * @param source the graph to view
     * @param edgeView mutator for edges
     * @return a view of the source with its edge stream mutated
     */
    public static <V, E> GraphView<V, E> ofEdges(final Graph<V, E> source,
            final UnaryOperator<Stream<? extends Edge<V, E>>> edgeView) {
        return new GraphView<>(source, UnaryOperator.identity(), edgeView);
    }

    /**
     * Creates a view with one vertex missing
     *
     * @param source the graph to view
     * @param vertex the vertex to simulate the removal of
     * @return a view of the source with the vertex removed
     */
    public static <V, E> GraphView<V, E> withoutVertex(final Graph<V, E> source, final V vertex) {
        return new GraphView<>(source, stream -> stream.filter(v -> !Objects.equals(v, vertex)), stream -> stream
                .filter(e -> !Objects.equals(e.getSource(), vertex) && !Objects.equals(e.getTarget(), vertex)));
    }
    
    /**
     * Creates a view with one edge missing
     *
     * @param source the graph to view
     * @param edge the edge to simulate the removal of
     * @return a view of the source with the edge removed
     */
    public static <V, E> GraphView<V, E> withoutEdge(final Graph<V, E> source, final EdgeImpl<V, E> edge) {
        return ofEdges(source, stream -> stream.filter(e -> !Objects.equals(edge, e)));
    }

    @Override
    public Stream<V> vertices() {
        return this.vertexView.apply(this.source.vertices());
    }

    @Override
    public Stream<? extends Edge<V, E>> edges() {
        return this.edgeView.apply(this.source.edges());
    }

    @Override
    public long numberOfVertices() {
        return this.vertices().count();
    }

    @Override
    public long numberOfEdges() {
        return this.edges().count();
    }

    @Override
    public boolean hasVertex(V vertex) {
        return this.vertices().anyMatch(v -> Objects.equals(v,  vertex));
    }

    @Override
    public boolean hasEdge(final V source, final V target) {
        return this.edges()
                .anyMatch(e -> Objects.equals(e.getSource(), source) && Objects.equals(e.getTarget(), target));
    }

    @Override
    public Set<V> neighbours(final V v) {
        return Stream
                .concat(this.edges().filter(e -> Objects.equals(e.getSource(), v)).map(Edge::getTarget),
                        this.edges().filter(e -> Objects.equals(e.getTarget(), v)).map(Edge::getSource))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean addVertex(final V v) {
        return false;
    }

    @Override
    public boolean removeVertex(final V v) {
        return false;
    }

    @Override
    public boolean addEdge(final V source, final E label, final V target, final boolean directed) {
        return false;
    }

    @Override
    public Set<Edge<V, E>> getEdges(final V source, final V target) {
        return this.edges().filter(e -> Objects.equals(e.getSource(), source))
                .filter(e -> Objects.equals(e.getTarget(), target)).collect(Collectors.toSet());
    }

    @Override
    public Set<Edge<V, E>> getEdges(final V source) {
        return this.edges().filter(e -> Objects.equals(source, e.getSource())).collect(Collectors.toSet());
    }

    @Override
    public boolean removeEdges(final V source, final V target) {
        return false;
    }
}
