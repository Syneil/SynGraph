package net.syneil.graph;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * An edge with a label
 *
 * @param <V> the type used for vertices
 * @param <L> the type used for the edge label
 */
public interface LabelledEdge<V, L> extends Edge<V> {
    /**
     * Creates a predicate that tests if edges have a specific label
     *
     * @param label the label to match
     * @param <L> the type used for labels
     *
     * @return a predicate that evaluates to true for edges with labels matching that given
     *
     * @see Objects#equals(Object, Object)
     */
    static <L> Predicate<LabelledEdge<?, L>> hasLabel(L label) {
        return edge -> Objects.equals(label, edge.getLabel());
    }

    /**
     * @return the label for this edge
     */
    L getLabel();
}
