package teamworks;

import java.util.ArrayList;
import java.util.List;

public class ShopService {
    private ProductRepository repository;

    public ShopService(ProductRepository repository) {
        this.repository = repository;
    }

    public boolean isInStock(int productId, int quantity) {
        Product p = repository.getById(productId);
        return p != null && p.getQuantity() >= quantity;
    }

    public double getPrice(int productId) {
        Product p = repository.getById(productId);
        return (p != null) ? p.getPrice() : 0;
    }

    public Receipt buyProduct(int productId, int quantity) {
        Product p = repository.getById(productId);

        if (p == null || p.getQuantity() < quantity) {
            return null;
        }

        p.setQuantity(p.getQuantity() - quantity);

        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem(p, quantity));

        return new Receipt(items);
    }
}