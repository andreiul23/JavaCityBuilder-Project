package main;

/**
 * Indică o eroare în timpul salvării sau încărcării unui oraș.
 */
public class SaveLoadException extends Exception {
	private static final long serialVersionUID = 1L;

	public SaveLoadException(String message) {
		super(message);
	}

	public SaveLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
