package net.syneil.graph;

import java.util.Objects;
import java.util.Optional;

/**
 * An edge in the graph.
 *
 * @param <V> the type used for vertices
 */
public interface Edge<V> {

    /**
     * @param vertex a vertex to examine
     * @return true if the vertex is one of the vertices this edge connects, false otherwise
     */
    boolean connects(V vertex);

    /**
     * @param vertex the vertex to compare
     * @return true if the vertex is this edge's source
     */
    boolean hasSource(V vertex);

    /**
     * @param vertex the vertex to compare
     * @return true if the vertex is this edge's target
     */
    boolean hasTarget(V vertex);

    /**
     * @return the source vertex of this edge
     */
    V getSource();

    /**
     * @return the target vertex of this edge
     */
    V getTarget();

    /**
     * @return true if this edge has the same source and target
     */
    default boolean isSelfEdge() {
        return Objects.equals(getSource(), getTarget());
    }

    /**
     * @param vertex one of the vertices this edge connects (otherwise the empty optional will be returned)
     * @return the "other" vertex that this edge connects, or the empty optional if this edge does not connect the
     * vertex
     */
    default Optional<? extends V> other(final V vertex) {
        Optional<? extends V> result = Optional.empty();
        if (Objects.equals(getSource(), vertex)) {
            result = Optional.of(getTarget());
        } else if (Objects.equals(getTarget(), vertex)) {
            result = Optional.of(getSource());
        }
        return result;
    }
}
