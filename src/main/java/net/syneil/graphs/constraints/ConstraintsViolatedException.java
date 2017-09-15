package net.syneil.graphs.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An exception encapsulating any number of graph constraint violations
 */
public class ConstraintsViolatedException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = -1368275402033921127L;

    /** The distinct constraints that we violated */
    private final List<ConstraintViolationException> causes;

    /**
     * @param causes the constraints that were violated
     */
    public ConstraintsViolatedException(final Collection<ConstraintViolationException> causes) {
        super(causes.toString());
        this.causes = new ArrayList<>(causes);
    }

    /**
     * @param causes the constraints that were violated
     * @param message the message to attach to this exception
     */
    public ConstraintsViolatedException(final Collection<ConstraintViolationException> causes, final String message) {
        super(message);
        this.causes = new ArrayList<>(causes);
    }

    /**
     * @return the distinct constraint violations
     */
    public List<ConstraintViolationException> getCauses() {
        return this.causes;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + this.causes;
    }
}
