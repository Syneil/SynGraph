package net.syneil.graph;

import lombok.Builder;
import lombok.ToString;

import java.util.Optional;

/**
 * Definition of the core properties of a graph implementation.
 *
 * @see Orientation
 * @see Looping
 * @see Multiplicity
 */
@Builder
@ToString
public final class GraphProperties {
    /**
     * The orientation of the graph
     */
    private final Orientation orientation;

    /**
     * The looping policy of the graph
     */
    private final Looping looping;

    /**
     * The edge-multiplicity of the graph
     */
    private final Multiplicity multiplicity;

    /**
     * @return the orientation of the graph, optionally
     */
    public Optional<Orientation> getOrientation() {
        return Optional.ofNullable(orientation);
    }

    /**
     * @return the looping policy of the graph, optionally
     */
    public Optional<Looping> getLooping() {
        return Optional.ofNullable(looping);
    }

    /**
     * @return the edge-multiplicity of the graph, optionally
     */
    public Optional<Multiplicity> getMultiplicity() {
        return Optional.ofNullable(multiplicity);
    }

    /**
     * The orientation of a graph
     */
    public enum Orientation {
        /**
         * A directed graph is one whose edges have definite source and target vertices
         */
        DIRECTED,

        /**
         * An undirected graph is one whose edges have no source or target vertices - it merely connects the two
         */
        UNDIRECTED
    }

    /**
     * The looping policy of a graph
     */
    public enum Looping {
        /**
         * Edges are permitted to the same vertex
         */
        PERMITTED,

        /**
         * The edges must connect two distinct vertices
         */
        FORBIDDEN
    }

    /**
     * The edge-multiplicity of a graph
     */
    public enum Multiplicity {
        /**
         * A maximum of one edge may exist between any two vertices
         */
        SINGLE,

        /**
         * Any number of edges may exist between two vertices
         */
        MULTIPLE
    }
}
