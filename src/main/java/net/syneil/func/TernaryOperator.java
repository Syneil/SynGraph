package net.syneil.func;

/**
 * A {@link FunctionalInterface} accepting three parameters (of identical, or common, type) and returning something of
 * the same type.
 *
 * @param <T> the type
 */
@FunctionalInterface
public interface TernaryOperator<T> {
    /**
     * @param a the first operand
     * @param b the second operand
     * @param c the third operand
     * @return the result of operating on the three operands
     */
    abstract T apply(T a, T b, T c);
}
