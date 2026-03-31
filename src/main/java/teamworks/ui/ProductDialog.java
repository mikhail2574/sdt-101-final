package teamworks.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import teamworks.model.Category;
import teamworks.model.Product;

import java.util.List;

public class ProductDialog extends Dialog<ProductDialog.ProductInput> {
    private final TextField nameField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField quantityField = new TextField();
    private final ComboBox<Category> categoryBox = new ComboBox<>();

    public ProductDialog(Product product, List<Category> categories) {
        setTitle(product == null ? "Add Product" : "Edit Product");
        setHeaderText(product == null ? "Enter product information" : "Update product information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        getDialogPane().getStyleClass().add("dialog-pane");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField.setPromptText("Product name");
        priceField.setPromptText("Price");
        quantityField.setPromptText("Quantity");
        categoryBox.setItems(FXCollections.observableArrayList(categories));
        categoryBox.setPromptText("Select category");

        if (product != null) {
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            quantityField.setText(String.valueOf(product.getQuantity()));
            for (Category category : categories) {
                if (category.getId() == product.getCategoryId()) {
                    categoryBox.setValue(category);
                    break;
                }
            }
        } else if (!categories.isEmpty()) {
            categoryBox.getSelectionModel().selectFirst();
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryBox, 1, 3);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                try {
                    String name = nameField.getText() == null ? "" : nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    Category category = categoryBox.getValue();

                    if (name.isEmpty()) {
                        showError("Product name cannot be empty.");
                        return null;
                    }
                    if (price < 0 || quantity < 0) {
                        showError("Price and quantity cannot be negative.");
                        return null;
                    }
                    if (category == null) {
                        showError("Please select a category.");
                        return null;
                    }

                    return new ProductInput(name, price, quantity, category.getId());
                } catch (NumberFormatException exception) {
                    showError("Price and quantity must be valid numbers.");
                }
            }
            return null;
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static final class ProductInput {
        private final String name;
        private final double price;
        private final int quantity;
        private final int categoryId;

        public ProductInput(String name, double price, int quantity, int categoryId) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.categoryId = categoryId;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getCategoryId() {
            return categoryId;
        }
    }
}
