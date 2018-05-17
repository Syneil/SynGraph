package net.syneil.graph;

public class GraphPropertyViolationException extends RuntimeException {
    public GraphPropertyViolationException(String message) {
        super(message);
    }

    public GraphPropertyViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphPropertyViolationException(Throwable cause) {
        super(cause);
    }
}
