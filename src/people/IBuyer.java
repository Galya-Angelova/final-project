package people;

import exceptions.InvalidOrderException;
import exceptions.InvalidProductException;
import exceptions.InvalidUserException;

public interface IBuyer {
	void addProduct(int productId, int quantity) throws InvalidProductException;

	void removeProduct(int productId, int numberToRemove) throws InvalidProductException;

	void makeOrder(boolean voucherUse) throws InvalidUserException, InvalidOrderException;

	void seeProductsInCart();
}
