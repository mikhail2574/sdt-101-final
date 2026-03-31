package teamworks.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import teamworks.model.Receipt;
import teamworks.model.ReceiptItem;

import java.time.format.DateTimeFormatter;

public final class ReceiptWindow {
    private static final DateTimeFormatter RECEIPT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private ReceiptWindow() {
    }

    public static void show(Receipt receipt) {
        Stage stage = new Stage();
        stage.setTitle("Sales Receipt");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Sales Receipt");
        title.getStyleClass().add("receipt-title");

        TextArea receiptArea = new TextArea(buildReceiptText(receipt));
        receiptArea.setEditable(false);
        receiptArea.setWrapText(true);
        receiptArea.getStyleClass().add("receipt-area");

        VBox root = new VBox(12, title, receiptArea);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("receipt-root");

        Scene scene = new Scene(root, 460, 360);
        if (ReceiptWindow.class.getResource("/styles.css") != null) {
            scene.getStylesheets().add(ReceiptWindow.class.getResource("/styles.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.showAndWait();
    }

    private static String buildReceiptText(Receipt receipt) {
        StringBuilder builder = new StringBuilder();
        builder.append("PET SHOP SALES RECEIPT\n");
        builder.append("Receipt ID: ").append(receipt.getId()).append("\n");
        builder.append("Issued at: ").append(RECEIPT_TIME_FORMAT.format(receipt.getIssuedAt())).append("\n");
        builder.append("====================================\n");

        for (ReceiptItem item : receipt.getItems()) {
            builder.append("Product: ").append(item.getProductName()).append("\n");
            builder.append("Quantity: ").append(item.getQuantity()).append("\n");
            builder.append("Unit price: ").append(String.format("%.2f", item.getUnitPrice())).append(" UAH\n");
            builder.append("Line total: ").append(String.format("%.2f", item.getTotal())).append(" UAH\n");
            builder.append("------------------------------------\n");
        }

        builder.append("TOTAL: ").append(String.format("%.2f", receipt.getTotalPrice())).append(" UAH\n");
        return builder.toString();
    }
}
