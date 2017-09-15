package net.syneil.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utility class for manipulating streams
 */
public final class StreamUtils {

    /**
     * Creates a view of every pairing of the elements in the collection, including identical pairings.
     * <p>
     * For example, a collection {@code (a,b,c)} yields a stream:<br/>
     * {@code ([a,a],[a,b],[a,c],[b,a],[b,b],[b,c],[c,a],[c,b],[c,c])}
     *
     * @param source the collection
     * @return the cross-product of the elements of the collection, as a stream of 2-arrays.
     */
    public static <T> Stream<T[]> crossProduct(final Collection<T> source) {
        return crossProduct(source::stream, source::stream);
    }

    /**
     * Creates a stream of every pairing between the elements of each stream.
     * <p>
     * For example, collections {@code (a,b,c)} and {@code (y,z)} yield a stream:<br/>
     * {@code ([a,y],[a,z],[b,y],[b,z],[c,y],[c,z])}
     *
     * @param rows one of the sources of elements
     * @param columns one of the sources of elements
     * @return the cross-product of the elements of the two streams
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T[]> crossProduct(final Supplier<Stream<T>> rows, final Supplier<Stream<T>> columns) {
        final Stream.Builder<T[]> builder = Stream.builder();
        rows.get().forEach(s -> columns.get().map(t -> (T[]) Arrays.asList(s, t).toArray()).forEach(builder::accept));
        return builder.build();
    }

    /** Hidden constructor */
    private StreamUtils() {
    }
}
