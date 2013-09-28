package genBot2;

public class FitnessNotSetException extends Exception {

	public FitnessNotSetException() {
		super();
	}

	public FitnessNotSetException(String message) {
		super(message);
	}

	public FitnessNotSetException(Throwable cause) {
		super(cause);
	}

	public FitnessNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public FitnessNotSetException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
