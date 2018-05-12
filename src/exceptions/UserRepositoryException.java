package exceptions;

public class UserRepositoryException extends Exception {

	private static final long serialVersionUID = 2706014205495338742L;

	public UserRepositoryException() {
		super();
	}

	public UserRepositoryException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public UserRepositoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UserRepositoryException(String arg0) {
		super(arg0);
	}

	public UserRepositoryException(Throwable arg0) {
		super(arg0);
	}
}
