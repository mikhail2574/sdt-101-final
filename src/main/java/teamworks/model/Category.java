package teamworks.model;

public class Category extends NamedEntity {
    private String description;

    public Category(int id, String name, String description) {
        super(id, name);
        setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    @Override
    public String toString() {
        return getName();
    }
}
