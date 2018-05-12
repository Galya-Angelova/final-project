package people;

import exceptions.InvalidUserException;

public interface IUser {
	void logOut() throws InvalidUserException;

	void seeEmail();
	
	void createPassword(String password) throws InvalidUserException;
}
