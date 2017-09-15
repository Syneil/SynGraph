package net.syneil.fsm;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import net.syneil.graphs.Graph;
import net.syneil.graphs.constraints.Multiplicity;
import net.syneil.graphs.constraints.Orientation;
import net.syneil.graphs.generation.GraphGenerator;

/**
 * A finite state machine is a graph with states for vertices and transitions for edges. One state is the start state,
 * and any number of states may be end states. One symbol is accepted into the FSM at a time and if there are no
 * transitions from this state then a {@link NoTransitionForSymbolException} will be thrown.
 *
 * @param <S> the type used for states
 * @param <T> the type used for inputs (the symbols)
 * @param <U> the type used for outputs
 */
public interface FSM<S, T, U> extends Transducer<S, T, U> {

    /**
     * Accepts all inputs from the stream, if possible, and tests if the resultant state of the machine is an end state.
     *
     * @param inputs a stream of symbols to input
     * @return true if the result of accepting all of the input symbols is for this machine to be in an end state, false
     *         otherwise
     * @throws NoTransitionForSymbolException if the machine cannot accept one of the inputs
     */
    @Override
    default Boolean acceptAll(final Stream<T> inputs) {
        inputs.forEach(this::accept);
        return isInEndState();
    }

    /**
     * Default builder for an FSM, facilitating adding states and transitions.
     *
     * @param <S> the type used for states
     * @param <T> the type used for inputs (symbols)
     * @param <U> the type used for outputs
     */
    class Builder<S, T, U> {
        /** The internal representation of the FSM, as a graph */
        private final Graph<S, Function<T, U>> machine = new GraphGenerator<S, Function<T, U>>(Orientation.DIRECTED,
                Multiplicity.MULTI).generate();

        /** The unique state to identify as the start state */
        private final S startState;

        /** The possibly empty set of end states */
        private final Set<S> endStates = new HashSet<>();

        /** What to do with the outputs */
        private Consumer<U> output = System.out::println;

        /**
         * Construct a new builder by defining the start state
         *
         * @param startState the start state for the graph being built
         * @param alsoEndState true if the start state is also an end state, false otherwise
         */
        public Builder(final S startState, final boolean alsoEndState) {
            this.startState = startState;
            addState(startState, alsoEndState);
        }

        /**
         * Defines how to consume the outputs of transitions
         *
         * @param output what to do with the outputs
         * @return this builder for chained operations
         */
        public Builder<S, T, U> setOutput(final Consumer<U> output) {
            this.output = output;
            return this;
        }

        /**
         * Adds a new state to the machine, declaring whether or not it should added to the set of end states.
         *
         * @param state the state to add
         * @param isEndState true if the state is an end state, or false if it is not
         * @return this builder for chained operations
         */
        public Builder<S, T, U> addState(final S state, final boolean isEndState) {
            this.machine.addVertex(state);
            if (isEndState) {
                this.endStates.add(state);
            }
            return this;
        }

        /**
         * Adds a transition from the source to the target for any input symbol that matches the predicate. The states
         * are added to the machine if they were not already.
         *
         * @param source the source state
         * @param condition the condition on which to follow this transition
         * @param target the target state
         * @return this builder for chained operations
         */
        public Builder<S, T, U> addTransition(final S source, final Function<T, U> condition, final S target) {
            this.machine.addEdge(source, condition, target, true);
            return this;
        }

        /**
         * Adds a transition from the source to the target for the given input symbol. The states are added to the
         * machine if they were not already.
         *
         * @param source the source state
         * @param input the input symbol on which to follow this transition
         * @param output what to do with the outputs
         * @param target the target state
         * @return this builder for chained operations
         */
        public Builder<S, T, U> addTransition(final S source, final T input, final U output, final S target) {
            addTransition(source, t -> Objects.equals(t, input) ? output : null, target);
            return this;
        }

        /**
         * @return an FSM implementation configured according to this builder
         */
        public FSM<S, T, U> build() {
            return new Impl<>(machine, startState, endStates, output);
        }
    }

    /**
     * A default implementation of the FSM interface that can only be constructed by the default builder.
     *
     * @param <S> the type used for states
     * @param <T> the type used for inputs (symbols)
     * @param <U> the type used for outputs
     */
    class Impl<S, T, U> implements FSM<S, T, U> {
        /** The internal representation of this FSM as a graph */
        private final Graph<S, Function<T, U>> machine;

        /** The unique start state */
        private final S startState;

        /** The possible empty set of end states */
        private final Set<S> endStates;

        /** What to do with the outputs */
        private final Consumer<U> output;

        /** The current state of this FSM */
        private S currentState;

        /**
         * Constructor for the default builder.
         *
         * @param machine the internal representation of the FSM as a graph
         * @param startState the unique start state
         * @param endStates the possibly empty set of end states
         * @param output what to do with the outputs
         */
        private Impl(final Graph<S, Function<T, U>> machine, final S startState, final Set<S> endStates,
                final Consumer<U> output) {
            this.machine = machine;
            this.startState = startState;
            this.endStates = endStates;
            this.currentState = startState;
            this.output = output;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void accept(final T input) {
            this.currentState = this.machine.getEdges(currentState).stream().map(e -> new Object[] {
                    e.getLabel().get().apply(input), e.getTarget()
            }).filter(arr -> arr[0] != null).peek(arr -> this.output.accept((U) arr[0])).map(arr -> (S) arr[1])
                    .findAny().orElseThrow(NoTransitionForSymbolException::new);
        }

        @Override
        public S currentState() {
            return this.currentState;
        }

        @Override
        public boolean isInStartState() {
            return Objects.equals(this.startState, this.currentState);
        }

        @Override
        public boolean isInEndState() {
            return this.endStates.contains(this.currentState);
        }

        @Override
        public void reset() {
            this.currentState = this.startState;
        }
    }
}
