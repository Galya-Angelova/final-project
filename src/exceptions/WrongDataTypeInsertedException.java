package exceptions;

public class WrongDataTypeInsertedException extends Exception {

	private static final long serialVersionUID = 5913839847621362363L;

	public WrongDataTypeInsertedException() {
	}

	public WrongDataTypeInsertedException(String arg0) {
		super(arg0);
	}

	public WrongDataTypeInsertedException(Throwable cause) {
		super(cause);
	}

	public WrongDataTypeInsertedException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongDataTypeInsertedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
