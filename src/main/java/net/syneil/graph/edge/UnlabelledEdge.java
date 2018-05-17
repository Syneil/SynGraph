package net.syneil.graph.edge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.syneil.graph.Edge;

import java.util.Objects;

/**
 * An edge between two vertices with no label
 *
 * @param <V> the type used for the vertices
 */
@AllArgsConstructor
@Getter
@ToString
public class UnlabelledEdge<V> implements Edge<V> {
    /**
     * The source vertex of this edge
     */
    private final V source;

    /**
     * The target vertex of this edge
     */
    private final V target;

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
