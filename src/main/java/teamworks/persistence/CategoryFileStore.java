package teamworks.persistence;

import teamworks.model.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoryFileStore {
    private final Path filePath;

    public CategoryFileStore(Path filePath) {
        this.filePath = filePath;
    }

    public List<Category> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Category> categories = new ArrayList<>();

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                List<String> fields = TextRecordCodec.decode(line, 3);
                categories.add(new Category(
                        Integer.parseInt(fields.get(0)),
                        fields.get(1),
                        fields.get(2)
                ));
            }

            return categories;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load categories from file.", e);
        }
    }

    public void save(List<Category> categories) {
        List<String> lines = new ArrayList<>();
        for (Category category : categories) {
            lines.add(TextRecordCodec.encode(List.of(
                    String.valueOf(category.getId()),
                    category.getName(),
                    category.getDescription()
            )));
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save categories to file.", e);
        }
    }
}
