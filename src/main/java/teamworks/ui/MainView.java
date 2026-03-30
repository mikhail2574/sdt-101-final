package teamworks.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import teamworks.Product;
import teamworks.ProductRepository;
import teamworks.Receipt;
import teamworks.ShopService;

import java.util.Optional;

public class MainView {

    private final BorderPane root;

    private final ProductRepository repository;
    private final ShopService shopService;

    private final TableView<Product> productTable;
    private final TextField searchField;
    private final Label infoLabel;
    private final Label totalProductsLabel;
    private final Label totalStockLabel;

    public MainView() {
        this.repository = new ProductRepository();
        this.shopService = new ShopService(repository);

        this.root = new BorderPane();
        this.productTable = new TableView<>();
        this.searchField = new TextField();
        this.infoLabel = new Label("System ready");
        this.totalProductsLabel = new Label();
        this.totalStockLabel = new Label();

        seedDemoData();
        buildUI();
        refreshTable();
        updateStats();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUI() {
        root.getStyleClass().add("app-root");

        VBox topSection = new VBox(18, createHeader(), createToolbar(), createStatsBar());
        topSection.setPadding(new Insets(20, 20, 10, 20));

        StackPane centerWrapper = new StackPane(createTableSection());
        centerWrapper.setPadding(new Insets(0, 20, 0, 20));

        HBox bottomBar = createBottomBar();
        bottomBar.setPadding(new Insets(10, 20, 20, 20));

        root.setTop(topSection);
        root.setCenter(centerWrapper);
        root.setBottom(bottomBar);
    }

    private Node createHeader() {
        Label title = new Label("Pet Shop Food Management System");
        title.setFont(Font.font(28));
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Manage pet food stock, prices, purchases, and sales receipts");
        subtitle.getStyleClass().add("subtitle-label");

        VBox textBox = new VBox(6, title, subtitle);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label("SHOP ASSISTANT PANEL");
        badge.getStyleClass().add("badge-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(textBox, spacer, badge);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-box");

        return header;
    }

    private Node createToolbar() {
        searchField.setPromptText("Search by product name...");
        searchField.setPrefWidth(260);
        searchField.getStyleClass().add("search-field");

        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");
        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button buyButton = new Button("Buy");
        Button checkPriceButton = new Button("Check Price");
        Button checkStockButton = new Button("Check Stock");

        searchButton.getStyleClass().addAll("action-button", "primary-button");
        showAllButton.getStyleClass().addAll("action-button", "neutral-button");
        addButton.getStyleClass().addAll("action-button", "success-button");
        editButton.getStyleClass().addAll("action-button", "neutral-button");
        deleteButton.getStyleClass().addAll("action-button", "danger-button");
        buyButton.getStyleClass().addAll("action-button", "buy-button");
        checkPriceButton.getStyleClass().addAll("action-button", "neutral-button");
        checkStockButton.getStyleClass().addAll("action-button", "neutral-button");

        searchButton.setOnAction(e -> searchProducts());
        showAllButton.setOnAction(e -> refreshTable());
        addButton.setOnAction(e -> addProduct());
        editButton.setOnAction(e -> editSelectedProduct());
        deleteButton.setOnAction(e -> deleteSelectedProduct());
        buyButton.setOnAction(e -> buySelectedProduct());
        checkPriceButton.setOnAction(e -> showSelectedPrice());
        checkStockButton.setOnAction(e -> showSelectedStock());

        HBox toolbar = new HBox(
                10,
                searchField,
                searchButton,
                showAllButton,
                new Separator(),
                addButton,
                editButton,
                deleteButton,
                new Separator(),
                checkPriceButton,
                checkStockButton,
                buyButton
        );
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar-box");

        return toolbar;
    }

    private Node createStatsBar() {
        VBox productsCard = createStatCard("Number of Products", totalProductsLabel);
        VBox stockCard = createStatCard("Units in Stock", totalStockLabel);

        HBox stats = new HBox(16, productsCard, stockCard);
        stats.setAlignment(Pos.CENTER_LEFT);
        return stats;
    }

    private VBox createStatCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        valueLabel.getStyleClass().add("stat-value");

        VBox box = new VBox(8, titleLabel, valueLabel);
        box.setMinWidth(240);
        box.setPadding(new Insets(18));
        box.getStyleClass().add("stat-card");
        return box;
    }

    private Node createTableSection() {
        TableColumn<Product, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idCol.setPrefWidth(90);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.setPrefWidth(340);

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));
        priceCol.setPrefWidth(150);

        TableColumn<Product, Number> quantityCol = new TableColumn<>("Stock");
        quantityCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        quantityCol.setPrefWidth(130);

        productTable.getColumns().setAll(idCol, nameCol, priceCol, quantityCol);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        productTable.setPlaceholder(new Label("No products available"));
        productTable.getStyleClass().add("product-table");

        VBox box = new VBox(productTable);
        box.setPadding(new Insets(18));
        box.getStyleClass().add("table-wrapper");
        VBox.setVgrow(productTable, Priority.ALWAYS);

        return box;
    }

    private HBox createBottomBar() {
        infoLabel.getStyleClass().add("info-label");

        HBox bottom = new HBox(infoLabel);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.getStyleClass().add("bottom-bar");
        return bottom;
    }

    private void seedDemoData() {
        if (repository.getAll().isEmpty()) {
            repository.addProduct(new Product(1, "Cat Dry Food Premium", 320.0, 15));
            repository.addProduct(new Product(2, "Dog Wet Food Chicken", 95.0, 25));
            repository.addProduct(new Product(3, "Bird Seeds Mix", 140.0, 18));
            repository.addProduct(new Product(4, "Rabbit Pellets Natural", 210.0, 12));
        }
    }

    private void refreshTable() {
        productTable.setItems(FXCollections.observableArrayList(repository.getAll()));
        updateStats();
        infoLabel.setText("Product list updated successfully");
    }

    private void updateStats() {
        int totalProducts = repository.getAll().size();
        int totalStock = repository.getAll()
                .stream()
                .mapToInt(Product::getQuantity)
                .sum();

        totalProductsLabel.setText(String.valueOf(totalProducts));
        totalStockLabel.setText(String.valueOf(totalStock));
    }

    private void searchProducts() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        var filtered = repository.getAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword))
                .toList();

        productTable.setItems(FXCollections.observableArrayList(filtered));
        infoLabel.setText("Search completed");
    }

    private void addProduct() {
        ProductDialog dialog = new ProductDialog(null);
        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            repository.addProduct(product);
            refreshTable();
            infoLabel.setText("Product added: " + product.getName());
        });
    }

    private void editSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("No selection", "Please select a product to edit.");
            return;
        }

        ProductDialog dialog = new ProductDialog(selected);
        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            selected.setName(updated.getName());
            selected.setPrice(updated.getPrice());
            selected.setQuantity(updated.getQuantity());

            refreshTable();
            infoLabel.setText("Product updated: " + selected.getName());
        });
    }

    private void deleteSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("No selection", "Please select a product to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Delete selected product?");
        alert.setContentText(selected.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            repository.deleteProduct(selected.getId());
            refreshTable();
            infoLabel.setText("Product deleted successfully");
        }
    }

    private void showSelectedPrice() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("No selection", "Please select a product.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Price");
        alert.setHeaderText(selected.getName());
        alert.setContentText("Price: " + selected.getPrice() + " UAH");
        alert.showAndWait();
    }

    private void showSelectedStock() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("No selection", "Please select a product.");
            return;
        }

        boolean inStock = shopService.isInStock(selected.getId(), 1);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Stock Status");
        alert.setHeaderText(selected.getName());
        alert.setContentText(
                "Quantity in stock: " + selected.getQuantity() + "\nAvailable: " + (inStock ? "Yes" : "No")
        );
        alert.showAndWait();
    }

    private void buySelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("No selection", "Please select a product to buy.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Buy Product");
        dialog.setHeaderText("Enter quantity to buy");
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(result.get());

            if (quantity <= 0) {
                showWarning("Invalid quantity", "Quantity must be greater than 0.");
                return;
            }

            Receipt receipt = shopService.buyProduct(selected.getId(), quantity);

            if (receipt == null) {
                showWarning("Purchase failed", "Not enough stock for this purchase.");
                return;
            }

            refreshTable();
            ReceiptWindow.show(receipt);
            infoLabel.setText("Purchase completed successfully");
        } catch (NumberFormatException e) {
            showWarning("Invalid input", "Please enter a valid integer quantity.");
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}