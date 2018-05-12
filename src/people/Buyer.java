package people;

import java.util.Set;
import java.util.TreeSet;

import exceptions.CardException;
import exceptions.InvalidCartException;
import exceptions.InvalidOrderException;
import exceptions.InvalidProductException;
import exceptions.InvalidUserException;
import order.Order;
import store.BuyerRepository;
import cart.Cart;

public class Buyer extends User implements IBuyer {
	private static final double DISCOUNT = 40;
	private static final int POSITIVE_MONEY = 0;
	private static final int MIN_LENGTH = 0;
	private static final int MIN_PRODUCTS = 0;
	private boolean correctAddress;
	private boolean correctCard;
	private boolean correctCardAvailability;
	private boolean hasVoucher;
	private String address;
	private Card card;
	private final Cart cart = new Cart();
	private Set<Order> orderHistory = new TreeSet<Order>(
			(order1, order2) -> order1.getDate().compareTo(order2.getDate()));

	private class Card {
		private static final int MAX_SPACES_IN_CARD_NUMBER = 3;
		private final int NUMBER_OF_DDIGITS = 19;
		private final int FIRST_SPACE = 4;
		private final int SECOND_SPACE = 9;
		private final int THIRD_SPACE = 14;
		private final char SPACE = ' ';

		private String cardNumber;
		private double money;

		Card(String number) throws CardException {
			setCardNumber(number);
		}

		void setCardNumber(String number) throws CardException {
			if (number != null) {
				if ((number.trim().length() != NUMBER_OF_DDIGITS) || (number.trim().charAt(FIRST_SPACE) != SPACE)
						|| (number.trim().charAt(SECOND_SPACE) != SPACE)
						|| (number.trim().charAt(THIRD_SPACE) != SPACE)) {
					throw new CardException("Invalid card number.");
				}
				int index = 0;
				int count = 0;
				for (index = 0; index < number.length(); index++) {
					if (!((number.charAt(index) == SPACE) || (Character.isDigit(number.charAt(index))))) {
						throw new CardException("Invalid card number.");
					}
					if (number.charAt(index) == SPACE) {
						if (++count > MAX_SPACES_IN_CARD_NUMBER) {
							throw new CardException("Invalid card number.");
						}
					}
				}
				this.cardNumber = number;
			} else {
				throw new CardException("Empty card number.");
			}
		}

		@Override
		public String toString() {
			return "Card : cardNumber=" + cardNumber + ", money=" + money + " BGN";
		}
	}

	// methods
	@Override
	public void seeProductsInCart() {
		this.cart.seeProductsInCart();
	}

	@Override
	public void addProduct(int productId, int quantity) throws InvalidProductException {
		if (isLogged()) {
			if (productId > 0) {
				if (quantity > MIN_PRODUCTS) {
					try {
						this.cart.addProductToCart(productId, quantity);
					} catch (InvalidCartException e) {
						System.out.println("There was a problem so your product isn't added to the cart");
					}
				} else {
					throw new InvalidProductException("Invalid quantity of product.");
				}
			} else {
				throw new InvalidProductException("Empty product.");
			}
		} else {
			System.out.println("You can't add products if you're not logged in");
		}
	}

	@Override
	public void removeProduct(int productId, int numberToRemove) throws InvalidProductException {
		if (isLogged()) {
			if (productId > 0) {
				if (numberToRemove > MIN_PRODUCTS) {
					try {
						this.cart.removeProductFromCart(productId, numberToRemove);
					} catch (InvalidCartException e) {
						System.out.println("There was a problem so your product isn't removed from the cart");
					}
				} else {
					throw new InvalidProductException("Invalid quantity of the product.");
				}
			} else {
				throw new InvalidProductException("Empty product.");
			}
		} else {
			System.out.println("You can't remove products if you're not logged in");
		}
	}

	@Override
	public void makeOrder(boolean voucherUse) throws InvalidUserException, InvalidOrderException {
		if (isLogged()) {
			double priceOfTheOrder = this.getCart().getCartPrice();
			if (this.card.money > priceOfTheOrder) {
				Order order = new Order(this.cart.getProducts());
				System.out.println(order);
				double price = order.totalPrice();
				if (voucherUse) {
					if (this.hasVoucher) {
						price -= price * (DISCOUNT / 100.0);
						System.out.println(String.format(
								"You have voucher for 40 percent discount for your order and your total price with this voucher is: %.2f",
								price));
						this.hasVoucher = false;
					} else {
						System.out.println("You've already used your voucher");
					}
				}
				this.orderHistory.add(order);
				this.card.money -= price;
				super.getStore().receiveMoney(price);
				this.cart.removeBoughtProductsFromStore();
				this.cart.removeAllFromCart();
			} else {
				throw new InvalidUserException("Not enought money to make this order!");
			}
		} else {
			System.out.println("You can't make orders if you're not logged in");
		}
	}

	// setters

	public void createUsername(String username, BuyerRepository buyers) throws InvalidUserException {
		if (username != null && username.trim().length() > MIN_LENGTH) {
			if (!buyers.containsName(username)) {
				this.setUsername(username);
				this.hasVoucher = true;
			} else {
				throw new InvalidUserException("Already taken username.");
			}
		} else {
			throw new InvalidUserException("Empty username.");
		}
	}

	public void setCard(String number) throws InvalidUserException {
		try {
			this.card = new Card(number);
			this.correctCard = true;
		} catch (CardException e) {
			throw new InvalidUserException("Invalid card.", e);
		}
	}

	public void setCardAvailability(double money) throws CardException {
		if (money > POSITIVE_MONEY) {
			this.card.money = money;
			this.correctCardAvailability = true;
		} else {
			throw new CardException("Invalid money in card.");
		}
	}

	public void setAddress(String address) throws InvalidUserException {
		if (address != null && address.trim().length() > MIN_LENGTH) {
			this.address = address;
			this.correctAddress = true;
		} else {
			throw new InvalidUserException("Invalid address.");
		}
	}

	public Cart getCart() {
		return this.cart;
	}

	@Override
	public String toString() {
		return super.toString() + ", address=" + address + ", card=" + card;

	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public boolean isBuyer() {
		return true;
	}

	public boolean isCorrectAddress() {
		return this.correctAddress;
	}

	public boolean isCorrectCard() {
		return this.correctCard;
	}

	public boolean isCorrectCardAvailability() {
		return this.correctCardAvailability;
	}

	public boolean hasVoucher() {
		return this.hasVoucher;
	}

}
