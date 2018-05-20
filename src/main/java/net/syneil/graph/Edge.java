package net.syneil.graph;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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
    default Optional<V> other(final V vertex) {
        Objects.requireNonNull(vertex);
        V source = getSource(), target = getTarget();
        return Optional.ofNullable(
                vertex.equals(source) ? target
                        : vertex.equals(target) ? source
                        : null);
    }

    /**
     * Creates a predicate that tests if edges are between two vertices
     *
     * @param source the source vertex
     * @param target the target vertex
     * @param <V>    the type used for vertices
     * @return a predicate that evaluates to true for edges that are between the given vertices and false otherwise
     */
    static <V> Predicate<Edge<V>> isBetween(V source, V target) {
        return isFrom(source).and(isTo(target));
    }

    /**
     * Creates a predicate that tests if edges are from a vertex
     *
     * @param source the source vertex
     * @param <V>    the type used for vertices
     * @return a predicate that evaluates to true for edges that are from the given vertex
     */
    static <V> Predicate<Edge<V>> isFrom(V source) {
        return edge -> edge.hasSource(source);
    }

    /**
     * Creates a predicate that tests if edges are to a vertex
     *
     * @param target the target vertex
     * @param <V>    the type used for vertices
     * @return a predicate that evaluates to true for edges that are from the given vertex
     */
    static <V> Predicate<Edge<V>> isTo(V target) {
        return edge -> edge.hasTarget(target);
    }
}
