package teamworks;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product getById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getAll() {
        return products;
    }

    public void updateProduct(int id, int newQuantity) {
        Product product = getById(id);
        if (product != null) {
            product.setQuantity(newQuantity);
        }
    }

    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }
}