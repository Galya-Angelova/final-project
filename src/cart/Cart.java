package cart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.InvalidCartException;
import people.Admin;
import people.User;
import product.Product;
import store.Store;

public class Cart {
	private static Store store;

	private static final int MIN_QUANTITY = 0;
	private Map<Integer, Integer> products;

	public Cart() {
		this.products = new HashMap<>();
	}

	public void seeProductsInCart() {
		if (this.products.size() == 0) {
			System.out.println("Your cart is still empty");
			return;
		}
		for (Entry<Integer, Integer> entry : this.products.entrySet()) {
			System.out.print(entry.getKey() + "\t : " + entry.getValue() + " numbers,");
			Product product = User.getStore().getProductById(entry.getKey());
			System.out.printf("price per item %.2f BGN", product.getPrice());
		}
	}

	public void addProductToCart(int productId, int numberOfProducts) throws InvalidCartException {
		if (productId > 0) {
			if (numberOfProducts > MIN_QUANTITY) {
				if (Cart.store.hasProductById(productId)) {
					if (this.products.containsKey(productId)) {
						int currentQuantity = this.products.get(productId);
						products.put(productId, currentQuantity + numberOfProducts);
					} else {
						products.put(productId, numberOfProducts);
					}
				} else {
					throw new InvalidCartException("There is no such product in the store.");
				}
			} else {
				throw new InvalidCartException("Invalid quantity.");
			}
		} else {
			throw new InvalidCartException("No product added.");
		}
	}

	public void removeProductFromCart(int productId, int numberToRemove) throws InvalidCartException {
		if (productId > 0) {
			if (numberToRemove > MIN_QUANTITY) {
				if ((this.products.containsKey(productId)) && (numberToRemove <= this.products.get(productId))) {
					if (numberToRemove == this.products.get(productId)) {
						this.products.remove(productId);
						return;
					}
					int currentQuantity = this.products.get(productId);
					int toRemove = currentQuantity - numberToRemove;
					this.products.put(productId, toRemove);
				} else {
					throw new InvalidCartException("No such an element in the cart.");
				}
			} else {
				throw new InvalidCartException("Invalid quantity.");
			}
		} else {
			throw new InvalidCartException("No product to remove.");
		}
	}

	public void removeAllFromCart() {
		this.products.clear();
	}

	public void removeBoughtProductsFromStore() {
		Admin admin = Cart.store.giveRandomAdmin();
		for (Entry<Integer, Integer> entry : this.products.entrySet()) {
			admin.removeNumberOfProduct(entry.getKey(), entry.getValue());
		}
	}

	// Setters
	public static void setStore(Store store) {
		if (Cart.store == null && store != null) {
			Cart.store = store;
		}
	}

	// Getters
	public Map<Integer, Integer> getProducts() {
		return Collections.unmodifiableMap(this.products);
	}

	public double getCartPrice() {
		double price = 0;
		for (Entry<Integer, Integer> entry : this.products.entrySet()) {
			Product product = Cart.store.getProductById(entry.getKey());
			price += product.getPrice() * entry.getValue();
		}
		return price;
	}
}
