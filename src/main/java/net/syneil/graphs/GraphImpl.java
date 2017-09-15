package net.syneil.graphs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Complete graph implementation. Uses hashes internally to locate vertices, so care must be taken by the
 * consumer to ensure that any mutations of objects used as vertices does not change its hashcode.
 *
 * @param <V> type used for the vertices
 * @param <E> type used for the edge labels
 */
public class GraphImpl<V, E> implements Graph<V, E> {
    /** Internal representation of this graph */
    private Map<V, Set<Edge<V, E>>> data = new HashMap<>();

    @Override
    public long numberOfVertices() {
        return this.data.size();
    }

    @Override
    public long numberOfEdges() {
        // number of edges according to the data representation
        final long dataEdges = this.data.values().stream().flatMap(Set::stream).count();

        // but non-directed edges will be counted twice
        final long undirectedEdges = this.data.values().stream().flatMap(Set::stream).filter(edge -> !edge.isDirected())
                .count();

        // so count only half of them
        return dataEdges - (undirectedEdges / 2);
    }

    @Override
    public Stream<V> vertices() {
        return this.data.keySet().stream();
    }

    @Override
    public Stream<Edge<V, E>> edges() {
        return this.data.values().stream().flatMap(Set::stream);
    }

    @Override
    public boolean hasVertex(final V vertex) {
        return this.data.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(final V source, final V target) {
        return hasVertex(source) && hasVertex(target)
                && this.data.get(source).stream().anyMatch(edge -> Objects.equals(edge.getTarget(), target));
    }

    @Override
    public Set<V> neighbours(final V vertex) {
        if (!hasVertex(vertex)) {
            return Collections.emptySet();
        }
        return this.data.get(vertex).stream().map(Edge::getTarget).collect(Collectors.toSet());
    }

    @Override
    public boolean addVertex(final V vertex) {
        return this.data.putIfAbsent(vertex, new HashSet<>()) == null;
    }

    @Override
    public boolean removeVertex(final V vertex) {
        // trivially false if not present
        if (!this.data.containsKey(vertex)) {
            return false;
        }

        // must also remove its edges
        this.data.values().forEach(edges -> edges.removeIf(edge -> Objects.equals(edge.getTarget(), vertex)));
        this.data.remove(vertex);
        return true;
    }

    /**
     * Adds an edge from the source to target vertices with label and orientation defined.
     *
     * @param source the source vertex
     * @param label the edge label
     * @param target the target vertex
     * @param directed true if the edge should be directed; false otherwise
     * @return true if this graph was updated as a result of this call; false otherwise - meaning a matching edge
     *         already exists
     */
    @Override
    public boolean addEdge(final V source, final E label, final V target, final boolean directed) {
        try {
            if (!this.data.containsKey(source)) {
                this.data.put(source, new HashSet<>());
            }
            if (!this.data.containsKey(target)) {
                this.data.put(target, new HashSet<>());
            }
            return this.data.get(source).add(new EdgeImpl<>(source, directed, label, target));
        } finally {
            if (!directed) {
                this.data.get(target).add(new EdgeImpl<>(target, directed, label, source));
            }
        }
    }

    @Override
    public Set<Edge<V, E>> getEdges(final V source, final V target) {
        if (!hasVertex(source) || !hasVertex(target)) {
            return Collections.emptySet();
        }
        return this.data.get(source).stream().filter(e -> Objects.equals(target, e.getTarget()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Edge<V, E>> getEdges(final V source) {
        return this.data.get(source);
    }

    @Override
    public boolean removeEdges(final V source, final V target) {
        return this.data.get(source).removeIf(e -> Objects.equals(target, e.getTarget()));
    }

    /**
     * Returns a view of the datastore for this graph. The map and sets may be modified without affecting this graph,
     * but changes to the objects used as vertices or edge labels cannot be protected against.
     *
     * @return a view of the datastore for this graph
     */
    protected Map<V, Set<Edge<V, E>>> getData() {
        return this.data.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> new HashSet<>(pair.getValue())));
    }

    /**
     * Completely overwrites the datastore used to represent this graph.
     *
     * @param data the new datastore to represent this graph from now on
     */
    protected void overwriteData(final Map<V, Set<Edge<V, E>>> data) {
        this.data = data;
    }
}
