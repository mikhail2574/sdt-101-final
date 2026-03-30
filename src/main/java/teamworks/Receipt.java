package teamworks;

import java.util.List;

public class Receipt {
    private List<ReceiptItem> items;

    public Receipt(List<ReceiptItem> items) {
        this.items = items;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(ReceiptItem::getTotal)
                .sum();
    }
}