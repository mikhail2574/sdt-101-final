package teamworks.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import teamworks.Receipt;
import teamworks.ReceiptItem;

public class ReceiptWindow {

    public static void show(Receipt receipt) {
        Stage stage = new Stage();
        stage.setTitle("Sales Receipt");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Sales Receipt");
        title.getStyleClass().add("receipt-title");

        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setWrapText(true);
        receiptArea.getStyleClass().add("receipt-area");
        receiptArea.setText(buildReceiptText(receipt));

        VBox root = new VBox(12, title, receiptArea);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("receipt-root");

        Scene scene = new Scene(root, 460, 360);
        scene.getStylesheets().add(ReceiptWindow.class.getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.showAndWait();
    }

    private static String buildReceiptText(Receipt receipt) {
        StringBuilder sb = new StringBuilder();

        sb.append("PET SHOP SALES RECEIPT\n");
        sb.append("====================================\n");

        for (ReceiptItem item : receipt.getItems()) {
            sb.append("Product: ").append(item.getProduct().getName()).append("\n");
            sb.append("Quantity: ").append(item.getQuantity()).append("\n");
            sb.append("Unit price: ").append(item.getProduct().getPrice()).append(" UAH\n");
            sb.append("Line total: ").append(item.getTotal()).append(" UAH\n");
            sb.append("------------------------------------\n");
        }

        sb.append("TOTAL: ").append(receipt.getTotalPrice()).append(" UAH\n");

        return sb.toString();
    }
}