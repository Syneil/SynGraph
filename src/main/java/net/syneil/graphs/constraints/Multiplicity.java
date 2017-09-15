package net.syneil.graphs.constraints;

/**
 * The different multiplicities of a graph. A multiplicity describes whether or not two vertices (A,B) may have
 * more than one edge between them. A {@link Multiplicity#SIMPLE simple} graph may have only one edge, whereas a
 * {@link Multiplicity#MULTI multi} graph can have any number.
 */
public enum Multiplicity {
    /** There can only be one edge (or none) between any given two nodes */
    SIMPLE {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return new GraphConstraint<>("Simple graph",
                    g -> g.edges().noneMatch(e -> g.getEdges(e.getSource(), e.getTarget()).size() > 1),
                    "Simple graphs may not have multiple edges between two vertices");
        }
    },
    /** There may be any number of edges between any given two nodes */
    MULTI {
        @Override
        <V, E> GraphConstraint<V, E> constraint() {
            return GraphConstraint.unconstrained();
        }
    };

    /**
     * @return a constraint on graphs with this multiplicity
     */
    abstract <V, E> GraphConstraint<V, E> constraint();
}