package net.syneil.fsm;

/**
 * Thrown when a symbol cannot be accepted by a {@link FSM finite-state machine}
 */
public class NoTransitionForSymbolException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = -2859843126076663582L;

    /**
     * Basic constructor
     */
    public NoTransitionForSymbolException() {
        super();
    }
}
