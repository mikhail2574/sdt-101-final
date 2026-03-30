package teamworks;

import java.util.List;

public class Receipt {
    private List<ReceiptItem> items;

    public Receipt(List<ReceiptItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(ReceiptItem::getTotal)
                .sum();
    }

    public void printReceipt() {
        for (ReceiptItem item : items) {
            System.out.println(item.getProduct().getName() +
                    " x" + item.getQuantity() +
                    " = " + item.getTotal());
        }
        System.out.println("TOTAL: " + getTotalPrice());
    }
}
