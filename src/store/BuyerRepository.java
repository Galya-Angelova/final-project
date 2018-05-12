package store;

import java.io.IOException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

import exceptions.BuyerRepositoryException;
import exceptions.CardException;
import exceptions.InvalidUserException;
import exceptions.WrongDataTypeInsertedException;
import people.Buyer;
import people.User;

public class BuyerRepository {
	private static final String CANCEL_LOGIN = "cancel";
	private static transient Store store;
	private final Set<Buyer> buyers = new HashSet<Buyer>();

	public boolean containsName(String username) {
		if (this.buyers.size() == 0) {
			return false;
		}
		for (Buyer buyer : this.buyers) {
			if (buyer.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	// Methods

	public void signIn(Scanner sc) throws WrongDataTypeInsertedException {
		Buyer buyer = new Buyer();
		do {
			System.out.println("Give your name:");
			try {
				buyer.setName(sc.nextLine());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectName());

		System.out.println("Give your birthdate");
		do {
			try {
				System.out.println("Day: ");
				int day = sc.nextInt();
				System.out.println("Month: ");
				int month = sc.nextInt();
				System.out.println("Year: ");
				int year = sc.nextInt();
				buyer.setBirthdate(day, month, year);
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			} catch (InputMismatchException e) {
				throw new WrongDataTypeInsertedException();
			}
		} while (!buyer.isCorrectBirthday());

		sc.nextLine();
		do {
			System.out.println("Give your address:");
			try {
				buyer.setAddress(sc.nextLine());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectAddress());

		do {
			System.out.println("Give your phone number:");

			try {
				buyer.setPhoneNumber(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectPhoneNumber());

		do {
			System.out.println("Give your e-mail address:");
			try {
				buyer.setEmail(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectEmail());

		System.out.println("Give info about your card");
		sc.nextLine();
		do {
			System.out.println("Give card number:");
			try {
				buyer.setCard(sc.nextLine());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectCard());

		do {
			System.out.println("Give card availability:");
			try {
				buyer.setCardAvailability(sc.nextDouble());
			} catch (CardException e) {
				System.out.println(e.getMessage());
			} catch (InputMismatchException e) {
				throw new WrongDataTypeInsertedException();
			}
		} while (!buyer.isCorrectCardAvailability());

		do {
			System.out.println("Create username:");
			try {
				buyer.createUsername(sc.next(), this);
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectUsername());

		do {
			System.out.println(
					"Write a password that is not less than 8 characters and contains at least: one diggit, one upper case letter,one lower case letter and one special character(@#$%^&+=)");
			try {
				buyer.createPassword(sc.next());
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
		} while (!buyer.isCorrectPassword());
		sc.nextLine();

		this.buyers.add(buyer);
		System.out.println(buyer.getName() + " has successfully signed in " + BuyerRepository.store.getName());
		try {
			BuyerRepository.store.updateTheKiflaJsonFile();
		} catch (IOException e) {
			System.out.println("The update of Kifla.json was unsuccessfull!");
		}
	}

	public static Buyer logIn(Scanner sc) throws InvalidUserException {
		boolean checkUsername = false;
		boolean correctPassword = false;
		Buyer current = null;
		while (!(correctPassword && checkUsername)) {
			System.out.println("Insert username: ");
			String username = sc.next();
			for (Buyer buyer : store.getBuyers().buyers) {
				if (buyer.getUsername().equals(username)) {
					checkUsername = true;
					current = buyer;
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
							System.out.println("User: " + current.getName() + " has successfully logged in.");
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

	// Setters
	public static void setStore(Store store) throws BuyerRepositoryException {
		if (BuyerRepository.store == null && store != null) {
			BuyerRepository.store = store;
		} else {
			throw new BuyerRepositoryException("Invalid store");
		}
	}

	// Getters
	public static Store getStore() {
		return BuyerRepository.store;
	}

	public HashSet<Buyer> getBuyers() {
		return new HashSet<Buyer>(this.buyers);
	}

}
