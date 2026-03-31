package teamworks.repository;

import teamworks.model.Category;
import teamworks.persistence.CategoryFileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private final CategoryFileStore fileStore;
    private final List<Category> categories;

    public CategoryRepository(CategoryFileStore fileStore) {
        this.fileStore = fileStore;
        this.categories = new ArrayList<>(fileStore.load());
    }

    public List<Category> findAll() {
        return new ArrayList<>(categories);
    }

    public Optional<Category> findById(int id) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    public Optional<Category> findByName(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    public void add(Category category) {
        categories.add(category);
        save();
    }

    public void update(Category updatedCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == updatedCategory.getId()) {
                categories.set(i, updatedCategory);
                save();
                return;
            }
        }
        throw new IllegalArgumentException("Category not found.");
    }

    public void delete(int id) {
        categories.removeIf(category -> category.getId() == id);
        save();
    }

    public int nextId() {
        int maxId = 0;
        for (Category category : categories) {
            if (category.getId() > maxId) {
                maxId = category.getId();
            }
        }
        return maxId + 1;
    }

    private void save() {
        fileStore.save(categories);
    }
}
