package net.syneil.graphs;

import net.syneil.graphs.Graph.Edge;
import net.syneil.graphs.constraints.ConstrainedGraph;
import net.syneil.graphs.constraints.Multiplicity;
import net.syneil.graphs.constraints.Orientation;

/**
 * Convenience class for weighted graphs
 *
 * @param <V> the type used for vertices
 * @param <N> the number type used for edge weights
 */
public class WeightedGraph<V, N extends Number> extends ConstrainedGraph<V, Edge<V, N>> {

    /**
     * Constructs a new empty graph with predefined orientation and multiplicity constraints
     *
     * @param orientation the orientation of the edges
     * @param multiplicity the multiplicity of the edges
     */
    public WeightedGraph(final Orientation orientation, final Multiplicity multiplicity) {
        super(orientation, multiplicity);
    }

}
