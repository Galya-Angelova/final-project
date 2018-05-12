package exceptions;

public class BuyerRepositoryException extends Exception {

	private static final long serialVersionUID = 2706014205495338742L;

	public BuyerRepositoryException() {
		super();
	}

	public BuyerRepositoryException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BuyerRepositoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BuyerRepositoryException(String arg0) {
		super(arg0);
	}

	public BuyerRepositoryException(Throwable arg0) {
		super(arg0);
	}
}
