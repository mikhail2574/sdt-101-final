package teamworks.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import teamworks.model.Category;

public class CategoryDialog extends Dialog<CategoryDialog.CategoryInput> {
    private final TextField nameField = new TextField();
    private final TextArea descriptionArea = new TextArea();

    public CategoryDialog(Category category) {
        setTitle(category == null ? "Add Category" : "Edit Category");
        setHeaderText(category == null ? "Enter category information" : "Update category information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        getDialogPane().getStyleClass().add("dialog-pane");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField.setPromptText("Category name");
        descriptionArea.setPromptText("Category description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);

        if (category != null) {
            nameField.setText(category.getName());
            descriptionArea.setText(category.getDescription());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                String name = nameField.getText() == null ? "" : nameField.getText().trim();
                String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

                if (name.isEmpty()) {
                    showError("Category name cannot be empty.");
                    return null;
                }

                return new CategoryInput(name, description);
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

    public static final class CategoryInput {
        private final String name;
        private final String description;

        public CategoryInput(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
