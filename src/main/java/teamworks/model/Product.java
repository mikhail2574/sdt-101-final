package teamworks.model;

public class Product extends NamedEntity {
    private double price;
    private int quantity;
    private int categoryId;

    public Product(int id, String name, double price, int quantity, int categoryId) {
        super(id, name);
        setPrice(price);
        setQuantity(quantity);
        setCategoryId(categoryId);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category id must be positive.");
        }
        this.categoryId = categoryId;
    }
}
