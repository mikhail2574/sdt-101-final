package teamworks;

public class Main {
    public static void main(String[] args) {
        ProductRepository repo = new ProductRepository();

        repo.addProduct(new Product(1, "Dog Food", 10.5, 20));
        repo.addProduct(new Product(2, "Cat Food", 8.0, 15));

        ShopService service = new ShopService(repo);

        System.out.println(service.isInStock(1, 5));

        Receipt receipt = service.buyProduct(1, 3);
        if (receipt != null) {
            receipt.printReceipt();
        }
    }
}
