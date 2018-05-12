package exceptions;

public class InvalidCartException extends Exception{

	private static final long serialVersionUID = 3286313533406904082L;

	public InvalidCartException() {
		super();
	}

	public InvalidCartException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public InvalidCartException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidCartException(String arg0) {
		super(arg0);
	}

	public InvalidCartException(Throwable arg0) {
		super(arg0);
	}

	
}
