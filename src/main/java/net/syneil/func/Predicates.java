package net.syneil.func;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Utility class for common predicates
 */
public final class Predicates {
    /**
     * Produces a predicate that passes only when the operand is one of a finite set of options
     *
     * @param options the objects for which the predicate should pass
     * @return a predicate that passes only for a finite set of options
     */
    public static <T> Predicate<T> isOneOf(final Collection<T> options) {
        return options::contains;
    }

    /**
     * Produces a predicate that passes only when the operand is one of a finite set of options
     *
     * @param options the objects for which the predicate should pass
     * @return a predicate that passes only for a finite set of options
     */
    public static <T> Predicate<T> isOneOf(final T... options) {
        return isOneOf(Arrays.asList(options));
    }

    /**
     * Produces a predicate that fails only when the operand is one of a finite set of options
     *
     * @param options the objects for which the predicate should fail
     * @return a predicate that fails only for a finite set of options
     */
    public static <T> Predicate<T> isNotOneOf(final Collection<T> options) {
        return t -> !options.contains(t);
    }

    /**
     * Produces a predicate that fails only when the operand is one of a finite set of options
     *
     * @param options the objects for which the predicate should fail
     * @return a predicate that fails only for a finite set of options
     */
    public static <T> Predicate<T> isNotOneOf(final T... options) {
        return isNotOneOf(Arrays.asList(options));
    }

    /**
     * hidden constructor
     */
    private Predicates() {
    }
}
