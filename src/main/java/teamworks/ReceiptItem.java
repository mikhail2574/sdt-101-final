package teamworks;

public class ReceiptItem {
    private Product product;
    private int quantity;

    public ReceiptItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotal() {
        return product.getPrice() * quantity;
    }
}