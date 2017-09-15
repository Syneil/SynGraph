package net.syneil.graphs.constraints;

import java.util.function.Predicate;

import net.syneil.graphs.Graph;

/**
 * Defines a constraint on a graph
 *
 * @param <V> the type of vertices in the graph
 * @param <E> the type of edges in the graph
 */
public class GraphConstraint<V, E> implements Predicate<Graph<V, E>> {

    /** The constraint that must pass */
    private final Predicate<Graph<V, E>> constraint;

    /** The name of this constraint, for readability */
    private final String name;

    /** The message to include in exceptions when this constraint is violated */
    private final String violationMessage;

    /**
     * @param name the name of the constraint
     * @param constraint the predicate
     * @param violationMessage the exception message
     */
    public GraphConstraint(final String name, final Predicate<Graph<V, E>> constraint, final String violationMessage) {
        this.name = name;
        this.constraint = constraint;
        this.violationMessage = violationMessage;
    }

    @Override
    public boolean test(final Graph<V, E> graph) {
        return this.constraint.test(graph);
    }

    /**
     * @return the name of this constraint
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the message to include in exceptions when this constraint is violated
     */
    public String getViolationMessage() {
        return this.violationMessage;
    }

    /**
     * @return a constraint that cannot be violated
     */
    public static <V, E> GraphConstraint<V, E> unconstrained() {
        return new GraphConstraint<>("Unconstrained", g -> true, "This constraint cannot be violated");
    }
}