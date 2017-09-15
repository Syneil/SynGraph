package net.syneil.graphs.constraints;

/**
 * A graph constraint violation
 */
public class ConstraintViolationException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = -4178640241843068518L;

    /**
     * @param message the message to attach to this exception
     */
    public ConstraintViolationException(final String message) {
        super(message);
    }
}
