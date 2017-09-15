package net.syneil.graphs.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.syneil.graphs.EdgeImpl;
import net.syneil.graphs.Graph;
import net.syneil.graphs.GraphImpl;
import net.syneil.graphs.GraphView;

/**
 * A graph that maintains sets of criteria for changes that will throw exceptions when violated. The graph may be put
 * into an "edit" mode which will allow the consumer to make multiple changes to the graph before testing its validity.
 *
 * @param <V> type used for vertices
 * @param <E> type used for edge labels
 */
public class ConstrainedGraph<V, E> extends GraphImpl<V, E> {

    /** Contains the state of the graph when edit mode was entered, for rolling back */
    private Map<V, Set<Edge<V, E>>> rollbackState;

    /** Contains the constraints defined for this graph at the time edit mode was entered */
    private Set<GraphConstraint<V, E>> rollbackConstraints;

    /** The constraints that must be satisfied by this graph */
    private final Set<GraphConstraint<V, E>> constraints = new HashSet<>();

    /**
     * Creates a graph with constraints appropriate for the orientation and multiplicity given.
     *
     * @param orientation the orientation of the graph
     * @param multiplicity the allowed edge multiplicity
     */
    public ConstrainedGraph(final Orientation orientation, final Multiplicity multiplicity) {
        this.constraints.add(orientation.constraint());
        this.constraints.add(multiplicity.constraint());
    }

    /**
     * Defines a new constraint on this graph
     *
     * @param constraint the new constraint
     */
    public void addConstraint(final GraphConstraint<V, E> constraint) {
        this.constraints.add(constraint);
    }

    /**
     * @return true if this graph is in edit mode, false otherwise
     */
    public final boolean isInEditMode() {
        return this.rollbackState != null;
    }

    /**
     * Puts this graph into edit mode. If this graph is already in edit mode, calling this method has no effect. The
     * state of this graph is maintained and can be retrieved via {@link #rollback()}. Exit edit mode with
     * {@link #endEditMode()}.
     */
    public final void startEditMode() {
        if (!isInEditMode()) {
            this.rollbackState = super.getData();
            this.rollbackConstraints = new HashSet<>(this.constraints);
        }
    }

    /**
     * Takes this graph out of edit mode. If this graph is not in edit mode, calling this method has no effect. The
     * defined constraints must be satisfied by the current state of this graph, otherwise a
     * {@link ConstraintViolationException} is thrown and this graph remains in edit mode.
     */
    public final void endEditMode() {
        if (!isInEditMode()) {
            return;
        }

        // check all constraints
        verifyConstraints(this, this.constraints);

        this.rollbackState = null;
        this.rollbackConstraints = null;
    }

    /**
     * Verifies that this graph meets the constraints that have been defined against it. If any constraints are
     * violated, a {@link ConstraintViolationException} will be thrown.
     */
    public final void verify() {
        verifyConstraints(this, this.constraints);
    }

    /**
     * Returns this graph to its state when edit mode was entered and exits edit mode. If this graph is not in edit
     * mode, calling this method has no effect. Note that constraints added while in edit mode will also be reverted.
     */
    public final void rollback() {
        if (isInEditMode()) {
            super.overwriteData(this.rollbackState);
            this.constraints.retainAll(this.rollbackConstraints);
            this.constraints.addAll(this.rollbackConstraints);
            this.rollbackState = null;
            this.rollbackConstraints = null;
        }
    }

    /* Override graph mutating methods to enforce constraints */

    @Override
    public boolean addVertex(final V vertex) {
        if (!isInEditMode()) {
            final GraphView<V, E> view = GraphView.ofVertices(this, sv -> Stream.concat(sv, Stream.of(vertex)));
            verifyConstraints(view, this.constraints);
        }

        return super.addVertex(vertex);
    }

    @Override
    public boolean removeVertex(final V vertex) {
        if (!isInEditMode()) {
            final GraphView<V, E> view = new GraphView<>(this, sv -> sv.filter(v -> !Objects.equals(v, vertex)),
                    se -> se.filter(e -> !Objects.equals(e.getSource(), vertex)
                            && !Objects.equals(e.getTarget(), vertex)));
            verifyConstraints(view, this.constraints);
        }

        return super.removeVertex(vertex);
    }

    @Override
    public boolean addEdge(final V source, final E label, final V target, final boolean directed) {
        if (!isInEditMode()) {
            final GraphView<V, E> view = GraphView
                    .ofEdges(this,
                             se -> Stream.concat(se, Stream.of(new EdgeImpl<V, E>(source, directed, label, target))));
            verifyConstraints(view, this.constraints);
        }
        return super.addEdge(source, label, target, directed);
    }

    @Override
    public boolean removeEdges(final V source, final V target) {
        if (!isInEditMode()) {
            final GraphView<V, E> view = GraphView.ofEdges(this, se -> se
                    .filter(e -> !Objects.equals(e.getSource(), source) && !Objects.equals(e.getTarget(), target)));
            verifyConstraints(view, this.constraints);
        }

        return super.removeEdges(source, target);
    }

    /**
     * Tests a graph against a collection of constraints. If any constraints are violated, a
     * {@link ConstraintsViolatedException} will be thrown with details of each violation.
     *
     * @param graph the graph to test
     * @param constraints the constraints to test with
     */
    private static <V, E> void verifyConstraints(final Graph<V, E> graph,
            final Collection<GraphConstraint<V, E>> constraints) {
        final Set<ConstraintViolationException> violations = constraints.stream().filter(c -> !c.test(graph))
                .map(c -> new ConstraintViolationException(
                        String.format("%s violation: %s", c.getName(), c.getViolationMessage())))
                .collect(Collectors.toSet());
        if (!violations.isEmpty()) {
            throw new ConstraintsViolatedException(violations);
        }
    }
}
