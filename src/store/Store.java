package store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cart.Cart;
import exceptions.BuyerRepositoryException;
import exceptions.InvalidUserException;
import exceptions.StoreException;
import people.Admin;
import people.Buyer;
import people.User;
import product.Product;
import product.Product.ProductTypes;

public class Store {
	private static final int MIN_MONEY = 0;
	private static final int MIN_ADMINS = 1;
	private static final int MIN_NUMBER_OF_PRODUCT = 10;
	private static final String NAME = "Kifla.bg";
	private static Store kifla;
	private final String name = NAME;
	private double money;

	private Set<Admin> admins = new HashSet<Admin>();
	private final BuyerRepository buyers = new BuyerRepository();
	private Map<Product.ProductTypes, HashSet<Product>> catalog = new HashMap<Product.ProductTypes, HashSet<Product>>();
	private Map<Integer, Integer> productsListById = new HashMap<Integer, Integer>();

	private Store(double money, Set<Admin> admins) throws StoreException {
		setMoney(money);
		setAdmins(admins);
		Archiver archiver = new Archiver();
		archiver.setDaemon(true);
		archiver.start();
	}

	public static Store getInstance(double money, Set<Admin> admins) throws StoreException {
		if (kifla == null) {
			kifla = new Store(money, admins);
		}
		return kifla;
	}

	// Methods

	@Override
	public String toString() {
		return String.format("%s with %.2f BGN", this.name, this.money);
	}

	public Admin giveRandomAdmin() {
		ArrayList<Admin> admins = new ArrayList<Admin>(this.admins);
		boolean isThereLoggedAdmin = false;
		for (Admin admin : this.admins) {
			if (admin.isLogged()) {
				isThereLoggedAdmin = true;
				break;
			}
		}
		if (isThereLoggedAdmin) {
			Admin admin = null;
			do {
				admin = admins.get((int) (Math.random() * admins.size()));
			} while (!admin.isLogged());
			return admin;
		} else {
			return admins.get((int) (Math.random() * admins.size()));
		}
	}

	public void hireAdmin(Admin admin) throws StoreException {
		if (admin != null) {
			this.admins.add(admin);
		} else {
			throw new StoreException("Invalid admin.");
		}
	}

	public void fireAdmin(String adminUsername) throws StoreException {
		if (this.containsAdminName(adminUsername)) {
			if (this.admins.size() == MIN_ADMINS) {
				throw new StoreException("The store must have at least 1 admin");
			}
			Admin admin = this.adminByAdminUsername(adminUsername);
			this.admins.remove(admin);
			System.out.println(
					"Admin " + admin.getName() + " with username: " + adminUsername + ", was removed successfully.");
			try {
				this.updateTheKiflaJsonFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new StoreException("There is no admin with this username");
		}
	}

	private Admin adminByAdminUsername(String adminUsername) {
		for (Admin admin : this.admins) {
			if (admin.getUsername().equals(adminUsername)) {
				return admin;
			}
		}
		return null;
	}

	public boolean containsAdminName(String username) {
		for (Admin admin : this.admins) {
			if (admin.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public void addProductToStore(Product product) throws StoreException {
		if (!(hasProductById(product.getProduct_id()))) {
			Product.ProductTypes type = product.getType();

			if (!this.catalog.containsKey(type)) {
				this.catalog.put(type, new HashSet<Product>());
			}
			this.catalog.get(type).add(product);

			this.productsListById.put(product.getProduct_id(), 0);
			try {
				giveRandomAdmin().addNumberOfProducts(product.getProduct_id(), MIN_NUMBER_OF_PRODUCT * 2);
			} catch (InvalidUserException e) {
				System.out.println(e.getMessage());
			}
			try {
				this.updateTheKiflaJsonFile();
			} catch (IOException e) {
				System.out.println("The update of Kifla.json was unsuccessfull!");
			}
		} else {
			throw new StoreException("Already exist.");
		}
	}

	public void addProductToStore(int productId, int number) throws StoreException {
		if (hasProductById(productId)) {
			this.productsListById.put(productId, (productsListById.get(productId) + number));
			try {
				this.updateTheKiflaJsonFile();
			} catch (IOException e) {
				System.out.println("The update of Kifla.json was unsuccessfull!");
			}
		} else {
			throw new StoreException("There is no such a product, you need to add the product first!");
		}
	}

	public void removeProductFromStore(int productId, int broi) throws StoreException {
		if (hasProductById(productId)) {
			if (this.productsListById.get(productId) >= broi) {
				this.productsListById.put(productId, (this.productsListById.get(productId) - broi));
				if (this.productsListById.get(productId) < MIN_NUMBER_OF_PRODUCT) {
					try {
						giveRandomAdmin().addNumberOfProducts(productId, MIN_NUMBER_OF_PRODUCT);
					} catch (InvalidUserException e) {
						System.out.println(e.getMessage());
					}
				}
				try {
					this.updateTheKiflaJsonFile();
				} catch (IOException e) {
					System.out.println("The update of Kifla.json was unsuccessfull!");
				}
			} else {
				throw new StoreException("There are not enought numbers of this product.");
			}
		} else {
			throw new StoreException("There is no such a product, you need to add the product first!");
		}
	}

	public void removeProductFromStore(int productId) throws StoreException {
		if (hasProductById(productId)) {
			Product product = this.getProductById(productId);
			catalog.get(product.getType()).remove(product);
			productsListById.remove(productId);
			try {
				this.updateTheKiflaJsonFile();
			} catch (IOException e) {
				System.out.println("The update of Kifla.json was unsuccessfull!");
			}
		} else {
			throw new StoreException("There is no such a product, you need to add the product first!");
		}
	}

	public Product getProductById(int productId) {
		for (Entry<Product.ProductTypes, HashSet<Product>> entry : this.catalog.entrySet()) {
			for (Product product : entry.getValue()) {
				if (product.getProduct_id() == productId) {
					return product;
				}
			}
		}
		return null;
	}

	public boolean hasProductById(int productId) {
		return this.productsListById.containsKey(productId);
	}

	public void updateTheKiflaJsonFile() throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter fw = new FileWriter("Kifla.json");
		gson.toJson(this, fw);
		fw.close();
	}

	public static Store jsonKiflaBackToObject()
			throws FileNotFoundException, InvalidUserException, BuyerRepositoryException {
		Gson g = new Gson();
		FileReader fr = new FileReader("Kifla.json");
		Store store = g.fromJson(fr, Store.class);
		Store.kifla = store;
		User.setStore(store);
		BuyerRepository.setStore(store);
		Cart.setStore(store);
		return store;
	}

	public void receiveMoney(double amount) throws InvalidUserException {
		if (amount > MIN_MONEY) {
			this.money += amount;
		} else {
			throw new InvalidUserException("Incorrect amount.");
		}
	}

	// Setters
	private void setMoney(double money) throws StoreException {
		if (money > MIN_MONEY) {
			this.money = money;
		} else {
			throw new StoreException("Invalid money.");
		}
	}

	private void setAdmins(Set<Admin> admins) throws StoreException {
		if (admins != null && admins.size() >= MIN_ADMINS) {
			this.admins = new HashSet<Admin>(admins);
		} else {
			throw new StoreException("Ivalid admins!There must be at least 1 admin.");
		}
	}

	// Getters
	public static Store getKifla() {
		return Store.kifla;
	}

	public String getName() {
		return this.name;
	}

	public double getMoney() {
		return this.money;
	}

	public BuyerRepository getBuyers() {
		return this.buyers;
	}

	public Set<Admin> getAdmins() {
		return Collections.unmodifiableSet(this.admins);
	}

	public Map<Integer, Integer> getListOfProductsById() {
		return Collections.unmodifiableMap(this.productsListById);
	}

	public void seeCatalog() {
		for (ProductTypes productType : this.catalog.keySet()) {
			System.out.println(productType);
			for (Product product : this.catalog.get(productType)) {
				System.out.println("\t" + product);
			}
		}
	}

	public void seeProductsWithQuantities() {
		for (ProductTypes productType : this.catalog.keySet()) {
			System.out.println(productType);
			for (Product product : this.catalog.get(productType)) {
				System.out.println(
						"\t" + product + " - " + this.productsListById.get(product.getProduct_id()) + " numbers.");
			}
		}
	}

	private class Archiver extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(24 * 60 * 60 * 1000);// 24*60*60*1000 = 24h
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
				String withoutMS = now.truncatedTo(ChronoUnit.SECONDS).format(formatter);

				File archiveFile = new File("backup-" + withoutMS + ".txt");
				try (PrintWriter pw = new PrintWriter(archiveFile)) {
					pw.println(name);
					pw.println("Current money : " + money);
					// admins
					pw.println('\n' + "Current admins:");
					for (Admin admin : admins) {
						pw.println(admin);
					}
					// buyers
					pw.println('\n' + "Current byers:");
					for (Buyer buyer : buyers.getBuyers()) {
						pw.println(buyer);
					}
					// products
					pw.println('\n' + "Current products:");
					for (Entry<Integer, Integer> products : productsListById.entrySet()) {
						Product product = Store.kifla.getProductById(products.getKey());
						pw.println(product + " - " + products.getValue() + " products.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
