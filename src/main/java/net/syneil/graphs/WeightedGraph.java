package net.syneil.graphs;

import net.syneil.graphs.Graph.Edge;
import net.syneil.graphs.constraints.ConstrainedGraph;
import net.syneil.graphs.constraints.Multiplicity;
import net.syneil.graphs.constraints.Orientation;

public class WeightedGraph<V, N extends Number> extends ConstrainedGraph<V, Edge<V, N>> {

    public WeightedGraph(final Orientation orientation, final Multiplicity multiplicity) {
        super(orientation, multiplicity);
    }

}
