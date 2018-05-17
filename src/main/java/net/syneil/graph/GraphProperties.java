package net.syneil.graph;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Definition of the core properties of a graph implementation.
 * <p>
 * The {@code net.syneil.graph} package contains the following:
 * <pre>
 *                                  Orientation Looping   Multiplicity Labelling  Expansion    Backing
 * .adjlist.BasicListGraph          DIRECTED    PERMITTED SINGLE       UNLABELLED UNRESTRICTED ADJ_LIST
 * .adjmatrix.BasicMatrixGraph      DIRECTED    PERMITTED SINGLE       UNLABELLED LIMITED      ADJ_MATRIX
 * .adjmatrix.LabelledMatrixGraph   DIRECTED    PERMITTED SINGLE       LABELLED   LIMITED      ADJ_MATRIX
 * .adjmatrix.UndirectedMatrixGraph UNDIRECTED  PERMITTED SINGLE       UNLABELLED LIMITED      ADJ_MATRIX
 * .nodes.NodeGraph                 DIRECTED    PERMITTED SINGLE       LABELLED   UNRESTRICTED NODE_SET
 * </pre>
 *
 * @see Orientation
 * @see Looping
 * @see Multiplicity
 * @see Labelling
 * @see Expansion
 * @see Backing
 */
@Getter
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
     * The edge label policy of the graph
     */
    private final Labelling labelling;

    /**
     * The expansibility of the graph
     */
    private final Expansion expansion;

    /**
     * The underlying implementation of the graph structure
     */
    private final Backing backing;

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

    /**
     * The edge label policy of a graph
     */
    public enum Labelling {
        /**
         * The edges carry labels
         */
        LABELLED,

        /**
         * The edges do not carry labels
         */
        UNLABELLED
    }

    /**
     * The expansibility of a graph
     */
    public enum Expansion {
        /**
         * The graph may only contain a limited number of vertices
         */
        LIMITED,

        /**
         * There is no restriction on the number of vertices
         */
        UNRESTRICTED
    }

    /**
     * The underlying implementation of a graph structure
     */
    public enum Backing {
        /**
         * The graph is backed by an adjacency list
         */
        ADJ_LIST,

        /**
         * The graph is backed by an adjacency matrix
         */
        ADJ_MATRIX,

        /**
         * The graph is backed by a set of nodes
         */
        NODE_SET
    }
}
