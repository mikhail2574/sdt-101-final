package teamworks.service;

import teamworks.algorithm.ActionHistoryStack;
import teamworks.algorithm.BinarySearch;
import teamworks.algorithm.MergeSort;
import teamworks.model.Category;
import teamworks.model.Product;
import teamworks.model.Receipt;
import teamworks.model.ReceiptItem;
import teamworks.persistence.CategoryFileStore;
import teamworks.persistence.ProductFileStore;
import teamworks.persistence.StoragePaths;
import teamworks.repository.CategoryRepository;
import teamworks.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ShopService {
    public enum ProductSortOption {
        ID,
        NAME,
        PRICE,
        QUANTITY
    }

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ActionHistoryStack<String> actionHistory;
    private int nextReceiptId;

    public ShopService() {
        this.categoryRepository = new CategoryRepository(new CategoryFileStore(StoragePaths.categoriesFile()));
        this.productRepository = new ProductRepository(new ProductFileStore(StoragePaths.productsFile()));
        this.actionHistory = new ActionHistoryStack<>();
        seedDefaultDataIfNeeded();
        this.nextReceiptId = 1;
    }

    public List<Category> getCategories() {
        return MergeSort.sort(categoryRepository.findAll(),
                Comparator.comparing(category -> category.getName().toLowerCase(Locale.ROOT)));
    }

    public List<Product> getProducts() {
        return getProductsSorted(ProductSortOption.ID);
    }

    public List<Product> getProductsSorted(ProductSortOption sortOption) {
        return MergeSort.sort(productRepository.findAll(), createComparator(sortOption));
    }

    public List<Product> filterProducts(String nameFilter, Integer categoryId, ProductSortOption sortOption) {
        String normalizedName = nameFilter == null ? "" : nameFilter.trim().toLowerCase(Locale.ROOT);
        List<Product> filtered = new ArrayList<>();

        for (Product product : productRepository.findAll()) {
            boolean matchesName = normalizedName.isEmpty()
                    || product.getName().toLowerCase(Locale.ROOT).contains(normalizedName);
            boolean matchesCategory = categoryId == null || product.getCategoryId() == categoryId;

            if (matchesName && matchesCategory) {
                filtered.add(product);
            }
        }

        return MergeSort.sort(filtered, createComparator(sortOption));
    }

    public Product findProductByIdBinarySearch(int productId) {
        List<Product> sortedProducts = MergeSort.sort(productRepository.findAll(), Comparator.comparingInt(Product::getId));
        int index = BinarySearch.findIndex(sortedProducts, Product::getId, productId, Integer::compareTo);
        return index >= 0 ? sortedProducts.get(index) : null;
    }

    public Category createCategory(String name, String description) {
        validateCategoryName(name, null);
        Category category = new Category(categoryRepository.nextId(), name, description);
        categoryRepository.add(category);
        actionHistory.push("Created category: " + category.getName());
        return category;
    }

    public void updateCategory(int categoryId, String name, String description) {
        Category existingCategory = getCategoryById(categoryId);
        validateCategoryName(name, categoryId);

        existingCategory.setName(name);
        existingCategory.setDescription(description);
        categoryRepository.update(existingCategory);
        actionHistory.push("Updated category: " + existingCategory.getName());
    }

    public void deleteCategory(int categoryId) {
        Category category = getCategoryById(categoryId);
        if (!productRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new IllegalStateException("Delete or move products from this category first.");
        }

        categoryRepository.delete(categoryId);
        actionHistory.push("Deleted category: " + category.getName());
    }

    public Product createProduct(String name, double price, int quantity, int categoryId) {
        Category category = getCategoryById(categoryId);
        Product product = new Product(productRepository.nextId(), name, price, quantity, category.getId());
        productRepository.add(product);
        actionHistory.push("Created product: " + product.getName());
        return product;
    }

    public void updateProduct(int productId, String name, double price, int quantity, int categoryId) {
        Product existingProduct = getProductById(productId);
        Category category = getCategoryById(categoryId);

        existingProduct.setName(name);
        existingProduct.setPrice(price);
        existingProduct.setQuantity(quantity);
        existingProduct.setCategoryId(category.getId());
        productRepository.update(existingProduct);
        actionHistory.push("Updated product: " + existingProduct.getName());
    }

    public void deleteProduct(int productId) {
        Product product = getProductById(productId);
        productRepository.delete(productId);
        actionHistory.push("Deleted product: " + product.getName());
    }

    public Receipt buyProduct(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = getProductById(productId);
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Not enough items in stock.");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.update(product);
        actionHistory.push("Sold " + quantity + " unit(s) of " + product.getName());

        ReceiptItem item = new ReceiptItem(product.getName(), product.getPrice(), quantity);
        return new Receipt(nextReceiptId++, LocalDateTime.now(), List.of(item));
    }

    public int getTotalProducts() {
        return productRepository.findAll().size();
    }

    public int getTotalCategories() {
        return categoryRepository.findAll().size();
    }

    public int getTotalStock() {
        int total = 0;
        for (Product product : productRepository.findAll()) {
            total += product.getQuantity();
        }
        return total;
    }

    public String getCategoryName(int categoryId) {
        return getCategoryById(categoryId).getName();
    }

    public int countProductsInCategory(int categoryId) {
        return productRepository.findByCategoryId(categoryId).size();
    }

    public List<Product> getProductsByCategory(int categoryId) {
        getCategoryById(categoryId);
        return MergeSort.sort(
                productRepository.findByCategoryId(categoryId),
                Comparator.comparing(product -> product.getName().toLowerCase(Locale.ROOT))
        );
    }

    public List<String> getRecentActions(int limit) {
        List<String> actions = actionHistory.toList();
        if (actions.size() <= limit) {
            return actions;
        }
        return new ArrayList<>(actions.subList(0, limit));
    }

    private Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
    }

    private Product getProductById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
    }

    private Comparator<Product> createComparator(ProductSortOption sortOption) {
        return switch (sortOption) {
            case NAME -> Comparator.comparing(product -> product.getName().toLowerCase(Locale.ROOT));
            case PRICE -> Comparator.comparingDouble(Product::getPrice);
            case QUANTITY -> Comparator.comparingInt(Product::getQuantity);
            case ID -> Comparator.comparingInt(Product::getId);
        };
    }

    private void validateCategoryName(String name, Integer currentCategoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        Category existingCategory = categoryRepository.findByName(name.trim()).orElse(null);
        if (existingCategory != null && existingCategory.getId() != (currentCategoryId == null ? -1 : currentCategoryId)) {
            throw new IllegalArgumentException("Category name must be unique.");
        }
    }

    private void seedDefaultDataIfNeeded() {
        if (!categoryRepository.findAll().isEmpty() || !productRepository.findAll().isEmpty()) {
            return;
        }

        Category cats = createCategory("Cats", "Dry and wet food for cats");
        Category dogs = createCategory("Dogs", "Main food assortment for dogs");
        Category birds = createCategory("Birds", "Seed mixes for decorative birds");

        createProduct("Cat Dry Food Premium", 320.0, 15, cats.getId());
        createProduct("Dog Wet Food Chicken", 95.0, 25, dogs.getId());
        createProduct("Bird Seeds Mix", 140.0, 18, birds.getId());
        createProduct("Dog Grain-Free Snack", 180.0, 10, dogs.getId());
        actionHistory.push("Seeded initial catalog from built-in demo data.");
    }
}
