package people;

import java.util.Scanner;

import exceptions.InvalidProductException;
import exceptions.InvalidUserException;
import exceptions.WrongDataTypeInsertedException;
import product.Product;

public interface IAdmin {
	void createAdmin(Scanner sc) throws WrongDataTypeInsertedException;

	void addNumberOfProducts(int productId, int broi) throws InvalidUserException;

	void removeNumberOfProduct(int productId, int broi) throws InvalidUserException;

	void addNewProduct(Product product) throws InvalidUserException;

	void removeOldProduct(int productId) throws InvalidUserException;

	void setProductOnSale(int productId, double salePercent) throws InvalidProductException, InvalidUserException;

	void setProductOffSale(int productId) throws InvalidProductException, InvalidUserException;

	void fireAdmin(String username);
}
