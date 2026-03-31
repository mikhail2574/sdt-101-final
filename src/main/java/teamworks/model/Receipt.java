package teamworks.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receipt extends BaseEntity {
    private final LocalDateTime issuedAt;
    private final List<ReceiptItem> items;

    public Receipt(int id, LocalDateTime issuedAt, List<ReceiptItem> items) {
        super(id);
        if (issuedAt == null) {
            throw new IllegalArgumentException("Receipt issue time cannot be null.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Receipt must contain at least one item.");
        }

        this.issuedAt = issuedAt;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double total = 0;
        for (ReceiptItem item : items) {
            total += item.getTotal();
        }
        return total;
    }
}
