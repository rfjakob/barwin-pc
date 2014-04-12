package serialRMI;

public class SerialRMIException extends Exception {

	private static final long serialVersionUID = 1L;

	public SerialRMIException() {
	}

	public SerialRMIException(String message) {
		super(message);
	}

	public SerialRMIException(Throwable cause) {
		super(cause);
	}

	public SerialRMIException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerialRMIException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
