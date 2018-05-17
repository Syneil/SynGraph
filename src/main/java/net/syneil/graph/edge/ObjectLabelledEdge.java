package net.syneil.graph.edge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.syneil.graph.LabelledEdge;

import java.util.Objects;

/**
 * An edge between two vertices with a label
 *
 * @param <V> the type used for vertices
 * @param <L> the type used for the label
 */
@Getter
@ToString
@AllArgsConstructor
public class ObjectLabelledEdge<V, L> implements LabelledEdge<V, L> {

    /**
     * The source vertex of this edge
     */
    private final V source;

    /**
     * The target vertex of this edge
     */
    private final V target;

    /**
     * The label of this edge
     */
    private final L label;

    @Override
    public boolean connects(V vertex) {
        return Objects.equals(vertex, source) || Objects.equals(vertex, target);
    }

    @Override
    public boolean hasSource(V vertex) {
        return Objects.equals(vertex, source);
    }

    @Override
    public boolean hasTarget(V vertex) {
        return Objects.equals(vertex, target);
    }
}
