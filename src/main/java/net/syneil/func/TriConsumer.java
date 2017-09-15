package net.syneil.func;

/**
 * A {@link FunctionalInterface} accepting three inputs (of potentially different types), returning nothing.
 *
 * @param <A> the type of the first operand
 * @param <B> the type of the second operand
 * @param <C> the type of the third operand
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
    /**
     * @param a the first operand
     * @param b the second operand
     * @param c the third operand
     */
    void offer(A a, B b, C c);
}
