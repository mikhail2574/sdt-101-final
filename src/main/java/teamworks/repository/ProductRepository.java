package teamworks.repository;

import teamworks.model.Product;
import teamworks.persistence.ProductFileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private final ProductFileStore fileStore;
    private final List<Product> products;

    public ProductRepository(ProductFileStore fileStore) {
        this.fileStore = fileStore;
        this.products = new ArrayList<>(fileStore.load());
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public List<Product> findByCategoryId(int categoryId) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategoryId() == categoryId) {
                result.add(product);
            }
        }
        return result;
    }

    public void add(Product product) {
        products.add(product);
        save();
    }

    public void update(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                save();
                return;
            }
        }
        throw new IllegalArgumentException("Product not found.");
    }

    public void delete(int id) {
        products.removeIf(product -> product.getId() == id);
        save();
    }

    public int nextId() {
        int maxId = 0;
        for (Product product : products) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        return maxId + 1;
    }

    private void save() {
        fileStore.save(products);
    }
}
