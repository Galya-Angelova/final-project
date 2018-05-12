package demo;

import java.io.FileNotFoundException;

import exceptions.BuyerRepositoryException;
import exceptions.InvalidUserException;
import store.Store;

public class DemoSusVecheZapomneniqStoreICommandite {
	public static void main(String[] args) {
		try {
			Store.jsonKiflaBackToObject();
		} catch (FileNotFoundException | InvalidUserException | BuyerRepositoryException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}

		try {
			Commands.commands();
		} catch (Exception e) {
		//	e.printStackTrace();
			System.out.println("Ooooops... something went wrong.");
		}
	}
}
