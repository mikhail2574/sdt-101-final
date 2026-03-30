package teamworks.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import teamworks.Product;

public class ProductDialog extends Dialog<Product> {

    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField quantityField = new TextField();

    public ProductDialog(Product product) {
        setTitle(product == null ? "Add Product" : "Edit Product");
        setHeaderText(product == null ? "Enter product information" : "Update selected product");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        getDialogPane().getStyleClass().add("dialog-pane");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        idField.setPromptText("Product ID");
        nameField.setPromptText("Product name");
        priceField.setPromptText("Price");
        quantityField.setPromptText("Quantity");

        if (product != null) {
            idField.setText(String.valueOf(product.getId()));
            idField.setDisable(true);
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            quantityField.setText(String.valueOf(product.getQuantity()));
        }

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);

        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                try {
                    int id = Integer.parseInt(idField.getText().trim());
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int quantity = Integer.parseInt(quantityField.getText().trim());

                    if (name.isEmpty()) {
                        showError("Name cannot be empty.");
                        return null;
                    }

                    if (price < 0 || quantity < 0) {
                        showError("Price and quantity cannot be negative.");
                        return null;
                    }

                    return new Product(id, name, price, quantity);
                } catch (NumberFormatException e) {
                    showError("ID, price and quantity must be valid numbers.");
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
}