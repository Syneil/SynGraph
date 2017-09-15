package net.syneil.graphs.constraints;

import net.syneil.graphs.Graph.Edge;

/**
 * The different orientations of graphs. An orientation describes if the edges must ({@link Orientation#DIRECTED
 * directed}), must not ({@link Orientation#UNDIRECTED undirected}) or may ({@link Orientation#MIXED mixed}) have
 * directions. A graph may also constrain edges such that a pair of nodes may not have edges going in both
 * directions simultaneously ({@link Orientation#ORIENTED oriented}).
 */
public enum Orientation {
    /** Edges have no direction */
    UNDIRECTED {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return new GraphConstraint<>("Undirected edges", g -> g.edges().noneMatch(Edge::isDirected),
                    "Undirected graphs may not have directed edges");
        }
    },
    /** Edges have source and target nodes */
    DIRECTED {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return new GraphConstraint<>("Directed edges", g -> g.edges().allMatch(Edge::isDirected),
                    "Directed graphs may not have undirected edges");
        }
    },
    /** Edges are directed and a pair of nodes (A,B) may have edges (A&rarr;B) XOR (A&larr;B) */
    ORIENTED {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return new GraphConstraint<>("Oriented edges",
                    g -> g.edges().noneMatch(e -> !e.isDirected() || g.hasEdge(e.getTarget(), e.getSource())),
                    "Oriented graphs may only have edges in one direction between any two vertices");
        }
    },
    /** Edges may be either directed or undirected */
    MIXED {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return GraphConstraint.unconstrained();
        }
    };

    /**
     * @return a constraint on graphs with this orientation
     */
    abstract <V, E> GraphConstraint<V, E> constraint();
}