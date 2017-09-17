package net.syneil.fsm;

/**
 * A transducer is a type of {@link FSM finite-state machine} that also produces output as state transitions are made
 * while processing input symbols.
 *
 * @param <S> the type used for states
 * @param <T> the type used for inputs
 * @param <U> the type used for outputs
 */
public interface Transducer<S, T, U> extends Classifier<T, Boolean>, Acceptor<T> {

    /**
     * Applies one input symbol to this finite state machine
     *
     * @param input the symbol to input
     * @throws NoTransitionForSymbolException if the machine cannot accept the input in its current state
     */
    void accept(T input);

    /**
     * @return the current state of this finite state machine
     */
    S currentState();

    /**
     * @return true if this finite state machine's current state is a start state, false otherwise
     */
    boolean isInStartState();

    /**
     * @return true if this finite state machine's current state is an end state, false otherwise
     */
    boolean isInEndState();

    /** Resets this FSM to its starting state */
    void reset();
}
