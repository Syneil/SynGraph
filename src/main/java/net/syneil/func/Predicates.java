package net.syneil.func;

import java.util.Arrays;
import java.util.function.Predicate;

public class Predicates {
    public static <T> Predicate<T> isOneOf(final T... options) {
        return t -> Arrays.asList(options).contains(t);
    }

    public static <T> Predicate<T> isNotOneOf(final T... options) {
        return t -> !Arrays.asList(options).contains(t);
    }
}
