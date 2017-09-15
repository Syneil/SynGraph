package net.syneil.graphs.constraints;

/**
 * A graph pre-loaded with the {@link Orientation#ORIENTED} and {@link Multiplicity#SIMPLE} constraints
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 */
public class OrientedGraph<V, E> extends ConstrainedGraph<V, E> {
    /**
     * Constructor for the empty oriented graph
     */
    public OrientedGraph() {
        super(Orientation.ORIENTED, Multiplicity.SIMPLE);
    }
}
