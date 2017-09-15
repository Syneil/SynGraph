package net.syneil.func;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utility class for making functions
 */
public class Functions {
    /**
     * @return an equivalent of {@link UnaryOperator#identity} when a function is not explicitly called out as a unary
     *         operator
     */
    public static <A> Function<A, A> identity() {
        return input -> input;
    }

    /**
     * @param f an integer unary operator
     * @return the same operator as a {@link Function}
     */
    public static Function<Integer, Integer> asFunction(final IntUnaryOperator f) {
        return f::applyAsInt;
    }

    /**
     * @param f a double unary operator
     * @return the same operator as a {@link Function}
     */
    public static Function<Double, Double> asFunction(final DoubleUnaryOperator f) {
        return f::applyAsDouble;
    }

    /**
     * @param f a long unary operator
     * @return the same operator as a {@link Function}
     */
    public static Function<Long, Long> asFunction(final LongUnaryOperator f) {
        return f::applyAsLong;
    }

    /**
     * @param p a predicate
     * @param q a predicate
     * @return a predicate that evaluates {@code p XOR q}
     */
    public static <A> Predicate<A> xor(final Predicate<A> p, final Predicate<A> q) {
        return input -> p.test(input) ^ q.test(input);
    }

    /**
     * Hidden constructor
     */
    private Functions() {
    }
}
