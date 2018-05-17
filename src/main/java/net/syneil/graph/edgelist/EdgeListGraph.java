package net.syneil.graph.edgelist;

import net.syneil.graph.Edge;
import net.syneil.graph.GraphProperties;
import net.syneil.graph.MutableGraph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdgeListGraph<V, E extends Edge<V>> implements MutableGraph<V, E> {
    private final Set<V> vertices = new HashSet<>();
    private final Set<E> edges = new HashSet<>();

    @Override
    public boolean addVertex(V v) {
        Objects.requireNonNull(v);
        return vertices.add(v);
    }

    @Override
    public boolean removeVertex(V v) {
        Objects.requireNonNull(v);
        return vertices.remove(v) | edges.removeIf(e -> v.equals(e.getSource()) || v.equals(e.getTarget()));
    }

    @Override
    public boolean addEdge(E edge) {
        Objects.requireNonNull(edge);
        Objects.requireNonNull(edge.getSource());
        Objects.requireNonNull(edge.getTarget());
        return !edges.contains(edge) && hasVertex(edge.getSource()) && hasVertex(edge.getTarget())
                && edges.add(edge);
    }

    @Override
    public boolean removeEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.removeIf(edge ->
                source.equals(edge.getSource()) && target.equals(edge.getTarget()));
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
        return edges.stream().anyMatch(edge -> source.equals(edge.getSource()) && target.equals(edge.getTarget()));
    }

    @Override
    public Set<? extends V> neighbours(V v) {
        Objects.requireNonNull(v);
        return edges.stream()
                .filter(edge -> v.equals(edge.getSource()))
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<? extends E> getEdges(V source, V target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        return edges.stream()
                .filter(edge -> source.equals(edge.getSource()) && target.equals(edge.getTarget()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<? extends E> getEdges(V source) {
        Objects.requireNonNull(source);
        return edges.stream()
                .filter(edge -> source.equals(edge.getSource()))
                .collect(Collectors.toSet());
    }

    @Override
    public GraphProperties getProperties() {
        return null;
    }
}
