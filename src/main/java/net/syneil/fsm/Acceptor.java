package net.syneil.fsm;

import java.util.stream.Stream;

/**
 * An acceptor is a specialised {@link Classifier} that reports only if a sequence of inputs is accepted or not. A
 * sequence is accepted only if every element is accepted and when there are no more elements, the machine is in an end
 * state.
 *
 * @param <S> the type used for states
 * @param <T> the type used for inputs
 */
public interface Acceptor<S, T> extends Classifier<S, T, Boolean> {

    /**
     * Accepts all inputs from the stream, if possible, and tests if the resultant state of the machine is an end state.
     *
     * @param inputs a stream of symbols to input
     * @return true if the result of accepting all of the input symbols is for this machine to be in an end state, false
     *         otherwise
     * @throws NoTransitionForSymbolException if the machine cannot accept one of the inputs
     */
    @Override
    Boolean acceptAll(Stream<T> inputs);
}
