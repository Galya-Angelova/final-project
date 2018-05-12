package people;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidProductException;
import exceptions.InvalidUserException;
import exceptions.StoreException;
import exceptions.WrongDataTypeInsertedException;
import product.Product;

public class Admin extends User implements IAdmin {
	private static final String CANCEL_LOGIN = "cancel";

	// Methods
	@Override
	public void createAdmin(Scanner sc) throws WrongDataTypeInsertedException {
		Admin admin = new Admin();
		do {
			System.out.println("Give your name:");
			try {
				admin.setName(sc.nextLine());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!admin.isCorrectName());

		System.out.println("Give your birthdate");
		do {
			try {
				System.out.println("Day: ");
				int day = sc.nextInt();
				System.out.println("Month: ");
				int month = sc.nextInt();
				System.out.println("Year: ");
				int year = sc.nextInt();
				admin.setBirthdate(day, month, year);
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			} catch (InputMismatchException e) {
				throw new WrongDataTypeInsertedException();
			}
		} while (!admin.isCorrectBirthday());

		do {
			System.out.println("Give your phone number:");
			try {
				admin.setPhoneNumber(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!admin.isCorrectPhoneNumber());

		do {
			System.out.println("Give your e-mail address:");
			try {
				admin.setEmail(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!admin.isCorrectEmail());

		do {
			System.out.println("Create username:");
			try {
				admin.createUsername(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!admin.isCorrectUsername());

		do {
			System.out.println(
					"Write a password that is not less than 8 characters and contains at least: one diggit, one upper case letter,one lower case letter and one special character(@#$%^&+=)");
			try {
				admin.createPassword(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!admin.isCorrectPassword());

		try {
			User.getStore().hireAdmin(admin);
			System.out.println(admin.getName() + "'ve successfully became an admin in " + User.getStore().getName());
			User.getStore().updateTheKiflaJsonFile();
		} catch (StoreException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("The update of Kifla.json was unsuccessfull!");
		}
	}

	@Override
	public void addNewProduct(Product product) throws InvalidUserException {
		if (this.isLogged()) {
			try {
				super.getStore().addProductToStore(product);
			} catch (StoreException e) {
				System.out.println(e.getMessage());
			}
		} else {
			throw new InvalidUserException("You need to log in.");
		}
	}

	@Override
	public void addNumberOfProducts(int productId, int number) throws InvalidUserException {
		if (this.isLogged()) {
			try {
				super.getStore().addProductToStore(productId, number);
			} catch (StoreException e) {
				System.out.println(e.getMessage());
			}
		} else {
			throw new InvalidUserException("You need to log in.");
		}
	}

	@Override
	public void removeNumberOfProduct(int productId, int broi) {
		try {
			super.getStore().removeProductFromStore(productId, broi);
		} catch (StoreException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void removeOldProduct(int productId) throws InvalidUserException {
		if (this.isLogged()) {
			try {
				super.getStore().removeProductFromStore(productId);
			} catch (StoreException e) {
				System.out.println(e.getMessage());
			}
		} else {
			throw new InvalidUserException("You need to log in.");
		}
	}

	@Override
	public void setProductOnSale(int productId, double salePercent)
			throws InvalidProductException, InvalidUserException {
		if (this.isLogged()) {
			if (super.getStore().hasProductById(productId)) {
				Product product = super.getStore().getProductById(productId);
				product.setOnSaleWithPercent(salePercent);
			} else {
				throw new InvalidProductException("This product is not for sale in Kifla.bg");
			}
		} else {
			throw new InvalidUserException("You need to log in.");
		}
	}

	@Override
	public void setProductOffSale(int productId) throws InvalidProductException, InvalidUserException {
		if (this.isLogged()) {
			if (super.getStore().hasProductById(productId)) {
				Product product = super.getStore().getProductById(productId);
				product.setOffSale();
			} else {
				throw new InvalidProductException("This product is not for sale in Kifla.bg");
			}
		} else {
			throw new InvalidUserException("You need to log in.");
		}
	}

	public static Admin logIn(Scanner sc) throws InvalidUserException {
		boolean checkUsername = false;
		boolean correctPassword = false;
		Admin current = null;
		while (!(correctPassword && checkUsername)) {
			System.out.println("Insert username: ");
			String username = sc.next();
			for (Admin admin : getStore().getAdmins()) {
				if (admin.getUsername().equals(username)) {
					checkUsername = true;
					current = admin;
					break;
				}
			}
			if (!checkUsername) {
				System.out.println("Wrong username");
				System.out.println("If you want to abort the log in, insert :'cancel'");
				if (sc.next().equalsIgnoreCase(CANCEL_LOGIN)) {
					return null;
				}
				continue;
			} else {
				do {
					try {
						System.out.println("Insert password: ");
						if (User.checkPassword(sc.next(), current.getPassword())) {
							correctPassword = true;
							current.setLogged();
							System.out.println("Admin: " + current.getName() + " has successfully logged in");
						}
					} catch (InvalidUserException e) {
						System.out.println(e.getMessage());
						System.out.println("If you want to abort the log in, insert :'cancel'");
						if (sc.next().equalsIgnoreCase(CANCEL_LOGIN)) {
							return null;
						}
					}
				} while (!correctPassword);
			}
		}
		sc.nextLine();
		return current;
	}

	@Override
	public void fireAdmin(String adminUsername) {
		try {
			User.getStore().fireAdmin(adminUsername);
			User.getStore().updateTheKiflaJsonFile();
		} catch (StoreException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("The update of Kifla.json was unsuccessfull!");
		}
	}

	// Setters
	public void createUsername(String username) throws InvalidUserException {
		if (username != null && username.trim().length() > 0) {
			if (super.getStore() == null) {
				this.setUsername(username);
			} else {
				if (!super.getStore().containsAdminName(username)) {
					this.setUsername(username);
				} else {
					throw new InvalidUserException("Already taken username.");
				}
			}
		} else {
			throw new InvalidUserException("Empty username.");
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
