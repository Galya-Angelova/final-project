package demo;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidOrderException;
import exceptions.InvalidProductException;
import exceptions.InvalidUserException;
import exceptions.WrongDataTypeInsertedException;
import people.Admin;
import people.Buyer;
import people.User;
import product.Product;
import store.BuyerRepository;
import store.Store;

public class Commands {

	enum CommandsNames {
		CREATE("create"), BUYER("buyer"), ADMIN("admin"), CANCEL("cancel"), SEE_EMAIL("see email"), NEW_ADMIN(

				"new admin"), FIRE_ADMIN("fire admin"), ADD_PRODUCT("add product"), ADD_PRODUCT_QUANTITY(
						"add product with quantity"), ON_SALE("on sale"), OFF_SALE("off sale"), REMOVE_PRODUCT(
								"remove product"), REMOVE_OLD_PRODUCT(
										"remove old product"), SEE_PRODUCTS_WITH_QUANTITIES(
												"see products with quantities"), SEE_PRODUCTS(
														"see products"), SEE_PRODUCT_BY_ID(
																"see product by id"), SEE_PRODUCTS_IN_CART(
																		"see products in cart"), REMOVE_ALL(
																				"remove all"), MAKE_ORDER(
																						"make order"), LOG_OUT(
																								"log out");

		private String name;

		private CommandsNames(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static CommandsNames fromString(String text) {
			for (CommandsNames commandName : CommandsNames.values()) {
				if (commandName.name.equalsIgnoreCase(text)) {
					return commandName;
				}
			}
			return null;
		}
	}

	private static Scanner sc = new Scanner(System.in);

	public static void commands() throws InvalidUserException {
		do {
			System.out.println();
			System.out.println("If you don't have an account, please insert 'CREATE' :");
			System.out.println("If you want to logIn as Buyer, please insert 'BUYER' :");
			System.out.println("If you want to logIn as Admin, please insert 'ADMIN' :");
			System.out.println("If you want to STOP the program , please insert 'CANCEL' :");

			String user = sc.nextLine();
			while (user.trim().isEmpty()) {
				user = sc.nextLine();
			}
			CommandsNames command = CommandsNames.fromString(user.trim());
			if (command != null) {
				switch (command) {
				case CREATE:
					try {
						Store.getKifla().getBuyers().signIn(sc);
					} catch (WrongDataTypeInsertedException e) {
						System.out.println("Ooooops... something went wrong. Try again!");
					}
					break;
				case ADMIN:
					adminCommands();
					break;
				case BUYER:
					buyerCommands();
					break;
				case CANCEL:
					System.out.println("You have successfully stopped the program !");
					return;
				default:
					System.out.println("Try again with correct command!");
					break;
				}
			} else {
				System.out.println("Try again with correct command!");
			}
		} while (true);
	}

	private static void adminCommands() throws InvalidUserException {
		Admin admin = Admin.logIn(sc);
		if (admin == null) {
			return;
		}
		do {
			System.out.println();
			System.out.println("To create new Admin account, insert 'NEW ADMIN' :");
			System.out.println("To fire Admin account, insert 'FIRE ADMIN' :");
			System.out.println("To see current products in store, insert 'SEE PRODUCTS' :");
			System.out.println("To see current products in store, insert 'SEE PRODUCTS WITH QUANTITIES' :");
			System.out.println("To add new product, press 'ADD PRODUCT' :");
			System.out.println("To stock product with some quantity, insert 'ADD PRODUCT WITH QUANTITY' :");
			System.out.println("To set product on sale, insert 'ON SALE' : ");
			System.out.println("To set product off sale, insert 'OFF SALE' :");
			System.out.println("To remove old product from the store, insert 'REMOVE OLD PRODUCT' :");
			System.out.println("To remove some quantity of product, insert 'REMOVE PRODUCT' :");
			System.out.println("To see your email, insert 'SEE EMAIL' :");
			System.out.println("To see product by ID, insert 'SEE PRODUCT BY ID:' ");
			System.out.println("To log out insert 'LOG OUT' :");

			String line = sc.nextLine();
			while (line.trim().isEmpty()) {
				line = sc.nextLine();
			}
			CommandsNames command = CommandsNames.fromString(line.trim());
			if (command != null) {
				try {
					switch (command) {
					case ADD_PRODUCT:
						System.out.println("Add brand new product: ");
						admin.addNewProduct(Product.createNewProduct(sc));
						break;
					case FIRE_ADMIN:
						System.out.println("Insert the username of the admin you want to fire: ");
						admin.fireAdmin(sc.nextLine());
						break;
					case ADD_PRODUCT_QUANTITY:
						System.out.println(
								"Insert product ID and quantity you want to add for this product to the store: ");
						admin.addNumberOfProducts(sc.nextInt(), sc.nextInt());
						sc.nextLine();
						break;
					case NEW_ADMIN:
						admin.createAdmin(sc);
						break;
					case ON_SALE:
						System.out.println("Insert product ID and percent to discount: ");
						try {
							admin.setProductOnSale(sc.nextInt(), sc.nextDouble());
							sc.nextLine();
						} catch (InvalidProductException e) {
							System.out.println(e.getMessage());
						}
						break;
					case OFF_SALE:
						System.out.println("Insert product ID to stop sale this product with discount : ");
						try {
							admin.setProductOffSale(sc.nextInt());
							sc.nextLine();
						} catch (InvalidProductException e) {
							System.out.println(e.getMessage());
						}
						break;
					case REMOVE_PRODUCT:
						System.out.println(
								"Insert ID number of product and how many of it you want to remove from the store:");
						admin.removeNumberOfProduct(sc.nextInt(), sc.nextInt());
						sc.nextLine();
						break;
					case REMOVE_OLD_PRODUCT:
						System.out.println("Insert ID number of the product you want to remove from the store: ");
						admin.removeOldProduct(sc.nextInt());
						sc.nextLine();
						break;
					case SEE_PRODUCTS:
						User.getStore().seeCatalog();
						break;
					case SEE_PRODUCTS_WITH_QUANTITIES:
						User.getStore().seeProductsWithQuantities();
						break;
					case SEE_EMAIL:
						admin.seeEmail();
						break;
					case SEE_PRODUCT_BY_ID:
						System.out.println("Insert ID of product, which you want to see: ");
						int id = sc.nextInt();
						if (id >= Product.getFirstID() && id <= Product.getCurrentID()) {
							System.out.println(User.getStore().getProductById(id));
						} else {
							System.out.println("Wrong id for product!");
						}
						sc.nextLine();
						break;
					case LOG_OUT:
						admin.logOut();
						return;
					default:
						System.out.println("Try again with correct command!");
						break;
					}
				} catch (WrongDataTypeInsertedException e) {
					System.out.println("Ooooops... something went wrong. Try again!");
				}
			} else {
				System.out.println("Try again with correct command!");
			}
		} while (true);
	}

	private static void buyerCommands() throws InvalidUserException {
		Buyer buyer = BuyerRepository.logIn(sc);

		if (buyer == null) {
			return;
		}
		do {
			System.out.println();
			System.out.println("To add product to your cart, insert 'ADD PRODUCT WITH QUANTITY' :");
			System.out.println("To see all the products from the catalog, insert 'SEE PRODUCTS' :");
			System.out.println("To see current products from cart, insert 'SEE PRODUCTS IN CART' :");
			System.out.println("To see product by ID, insert 'SEE PRODUCT BY ID:' ");
			System.out.println("To remove some quantity of product from your cart, insert 'REMOVE PRODUCT' :");
			System.out.println("To remove all products from your cart, insert 'REMOVE ALL'");
			System.out.println("To buy all products that are in your cart, insert 'MAKE ORDER'");
			System.out.println("To see your email, insert 'SEE EMAIL' :");
			System.out.println("To log out insert 'LOG OUT' :");

			String line = sc.nextLine();
			while (line.trim().isEmpty()) {
				line = sc.nextLine();
			}
			CommandsNames command = CommandsNames.fromString(line.trim());
			if (command != null) {
				try {
					switch (command) {
					case SEE_PRODUCTS:
						User.getStore().seeCatalog();
						break;
					case SEE_PRODUCTS_IN_CART:
						buyer.seeProductsInCart();
						break;
					case ADD_PRODUCT_QUANTITY:
						System.out.println(
								"Insert product ID and quantity of the product you want to add to your cart: ");
						try {
							buyer.addProduct(sc.nextInt(), sc.nextInt());
							sc.nextLine();
						} catch (InvalidProductException e) {
							System.out.println(e.getMessage());
						}
						break;
					case REMOVE_PRODUCT:
						System.out.println(
								"Insert ID number of product and how many of it you want to remove from your cart:");
						try {
							buyer.removeProduct(sc.nextInt(), sc.nextInt());
							sc.nextLine();
						} catch (InvalidProductException e) {
							System.out.println(e.getMessage());
						}
						break;
					case REMOVE_ALL:
						buyer.getCart().removeAllFromCart();
						break;
					case MAKE_ORDER:
						System.out.println(
								"Insert 'true' if you want to use your voucher, or 'false' if you don't want to use it now:");
						try {
							buyer.makeOrder(sc.nextBoolean());
							sc.nextLine();
						} catch (InvalidOrderException | InvalidUserException e) {
							System.out.println(e.getMessage());
						}
						break;
					case SEE_EMAIL:
						buyer.seeEmail();
						break;
					case SEE_PRODUCT_BY_ID:
						System.out.println("Insert ID of product, which you want to see: ");
						int id = sc.nextInt();
						if (id >= Product.getFirstID() && id <= Product.getCurrentID()) {
							System.out.println(User.getStore().getProductById(id));
						} else {
							System.out.println("Wrong id for product!");
						}
						sc.nextLine();
						break;
					case LOG_OUT:
						buyer.logOut();
						return;
					default:
						System.out.println("Try again with correct command!");
						break;
					}
				} catch (InputMismatchException e) {
					System.out.println("Ooooops... something went wrong. Try again!");
				}
			} else {
				System.out.println("Try again with correct command!");
			}
		} while (true);
	}
}
