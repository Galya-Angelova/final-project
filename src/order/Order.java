package order;

import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.InvalidOrderException;
import people.User;
import product.Product;

public class Order {
	private double sum;
	private LocalDate date;
	private Map<Integer, Integer> productsForOrder;

	public Order(Map<Integer, Integer> products) throws InvalidOrderException {
		if (products != null && products.size() > 0) {
			this.productsForOrder = products;
			this.date = LocalDate.now();
		} else {
			throw new InvalidOrderException("Empty products for order.");
		}
		double sum = 0;
		for (Entry<Integer, Integer> entry : this.productsForOrder.entrySet()) {
			Product product = User.getStore().getProductById(entry.getKey());
			sum += product.getPrice() * entry.getValue();
		}
		this.sum = sum;
	}

	public double totalPrice() {
		return this.sum;
	}

	@Override
	public String toString() {
		return String.format("You make order for %.2f BGN on " + date, this.totalPrice());
	}

	public LocalDate getDate() {
		return this.date;
	}
}
