package teamworks.model;

public abstract class NamedEntity extends BaseEntity {
    private String name;

    protected NamedEntity(int id, String name) {
        super(id);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }
}
