package net.syneil.func;

/**
 * Functional interface for functions accepting 3 inputs of any type
 *
 * @param <A> type of the first operand
 * @param <B> type of the second operand
 * @param <C> type of the third operand
 * @param <D> type of the output
 */
@FunctionalInterface
public interface TriFunction<A, B, C, D> {
    /**
     * Applies the three operands and returns the output
     *
     * @param a the first operand
     * @param b the second operand
     * @param c the third operand
     * @return the result of applying this function to the three operands
     */
    D apply(A a, B b, C c);
}
