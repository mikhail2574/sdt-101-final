package teamworks.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import teamworks.model.Category;
import teamworks.model.Product;
import teamworks.model.Receipt;
import teamworks.service.ShopService;

import java.util.List;
import java.util.Optional;

public class MainView {
    private static final String ALL_CATEGORIES = "All Categories";

    private final BorderPane root;
    private final ShopService shopService;

    private final TableView<Product> productTable;
    private final TableView<Category> categoryTable;
    private final ListView<String> historyList;

    private final TextField productNameFilterField;
    private final TextField productIdSearchField;
    private final ComboBox<String> productCategoryFilterBox;
    private final ComboBox<String> productSortBox;
    private final Label infoLabel;
    private final Label totalProductsLabel;
    private final Label totalStockLabel;
    private final Label totalCategoriesLabel;

    public MainView() {
        this.shopService = new ShopService();
        this.root = new BorderPane();
        this.productTable = new TableView<>();
        this.categoryTable = new TableView<>();
        this.historyList = new ListView<>();
        this.productNameFilterField = new TextField();
        this.productIdSearchField = new TextField();
        this.productCategoryFilterBox = new ComboBox<>();
        this.productSortBox = new ComboBox<>();
        this.infoLabel = new Label("System ready");
        this.totalProductsLabel = new Label();
        this.totalStockLabel = new Label();
        this.totalCategoriesLabel = new Label();

        buildUi();
        refreshAllViews();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUi() {
        root.getStyleClass().add("app-root");

        VBox topSection = new VBox(18, createHeader(), createStatsBar());
        topSection.setPadding(new Insets(20, 20, 12, 20));

        HBox centerSection = new HBox(18, createMainTabs(), createHistoryPanel());
        centerSection.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(centerSection.getChildren().get(0), Priority.ALWAYS);

        HBox bottomBar = new HBox(infoLabel);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setPadding(new Insets(12, 20, 20, 20));
        infoLabel.getStyleClass().add("info-label");

        root.setTop(topSection);
        root.setCenter(centerSection);
        root.setBottom(bottomBar);
    }

    private Node createHeader() {
        Label title = new Label("Pet Shop Inventory Manager");
        title.setFont(Font.font(28));
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Persistent CRUD for categories and products with custom algorithms");
        subtitle.getStyleClass().add("subtitle-label");

        VBox textBox = new VBox(6, title, subtitle);

        Label badge = new Label("COURSE PROJECT");
        badge.getStyleClass().add("badge-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(textBox, spacer, badge);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-box");
        return header;
    }

    private Node createStatsBar() {
        VBox productsCard = createStatCard("Products", totalProductsLabel);
        VBox categoriesCard = createStatCard("Categories", totalCategoriesLabel);
        VBox stockCard = createStatCard("Units in Stock", totalStockLabel);

        HBox stats = new HBox(16, productsCard, categoriesCard, stockCard);
        stats.setAlignment(Pos.CENTER_LEFT);
        return stats;
    }

    private VBox createStatCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        valueLabel.getStyleClass().add("stat-value");

        VBox box = new VBox(8, titleLabel, valueLabel);
        box.getStyleClass().add("stat-card");
        box.setPadding(new Insets(18));
        box.setMinWidth(190);
        return box;
    }

    private Node createMainTabs() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("content-tabs");

        Tab productsTab = new Tab("Products");
        productsTab.setClosable(false);
        productsTab.setContent(createProductsTab());

        Tab categoriesTab = new Tab("Categories");
        categoriesTab.setClosable(false);
        categoriesTab.setContent(createCategoriesTab());

        tabPane.getTabs().addAll(productsTab, categoriesTab);

        StackPane wrapper = new StackPane(tabPane);
        wrapper.getStyleClass().add("content-wrapper");
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private Node createProductsTab() {
        configureProductTable();

        productNameFilterField.setPromptText("Filter by product name");
        productNameFilterField.setPrefWidth(180);

        productCategoryFilterBox.setPrefWidth(180);
        productCategoryFilterBox.setOnAction(event -> refreshProductTable());

        productSortBox.setItems(FXCollections.observableArrayList("ID", "Name", "Price", "Quantity"));
        productSortBox.setValue("ID");
        productSortBox.setOnAction(event -> {
            refreshProductTable();
            infoLabel.setText("Products sorted with merge sort.");
        });

        productIdSearchField.setPromptText("Binary search by product ID");
        productIdSearchField.setPrefWidth(190);

        Button filterButton = createActionButton("Apply Filter", "primary-button");
        filterButton.setOnAction(event -> refreshProductTable());

        Button showAllButton = createActionButton("Show All", "neutral-button");
        showAllButton.setOnAction(event -> resetProductFilters());

        Button binarySearchButton = createActionButton("Binary Search", "neutral-button");
        binarySearchButton.setOnAction(event -> runBinarySearch());

        Button addButton = createActionButton("Add", "success-button");
        addButton.setOnAction(event -> addProduct());

        Button editButton = createActionButton("Edit", "neutral-button");
        editButton.setOnAction(event -> editSelectedProduct());

        Button deleteButton = createActionButton("Delete", "danger-button");
        deleteButton.setOnAction(event -> deleteSelectedProduct());

        Button buyButton = createActionButton("Buy", "buy-button");
        buyButton.setOnAction(event -> buySelectedProduct());

        HBox filters = new HBox(
                10,
                productNameFilterField,
                productCategoryFilterBox,
                productSortBox,
                filterButton,
                showAllButton,
                new Separator(),
                productIdSearchField,
                binarySearchButton
        );
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.getStyleClass().add("toolbar-box");

        HBox actions = new HBox(10, addButton, editButton, deleteButton, buyButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(14, filters, actions, productTable);
        content.setPadding(new Insets(18));
        content.getStyleClass().add("table-wrapper");
        VBox.setVgrow(productTable, Priority.ALWAYS);
        return content;
    }

    private Node createCategoriesTab() {
        configureCategoryTable();

        Button addButton = createActionButton("Add Category", "success-button");
        addButton.setOnAction(event -> addCategory());

        Button editButton = createActionButton("Edit Category", "neutral-button");
        editButton.setOnAction(event -> editSelectedCategory());

        Button deleteButton = createActionButton("Delete Category", "danger-button");
        deleteButton.setOnAction(event -> deleteSelectedCategory());

        HBox actions = new HBox(10, addButton, editButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(14, actions, categoryTable);
        content.setPadding(new Insets(18));
        content.getStyleClass().add("table-wrapper");
        VBox.setVgrow(categoryTable, Priority.ALWAYS);
        return content;
    }

    private Node createHistoryPanel() {
        Label title = new Label("Recent Actions");
        title.getStyleClass().add("panel-title");

        Label hint = new Label("Stored in a custom stack implementation.");
        hint.getStyleClass().add("panel-subtitle");

        historyList.setPlaceholder(new Label("No actions yet"));
        historyList.getStyleClass().add("history-list");

        VBox panel = new VBox(10, title, hint, historyList);
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(280);
        panel.getStyleClass().add("side-panel");
        VBox.setVgrow(historyList, Priority.ALWAYS);
        return panel;
    }

    private void configureProductTable() {
        TableColumn<Product, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idColumn.setPrefWidth(70);

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameColumn.setPrefWidth(210);

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(shopService.getCategoryName(data.getValue().getCategoryId())));
        categoryColumn.setPrefWidth(150);

        TableColumn<Product, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));
        priceColumn.setPrefWidth(100);

        TableColumn<Product, Number> quantityColumn = new TableColumn<>("Stock");
        quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        quantityColumn.setPrefWidth(90);

        productTable.getColumns().setAll(idColumn, nameColumn, categoryColumn, priceColumn, quantityColumn);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        productTable.setPlaceholder(new Label("No products found"));
        productTable.getStyleClass().add("data-table");
    }

    private void configureCategoryTable() {
        TableColumn<Category, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idColumn.setPrefWidth(70);

        TableColumn<Category, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameColumn.setPrefWidth(170);

        TableColumn<Category, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription()));
        descriptionColumn.setPrefWidth(300);

        TableColumn<Category, Number> countColumn = new TableColumn<>("Products");
        countColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(shopService.countProductsInCategory(data.getValue().getId())));
        countColumn.setPrefWidth(90);

        categoryTable.getColumns().setAll(idColumn, nameColumn, descriptionColumn, countColumn);
        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        categoryTable.setPlaceholder(new Label("No categories found"));
        categoryTable.getStyleClass().add("data-table");
    }

    private Button createActionButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("action-button", styleClass);
        return button;
    }

    private void refreshAllViews() {
        updateCategoryFilterOptions();
        refreshProductTable();
        refreshCategoryTable();
        refreshHistory();
        updateStats();
    }

    private void refreshProductTable() {
        List<Product> products = shopService.filterProducts(
                productNameFilterField.getText(),
                resolveSelectedCategoryId(),
                getSelectedSortOption()
        );
        productTable.setItems(FXCollections.observableArrayList(products));
    }

    private void refreshCategoryTable() {
        categoryTable.setItems(FXCollections.observableArrayList(shopService.getCategories()));
    }

    private void refreshHistory() {
        historyList.setItems(FXCollections.observableArrayList(shopService.getRecentActions(20)));
    }

    private void updateStats() {
        totalProductsLabel.setText(String.valueOf(shopService.getTotalProducts()));
        totalCategoriesLabel.setText(String.valueOf(shopService.getTotalCategories()));
        totalStockLabel.setText(String.valueOf(shopService.getTotalStock()));
    }

    private void updateCategoryFilterOptions() {
        String previousSelection = productCategoryFilterBox.getValue();
        productCategoryFilterBox.getItems().clear();
        productCategoryFilterBox.getItems().add(ALL_CATEGORIES);
        for (Category category : shopService.getCategories()) {
            productCategoryFilterBox.getItems().add(category.getName());
        }

        if (previousSelection != null && productCategoryFilterBox.getItems().contains(previousSelection)) {
            productCategoryFilterBox.setValue(previousSelection);
        } else {
            productCategoryFilterBox.setValue(ALL_CATEGORIES);
        }
    }

    private Integer resolveSelectedCategoryId() {
        String selectedCategory = productCategoryFilterBox.getValue();
        if (selectedCategory == null || ALL_CATEGORIES.equals(selectedCategory)) {
            return null;
        }

        for (Category category : shopService.getCategories()) {
            if (category.getName().equals(selectedCategory)) {
                return category.getId();
            }
        }
        return null;
    }

    private ShopService.ProductSortOption getSelectedSortOption() {
        String selectedSort = productSortBox.getValue();
        if ("Name".equals(selectedSort)) {
            return ShopService.ProductSortOption.NAME;
        }
        if ("Price".equals(selectedSort)) {
            return ShopService.ProductSortOption.PRICE;
        }
        if ("Quantity".equals(selectedSort)) {
            return ShopService.ProductSortOption.QUANTITY;
        }
        return ShopService.ProductSortOption.ID;
    }

    private void resetProductFilters() {
        productNameFilterField.clear();
        productIdSearchField.clear();
        productCategoryFilterBox.setValue(ALL_CATEGORIES);
        productSortBox.setValue("ID");
        refreshProductTable();
        infoLabel.setText("Product table reset.");
    }

    private void addProduct() {
        List<Category> categories = shopService.getCategories();
        if (categories.isEmpty()) {
            showWarning("No categories", "Create a category before adding products.");
            return;
        }

        ProductDialog dialog = new ProductDialog(null, categories);
        Optional<ProductDialog.ProductInput> result = dialog.showAndWait();

        result.ifPresent(input -> {
            try {
                shopService.createProduct(
                        input.getName(),
                        input.getPrice(),
                        input.getQuantity(),
                        input.getCategoryId()
                );
                refreshAllViews();
                infoLabel.setText("Product added successfully.");
            } catch (RuntimeException exception) {
                showError("Add product failed", exception.getMessage());
            }
        });
    }

    private void editSelectedProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showWarning("No selection", "Select a product to edit.");
            return;
        }

        ProductDialog dialog = new ProductDialog(selectedProduct, shopService.getCategories());
        Optional<ProductDialog.ProductInput> result = dialog.showAndWait();

        result.ifPresent(input -> {
            try {
                shopService.updateProduct(
                        selectedProduct.getId(),
                        input.getName(),
                        input.getPrice(),
                        input.getQuantity(),
                        input.getCategoryId()
                );
                refreshAllViews();
                infoLabel.setText("Product updated successfully.");
            } catch (RuntimeException exception) {
                showError("Update product failed", exception.getMessage());
            }
        });
    }

    private void deleteSelectedProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showWarning("No selection", "Select a product to delete.");
            return;
        }

        if (!confirm("Delete product?", selectedProduct.getName())) {
            return;
        }

        try {
            shopService.deleteProduct(selectedProduct.getId());
            refreshAllViews();
            infoLabel.setText("Product deleted successfully.");
        } catch (RuntimeException exception) {
            showError("Delete product failed", exception.getMessage());
        }
    }

    private void buySelectedProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showWarning("No selection", "Select a product to sell.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Sell Product");
        dialog.setHeaderText("Enter quantity to sell");
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(result.get().trim());
            Receipt receipt = shopService.buyProduct(selectedProduct.getId(), quantity);
            refreshAllViews();
            ReceiptWindow.show(receipt);
            infoLabel.setText("Purchase completed successfully.");
        } catch (NumberFormatException exception) {
            showWarning("Invalid input", "Enter a valid whole number.");
        } catch (RuntimeException exception) {
            showError("Purchase failed", exception.getMessage());
        }
    }

    private void runBinarySearch() {
        try {
            int productId = Integer.parseInt(productIdSearchField.getText().trim());
            Product product = shopService.findProductByIdBinarySearch(productId);

            if (product == null) {
                showWarning("Not found", "No product with this ID was found.");
                return;
            }

            productNameFilterField.clear();
            productCategoryFilterBox.setValue(ALL_CATEGORIES);
            productSortBox.setValue("ID");
            refreshProductTable();
            productTable.getSelectionModel().select(product);
            productTable.scrollTo(product);
            infoLabel.setText("Binary search found product: " + product.getName());
        } catch (NumberFormatException exception) {
            showWarning("Invalid input", "Enter a valid numeric product ID.");
        }
    }

    private void addCategory() {
        CategoryDialog dialog = new CategoryDialog(null);
        Optional<CategoryDialog.CategoryInput> result = dialog.showAndWait();

        result.ifPresent(input -> {
            try {
                shopService.createCategory(input.getName(), input.getDescription());
                refreshAllViews();
                infoLabel.setText("Category added successfully.");
            } catch (RuntimeException exception) {
                showError("Add category failed", exception.getMessage());
            }
        });
    }

    private void editSelectedCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showWarning("No selection", "Select a category to edit.");
            return;
        }

        CategoryDialog dialog = new CategoryDialog(selectedCategory);
        Optional<CategoryDialog.CategoryInput> result = dialog.showAndWait();

        result.ifPresent(input -> {
            try {
                shopService.updateCategory(selectedCategory.getId(), input.getName(), input.getDescription());
                refreshAllViews();
                infoLabel.setText("Category updated successfully.");
            } catch (RuntimeException exception) {
                showError("Update category failed", exception.getMessage());
            }
        });
    }

    private void deleteSelectedCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showWarning("No selection", "Select a category to delete.");
            return;
        }

        if (!confirm("Delete category?", selectedCategory.getName())) {
            return;
        }

        try {
            shopService.deleteCategory(selectedCategory.getId());
            refreshAllViews();
            infoLabel.setText("Category deleted successfully.");
        } catch (RuntimeException exception) {
            showError("Delete category failed", exception.getMessage());
        }
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
