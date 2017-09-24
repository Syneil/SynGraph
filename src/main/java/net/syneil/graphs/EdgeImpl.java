package net.syneil.graphs;

import java.util.Objects;
import java.util.Optional;

import net.syneil.graphs.Graph.Edge;

/**
 * An edge in the graph
 *
 * @param <V> the type of the vertices this edge connects
 * @param <E> the type of the edge label; may be {@link Void}
 */
public class EdgeImpl<V, E> implements Edge<V, E> {
    /**
     * The source vertex of this edge
     */
    private final V source;

    /**
     * True if this edge is directed, false otherwise
     */
    private final boolean directed;

    /**
     * The label attached to this edge
     */
    private final E label;

    /**
     * The target vertex of this edge
     */
    private final V target;

    /**
     * Constructor
     *
     * @param source   source vertex
     * @param directed true if directed
     * @param label    edge label
     * @param target   target vertex
     */
    public EdgeImpl(final V source, final boolean directed, final E label, final V target) {
        this.source = source;
        this.directed = directed;
        this.label = label;
        this.target = target;
    }

    /**
     * @return the source vertex of this edge
     */
    @Override
    public V getSource() {
        return this.source;
    }

    /**
     * @return the target vertex of this edge
     */
    @Override
    public V getTarget() {
        return this.target;
    }

    /**
     * @return true if this edge is directed, false otherwise
     */
    @Override
    public boolean isDirected() {
        return this.directed;
    }

    /**
     * @return the label of this edge as an optional for null-safety
     */
    @Override
    public Optional<E> getLabel() {
        return Optional.ofNullable(this.label);
    }

    @Override
    public boolean connects(final V vertex) {
        return Objects.equals(vertex, this.source) || Objects.equals(vertex, this.target);
    }

    @Override
    public boolean hasSource(final V vertex) {
        return Objects.equals(vertex, this.source) || (!this.directed && Objects.equals(vertex, this.target));
    }

    @Override
    public boolean hasTarget(final V vertex) {
        return Objects.equals(vertex, this.target) || (!this.directed && Objects.equals(vertex, this.source));
    }

    @Override
    public String toString() {
        return String.valueOf(this.source) + (this.directed ? '-' : '<') + '-' + this.label + "->" + this.target;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.directed ? 1231 : 1237);
        result = prime * result + ((this.label == null) ? 0 : this.label.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode())
                + ((this.target == null) ? 0 : this.target.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EdgeImpl)) {
            return false;
        }
        EdgeImpl<?, ?> that = (EdgeImpl<?, ?>) obj;
        return this.directed == that.directed && Objects.equals(this.label, that.label)
                && ((Objects.equals(this.source, that.source) && Objects.equals(this.target, that.target))
                || (!this.directed && Objects.equals(this.source, that.target) && Objects.equals(this.target, that.source)));
    }
}