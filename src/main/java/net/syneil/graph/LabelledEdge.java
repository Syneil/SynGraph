package net.syneil.graph;

/**
 * An edge with a label
 *
 * @param <V> the type used for vertices
 * @param <L> the type used for the edge label
 */
public interface LabelledEdge<V, L> extends Edge<V> {
    /**
     * @return the label for this edge
     */
    L getLabel();
}
