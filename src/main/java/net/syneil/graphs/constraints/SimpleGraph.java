package net.syneil.graphs.constraints;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.syneil.graphs.EdgeImpl;
import net.syneil.graphs.Graph;

/**
 * <B>Valued:</B> true<br/>
 * <B>Labelled:</B> false<br/>
 * <B>Directed:</B> false<br/>
 * <B>Multiedged:</B> false<br/>
 *
 * @param <V> type for vertices
 */
public class SimpleGraph<V> implements Graph<V, Void> {
    /** Internal graph representation */
    private final Map<V, Set<V>> data = new HashMap<>();

    /** Tracks the number of edges */
    private long edgeCount = 0;

    @Override
    public long numberOfVertices() {
        return this.data.size();
    }

    @Override
    public long numberOfEdges() {
        return this.edgeCount;
    }

    @Override
    public Stream<V> vertices() {
        return this.data.keySet().stream();
    }

    @Override
    public Stream<EdgeImpl<V, Void>> edges() {
        return this.data.entrySet().stream().flatMap(entry -> entry.getValue().stream()
                .map(v -> new EdgeImpl<V, Void>(entry.getKey(), false, null, v)));
    }

    @Override
    public boolean hasVertex(final V vertex) {
        return this.data.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(final V a, final V b) {
        return this.data.get(a).contains(b);
    }

    @Override
    public Set<V> neighbours(final V vertex) {
        return new HashSet<>(this.data.get(vertex));
    }

    @Override
    public boolean addVertex(final V vertex) {
        return this.data.putIfAbsent(vertex, new HashSet<>()) == null;
    }

    @Override
    public boolean removeVertex(final V vertex) {
        final AtomicBoolean changed = new AtomicBoolean(false);
        this.data.keySet().stream().map(this.data::get).filter(set -> set.contains(vertex))
                .peek(set -> this.edgeCount--).forEach(set -> changed.compareAndSet(false, set.remove(vertex)));
        return this.data.remove(vertex) != null || changed.get();
    }

    @Override
    public boolean addEdge(final V a, final Void label, final V b, final boolean directed) {
        if (this.data.containsKey(a) && this.data.containsKey(b) && !this.data.get(a).contains(b)
                && !this.data.get(b).contains(a)) {
            this.data.get(a).add(b);
            if (!directed) {
                this.data.get(b).add(a);
            }
            this.edgeCount++;
            return true;
        }
        return false;
    }

    /**
     * Convenience method to add edges knowing that there won't be a label
     *
     * @param a one of the vertices
     * @param b one of the vertices
     * @param directed true if the edge is one-way, false for bi-directional
     * @return true if this graph was updated as a result of this call; false otherwise (meaning the graph already
     *         contained such an edge)
     */
    public boolean addEdge(final V a, final V b, final boolean directed) {
        return addEdge(a, null, b, directed);
    }

    @Override
    public boolean removeEdges(final V a, final V b) {
        if (this.hasEdge(a, b)) {
            this.data.get(a).remove(b);
            this.data.get(b).remove(a);
            this.edgeCount--;
            return true;
        }
        return false;
    }

    @Override
    public Set<EdgeImpl<V, Void>> getEdges(final V source, final V target) {
        if (this.hasEdge(source, target)) {
            return Collections.singleton(new EdgeImpl<V, Void>(source, false, null, target));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<EdgeImpl<V, Void>> getEdges(final V source) {
        if (this.data.containsKey(source)) {
            return this.data.get(source).stream().map(target -> new EdgeImpl<>(source, false, (Void) null, target))
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }
}
