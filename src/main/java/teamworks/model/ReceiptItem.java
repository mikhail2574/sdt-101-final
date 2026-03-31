package teamworks.model;

public class ReceiptItem {
    private final String productName;
    private final double unitPrice;
    private final int quantity;

    public ReceiptItem(String productName, double unitPrice, int quantity) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        this.productName = productName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return unitPrice * quantity;
    }
}
