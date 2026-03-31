package teamworks.persistence;

import java.nio.file.Path;

public final class StoragePaths {
    private static final Path DATA_DIRECTORY = Path.of(System.getProperty("user.dir"), "data");
    private static final Path CATEGORIES_FILE = DATA_DIRECTORY.resolve("categories.db");
    private static final Path PRODUCTS_FILE = DATA_DIRECTORY.resolve("products.db");

    private StoragePaths() {
    }

    public static Path dataDirectory() {
        return DATA_DIRECTORY;
    }

    public static Path categoriesFile() {
        return CATEGORIES_FILE;
    }

    public static Path productsFile() {
        return PRODUCTS_FILE;
    }
}
