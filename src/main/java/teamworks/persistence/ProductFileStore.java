package teamworks.persistence;

import teamworks.model.Product;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductFileStore {
    private final Path filePath;

    public ProductFileStore(Path filePath) {
        this.filePath = filePath;
    }

    public List<Product> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Product> products = new ArrayList<>();

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                List<String> fields = TextRecordCodec.decode(line, 5);
                products.add(new Product(
                        Integer.parseInt(fields.get(0)),
                        fields.get(1),
                        Double.parseDouble(fields.get(2)),
                        Integer.parseInt(fields.get(3)),
                        Integer.parseInt(fields.get(4))
                ));
            }

            return products;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load products from file.", e);
        }
    }

    public void save(List<Product> products) {
        List<String> lines = new ArrayList<>();
        for (Product product : products) {
            lines.add(TextRecordCodec.encode(List.of(
                    String.valueOf(product.getId()),
                    product.getName(),
                    String.valueOf(product.getPrice()),
                    String.valueOf(product.getQuantity()),
                    String.valueOf(product.getCategoryId())
            )));
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save products to file.", e);
        }
    }
}
