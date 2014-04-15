package genBot;

public class IllegalIngredientException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalIngredientException() {
		super();
	}

	public IllegalIngredientException(String s) {
		super(s);
	}

	public IllegalIngredientException(Throwable cause) {
		super(cause);
	}

	public IllegalIngredientException(String message, Throwable cause) {
		super(message, cause);
	}
}
