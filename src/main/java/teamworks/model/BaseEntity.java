package teamworks.model;

public abstract class BaseEntity {
    private final int id;

    protected BaseEntity(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
