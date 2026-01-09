package main;

/**
 * Aruncată atunci când se încearcă plasarea unei piese pe o poziție ocupată sau invalidă a tablei.
 */
public class PositionOccupiedException extends Exception {
    private static final long serialVersionUID = 1L;

    public PositionOccupiedException() {
        super();
    }

    public PositionOccupiedException(String message) {
        super(message);
    }

    public PositionOccupiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositionOccupiedException(Throwable cause) {
        super(cause);
    }
}
