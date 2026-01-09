package main;

/**
 * Aruncată atunci când scenariile demo scriptate nu pot fi executate.
 */
public class ScenarioExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ScenarioExecutionException(String message) {
		super(message);
	}

	public ScenarioExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
