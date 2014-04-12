package genBot2;

public class IllegalIngredientNameException extends IllegalIngredientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalIngredientNameException() {
		super();
	}

	public IllegalIngredientNameException(String s) {
		super(s);
	}

	public IllegalIngredientNameException(Throwable cause) {
		super(cause);
	}

	public IllegalIngredientNameException(String message, Throwable cause) {
		super(message, cause);
	}

}
