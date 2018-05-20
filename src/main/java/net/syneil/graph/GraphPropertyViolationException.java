package net.syneil.graph;

/**
 * Thrown when a mutating operation is made on a graph that would violate the restrictions defined at its creation.
 */
public class GraphPropertyViolationException extends RuntimeException {
    /**
     * Constructs a new graph property violation exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public GraphPropertyViolationException(String message) {
        super(message);
    }
}
