package product;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidProductException;
import exceptions.WrongDataTypeInsertedException;

public class Product {

	private static final double HUNDRED_PERCENT = 100;

	public enum ProductTypes {
		DRESS("dress"), PANTS("pants"), SHIRTS("shirts"), SKIRTS("skirts"), SHOES("shoes"), UNDERWEAR(
				"underwear"), ACCESSORIES("accessories");

		private String type;

		ProductTypes(String type) {
			if (type != null) {
				this.type = type;
			}
		}

		public static ProductTypes fromString(String text) {
			for (ProductTypes productType : ProductTypes.values()) {
				if (productType.type.equalsIgnoreCase(text)) {
					return productType;
				}
			}
			return null;
		}
	}

	public enum ProductSize {
		XXS("xxs"), XS("xs"), S("s"), M("m"), L("l"), XL("xl"), XXL("xxl"), XXXL("xxxl");

		private String size;

		private ProductSize(String s) {
			if (s != null) {
				this.size = s;
			}
		}

		public static ProductSize fromString(String text) {
			for (ProductSize productSize : ProductSize.values()) {
				if (productSize.size.equalsIgnoreCase(text)) {
					return productSize;
				}
			}
			return null;
		}
	}

	public enum ProductColor {
		BLACK("black"), BLUE("blue"), GREEN("green"), AQUA("aqua"), PURPLE("purple"), GRAY("gray"), BROWN(
				"brown"), WHITE("white"), RED(
						"red"), ORANGE("orange"), PINK("pink"), YELLOW("yellow"), MULTICOLOR("multicolor");

		private String colorName;

		private ProductColor(String color) {
			if (color != null) {
				this.colorName = color;
			}
		}

		public static ProductColor fromString(String text) {
			for (ProductColor productColor : ProductColor.values()) {
				if (productColor.colorName.equalsIgnoreCase(text)) {
					return productColor;
				}
			}
			return null;
		}
	}

	private static final double MIN_PERCENT_FOR_SALE = 0;
	private static final int MIN_LENGTH = 0;
	private static final double MIN_PRICE = 10;
	private static final int FIRST_ID = 1;

	private static int id = FIRST_ID;
	private ProductTypes type;
	private String model;
	private double standartPrice;
	private double discountPrice;
	private ProductSize size;
	private String description;
	private ProductColor color;
	private boolean onSale;
	private double salePercent;
	private int product_id;

	public Product() {
		this.product_id = id++;
	}

	public Product(String type, String model, double price, String size, String description, String color)
			throws InvalidProductException {
		setType(type);
		setModel(model);
		setPrice(price);
		discountPrice = this.standartPrice;
		setSize(size);
		setDescription(description);
		setColor(color);
		this.product_id = id++;
	}

	// Methods

	public static Product createNewProduct(Scanner sc) throws WrongDataTypeInsertedException {
		Product product = new Product();
		System.out.println("Product id " + product.product_id);
		do {
			System.out.println("Insert product type: (dress, pants, shirts, skirts, shoes, underwear, accsessories)");
			try {
				product.setType(sc.next());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			}
		} while (product.type == null);
		sc.nextLine();
		do {
			System.out.println("Insert model: ");
			try {
				product.setModel(sc.nextLine());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			}
		} while (product.model == null);

		do {
			System.out.println("Set product size (XXS, XS, S, M, L, XL, XXL, XXXL) : ");
			try {
				product.setSize(sc.next());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			}
		} while (product.size == null);
		sc.nextLine();
		do {
			System.out.println("Set description: ");
			try {
				product.setDescription(sc.nextLine());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			}
		} while (product.description == null);

		do {
			System.out.println(
					"Set color of the product (black, blue, green, aqua, purple, gray, brown, white, red, orange, pink, yellow, multicolor): ");
			try {
				product.setColor(sc.next());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			}
		} while (product.color == null);
		sc.nextLine();
		do {
			System.out.println("Set price of the product: ");
			try {
				product.setPrice(sc.nextDouble());
			} catch (InvalidProductException e) {
				System.out.println(e.getMessage());
			} catch (InputMismatchException e) {
				throw new WrongDataTypeInsertedException();
			}
		} while (product.standartPrice == 0);

		return product;
	}

	@Override
	public String toString() {
		return String.format(
				"Product: id: %d,	type: %s,	model: %s,	price: %.2f,	size: %s,	description: %s,	color:%s",
				this.product_id, this.type, this.model, this.discountPrice, this.size, this.description, this.color);
	}

	public void setOnSaleWithPercent(double percent) throws InvalidProductException {
		if (percent > MIN_PERCENT_FOR_SALE) {
			if (!onSale) {
				this.salePercent = percent;
				this.onSale = true;
			} else {
				this.salePercent = percent;
			}
			decreasePrice(this.salePercent);
		} else {
			throw new InvalidProductException("Invalid percent for sale.");
		}
	}

	private void decreasePrice(double percent) {
		if (percent > MIN_PERCENT_FOR_SALE) {
			this.discountPrice = (this.standartPrice - (this.standartPrice * (percent / HUNDRED_PERCENT)));
		}
	}

	public void setOffSale() throws InvalidProductException {
		if (this.onSale) {
			this.onSale = false;
			this.salePercent = MIN_PERCENT_FOR_SALE;
			this.discountPrice = this.standartPrice;
		} else {
			throw new InvalidProductException("The product is not on sale.");
		}
	}

	// Setters
	private void setType(String type) throws InvalidProductException {
		if (type != null) {
			this.type = ProductTypes.fromString(type);
		} else {
			throw new InvalidProductException("Not a valid product type.");
		}
	}

	private void setModel(String model) throws InvalidProductException {
		if (model != null && model.trim().length() > MIN_LENGTH) {
			this.model = model;
		} else {
			throw new InvalidProductException("Invalid model.");
		}
	}

	private void setPrice(double price) throws InvalidProductException {
		if (price > MIN_PRICE) {
			this.standartPrice = price;
			discountPrice = this.standartPrice;
		} else {
			throw new InvalidProductException("Invalid price.");
		}
	}

	private void setSize(String size) throws InvalidProductException {
		if (size != null) {
			this.size = ProductSize.fromString(size);
		} else {
			throw new InvalidProductException("Invalid size.");
		}
	}

	private void setDescription(String description) throws InvalidProductException {
		if (description != null && description.trim().length() > MIN_LENGTH) {
			this.description = description;
		} else {
			throw new InvalidProductException("Invalid description.");
		}
	}

	private void setColor(String color) throws InvalidProductException {
		if (color != null) {
			this.color = ProductColor.fromString(color);
		} else {
			throw new InvalidProductException("Invalid color.");
		}
	}

	// Getters
	public String getStringType() {
		return this.type.type;
	}

	public ProductTypes getType() {
		return type;
	}

	public double getPrice() {
		return this.discountPrice;
	}

	public int getProduct_id() {
		return this.product_id;
	}

	public String getModel() {
		return this.model;
	}

	public String getStringSize() {
		return this.size.size;
	}

	public ProductSize getSize() {
		return size;
	}

	public String getDescription() {
		return this.description;
	}

	public String getStringColor() {
		return this.color.colorName;
	}

	public static int getFirstID() {
		return FIRST_ID;
	}

	public static int getCurrentID() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + product_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (product_id != other.product_id)
			return false;
		return true;
	}

}
