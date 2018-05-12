package demo;

import java.util.HashSet;
import java.util.Scanner;

import exceptions.InvalidProductException;
import exceptions.InvalidUserException;
import exceptions.StoreException;
import people.Admin;
import people.User;
import product.Product;
import product.Product.ProductColor;
import product.Product.ProductSize;
import product.Product.ProductTypes;
import store.Store;

public class DemoSuzdavanetoNaStora {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		HashSet<Admin> admins = new HashSet<Admin>();
		Admin admin = new Admin();
		try {
			admin.createPassword("rAnD=586");
			admin.createUsername("random");
			admin.setBirthdate(15, 2, 2000);
			admin.setName("Galya Angelova");
			admin.setEmail("galya@gmail.com");
			admin.setPhoneNumber("0885662233");
		} catch (InvalidUserException e) {
			System.out.println(e.getMessage());
		}
		admins.add((Admin) admin);

		Admin admin2 = new Admin();
		try {
			admin2.createPassword("Kp+920505");
			admin2.createUsername("kaloian_92");
			admin2.setBirthdate(5, 5, 1992);
			admin2.setName("Kaloian Pavlov");
			admin2.setEmail("kaloian_pavlov@mail.bg");
			admin2.setPhoneNumber("0899499934");
		} catch (InvalidUserException e) {
			System.out.println(e.getMessage());
		}
		admins.add((Admin) admin2);

		Store kifla = null;
		try {
			kifla = Store.getInstance(100000, admins);
			User.setStore(kifla);
		} catch (StoreException | InvalidUserException e1) {
			System.out.println(e1.getMessage());
			System.out.println("Invalid arguments for Store");
		}

		try {
			admin = Admin.logIn(sc);
		} catch (InvalidUserException e3) {
			System.out.println(e3.getMessage());
		}

		for (int i = 0; i < 100; i++) {
			try {
				Product product = new Product(
						ProductTypes.values()[(int) (Math.random() * ProductTypes.values().length)].toString(),
						"Model " + i, Math.random() * 120 + 35,
						ProductSize.values()[(int) (Math.random() * ProductSize.values().length)].toString(),
						"Description " + i,
						ProductColor.values()[(int) (Math.random() * ProductColor.values().length)].toString());
				admin.addNewProduct(product);
			} catch (InvalidProductException e) {
				e.printStackTrace();
			} catch (InvalidUserException e) {
				e.printStackTrace();
				System.out.println("Incorrect information about product.");
			}
		}
		System.out.println("\t\t*************Catalog**************");
		kifla.seeCatalog();

		try {
			admin.logOut();
		} catch (InvalidUserException e) {
			e.printStackTrace();
		}
	}

}
