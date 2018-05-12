package people;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

import cart.Cart;
import exceptions.InvalidUserException;
import store.Store;

public abstract class User implements IUser {
	private static final int MIN_LENGTH = 0;
	// Define the BCrypt workload to use when generating password hashes. 10-31 is a
	// valid value.
	private static int workload = 12;

	private transient static Store store;

	private String name;
	private LocalDate birthdate;
	private String username;
	private String email;
	private String password;
	private String phoneNumber;
	private boolean isLogged;

	private boolean correctName;
	private boolean correctBirthday;
	private boolean correctEmail;
	private boolean correctPhoneNumber;
	private boolean correctPassword;
	private boolean correctUsername;

	public void setName(String name) throws InvalidUserException {
		if (name != null && name.trim().length() > MIN_LENGTH) {
			this.name = name;
			this.correctName = true;
		} else {
			throw new InvalidUserException("No name.");
		}
	}

	public void setBirthdate(int day, int month, int year) throws InvalidUserException {
		try {
			LocalDate date = LocalDate.of(year, month, day);
			this.birthdate = date;
			this.correctBirthday = true;
		} catch (DateTimeException e) {
			throw new InvalidUserException("Invalid birthdate.");
		}
	}

	public void setPhoneNumber(String phoneNumber) throws InvalidUserException {
		if (phoneNumber == null || phoneNumber.trim().length() == MIN_LENGTH) {
			throw new InvalidUserException("Empty phone number.");
		} else {
			if (isValidPhone(phoneNumber)) {
				this.phoneNumber = phoneNumber;
				this.correctPhoneNumber = true;
			} else {
				throw new InvalidUserException("Invalid phone number.");
			}
		}
	}

	public void setEmail(String email) throws InvalidUserException {
		if (isValidEmailAddress(email)) {
			this.email = email;
			this.correctEmail = true;
		} else {
			throw new InvalidUserException("Invalid e-mail.");
		}
	}

	@Override
	public void createPassword(String password) throws InvalidUserException {
		if (isValidPassword(password)) {
			this.password = hashPassword(password);
			this.correctPassword = true;
		} else {
			throw new InvalidUserException("Your password is not strong enought!");
		}
	}

	/**
	 * This method can be used to generate a string representing an account password
	 * suitable for storing in a database. It will be an OpenBSD-style crypt(3)
	 * formatted hash string of length=60 The bcrypt workload is specified in the
	 * above static variable, a value from 10 to 31. A workload of 12 is a very
	 * reasonable safe default as of 2013. This automatically handles secure 128-bit
	 * salt generation and storage within the hash.
	 * 
	 * @param password_plaintext
	 *            The account's plaintext password as provided during account
	 *            creation, or when changing an account's password.
	 * @return String - a string of length 60 that is the bcrypt hashed password in
	 *         crypt(3) format.
	 */
	private String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);
		return (hashed_password);
	}

	/**
	 * This method can be used to verify a computed hash from a plaintext (e.g.
	 * during a login request) with that of a stored hash from a database. The
	 * password hash from the database must be passed as the second variable.
	 * 
	 * @param password_plaintext
	 *            The account's plaintext password, as provided during a login
	 *            request
	 * @param stored_hash
	 *            The account's stored password hash, retrieved from the
	 *            authorization database
	 * @return boolean - true if the password matches the password of the stored
	 *         hash, false otherwise
	 */
	public static boolean checkPassword(String password_plaintext, String stored_hash) throws InvalidUserException {
		boolean password_verified = false;
		if (stored_hash == null || !stored_hash.startsWith("$2a$")) {
			throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
		}
		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

		if (password_verified) {
			return (password_verified);
		} else {
			throw new InvalidUserException("Wrong password.");
		}
	}

	public static void setStore(Store store) throws InvalidUserException {
		if (User.store == null && store != null) {
			User.store = store;
			Cart.setStore(store);
		} else {
			throw new InvalidUserException("Invalid store given or this user already has a store.");
		}
	}

	public void setUsername(String username) throws InvalidUserException {
		if (this.username == null && username != null) {
			this.username = username;
			this.correctUsername = true;
		} else {
			throw new InvalidUserException("Empty username.");
		}
	}

	/**
	 * At least 8 chars Contains at least one digit Contains at least one lower
	 * alpha char and one upper alpha char Contains at least one char within a set
	 * of special chars (@#%$^ etc.) Does not contain space, tab, etc.
	 *
	 * Explanations:
	 * 
	 * (?=.*[0-9]) a digit must occur at least once (?=.*[a-z]) a lower case letter
	 * must occur at least once (?=.*[A-Z]) an upper case letter must occur at least
	 * once (?=.*[@#$%^&+=]) a special character must occur at least once (?=\\S+$)
	 * no whitespace allowed in the entire string .{8,} at least 8 characters
	 * 
	 * @param password
	 * @return
	 */
	private boolean isValidPassword(String password) {
		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
		return password.matches(pattern);
	}

	private boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	private boolean isValidPhone(String phone) {
		if (phone != null && phone.trim().length() == 10) {
			if (phone.startsWith("08") && (phone.matches("^[0-9]{10}$"))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void logOut() throws InvalidUserException {
		if (this.isLogged) {
			this.isLogged = false;
			System.out.println("You've successfully logged out");
			try {
				User.store.updateTheKiflaJsonFile();
			} catch (IOException e) {
				System.out.println("The update of Kifla.json was unsuccessfull!");
			}
		} else {
			throw new InvalidUserException("You can't log out if you're not logged in.");
		}
	}

	@Override
	public void seeEmail() {
		System.out.println(this.email);
	}

	public void setLogged() throws InvalidUserException {
		this.isLogged = true;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", birthdate=" + birthdate + ", username=" + username + ", email=" + email
				+ ", password=" + password + ", phoneNumber=" + phoneNumber + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public String getName() {
		return this.name;
	}

	public boolean isLogged() {
		return this.isLogged;
	}

	public static Store getStore() {
		return User.store;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isBuyer() {
		return false;
	}

	public boolean isCorrectBirthday() {
		return this.correctBirthday;
	}

	public boolean isCorrectEmail() {
		return this.correctEmail;
	}

	public boolean isCorrectName() {
		return this.correctName;
	}

	public boolean isCorrectPassword() {
		return this.correctPassword;
	}

	public boolean isCorrectPhoneNumber() {
		return this.correctPhoneNumber;
	}

	public boolean isCorrectUsername() {
		return this.correctUsername;
	}

}
