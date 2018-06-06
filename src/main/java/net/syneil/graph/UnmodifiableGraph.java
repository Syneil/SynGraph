package net.syneil.graph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A graph that cannot be modified. Note that the injected vertices and edges are not guaranteed to be immutable.
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 */
public class UnmodifiableGraph<V, E extends Edge<V>> implements Graph<V, E> {
    private final List<V> vertices;
    private final List<E> edges;

    public UnmodifiableGraph(Collection<? extends V> vertices, Collection<? extends E> edges) {
        Objects.requireNonNull(vertices);
        Objects.requireNonNull(edges);
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
        this.edges = Collections.unmodifiableList(new ArrayList<>(edges));
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
        return vertices.contains(vertex);
    }

    @Override
    public boolean hasEdge(V source, V target) {
        return edges.stream().anyMatch(Edge.isBetween(source, target));
    }

    @Override
    public Set<V> neighbours(V v) {
        return edges.stream().filter(Edge.isFrom(v)).map(Edge::getTarget).collect(Collectors.toSet());
    }

    @Override
    public List<E> getEdges(V source, V target) {
        return edges.stream().filter(Edge.isBetween(source, target)).collect(Collectors.toList());
    }

    @Override
    public List<E> getEdges(V source) {
        return edges.stream().filter(Edge.isFrom(source)).collect(Collectors.toList());
    }

    @Override
    public GraphProperties getProperties() {
        return GraphProperties.builder().build();
    }
}
