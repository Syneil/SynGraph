package net.syneil.fsm;

import java.util.stream.Stream;

/**
 * A classifier is a specialised {@link FSM finite-state machine} that accepts a sequence of symbols and reports the
 * resultant state.
 *
 * @param <T> the type used for inputs
 * @param <U> the type used for outputs
 */
public interface Classifier<T, U> {
    /**
     * Accepts all inputs from the stream, if possible, and returns an output.
     *
     * @param inputs a stream of symbols to input
     * @return the output
     * @throws NoTransitionForSymbolException if the machine cannot accept one of the inputs
     */
    U acceptAll(Stream<T> inputs);
}
