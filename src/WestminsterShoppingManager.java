import java.io.*;
import java.util.*;
import javax.swing.*;

class WestminsterShoppingManager implements ShoppingManager {
    private List<Product> products;

    public WestminsterShoppingManager() {
        this.products = new ArrayList<>();
    }

    // Other methods for managing the product list and system operations

    public void displayMenu() {
        loadFromFile();
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n------ Westminster Shopping Manager ------");
            System.out.println("1. Add a new product");
            System.out.println("2. Delete a product");
            System.out.println("3. Print the list of products");
            System.out.println("4. Save product list to a file");
            System.out.println("5. Load product list from a file");
            System.out.println("6. Load ShoppingCartGUI");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    ShoppingCartGUI r = new ShoppingCartGUI();
                    addNewProduct();
                    break;
                case 2:
                    deleteProduct();
                    break;
                case 3:
                    printProductList();
                    break;
                case 4:
                    saveToFile();
                    break;
                case 5:
                    loadFromFile();
                    break;
                case 6:
                    // Load ShoppingCartGUI
                    loadShoppingCartGUI();
                    break;
                case 0:
                    System.out.println("Exiting Westminster Shopping Manager. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        } while (choice != 0);
    }

    private void addNewProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Add a new product:");
        System.out.println("1. Electronics");
        System.out.println("2. Clothing");
        System.out.print("Choose product type (1 or 2): ");

        int productTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String productId, productName;
        int availableItems;
        double price;

        switch (productTypeChoice) {
            case 1:
                // Add Electronics
                System.out.print("Enter Electronics brand: ");
                String brand = scanner.nextLine();
                System.out.print("Enter Electronics warranty period (months): ");
                int warrantyPeriod = scanner.nextInt();

                while (true){
                    System.out.print("Enter product ID: ");
                    productId = scanner.next();
                    if (isProductIdExists(productId)) {
                        System.out.println("Product ID already exists in the system. Please enter a different product ID.");
                    } else {
                        break;
                    }
                }

                System.out.print("Enter Product Name: ");
                productName = scanner.next();
                System.out.print("Enter Available Items: ");
                availableItems = scanner.nextInt();
                System.out.print("Enter Price: ");
                price = scanner.nextDouble();

                // Create and add Electronics to the product list
                Electronics electronics = new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
                products.add(electronics);

                System.out.println("Electronics added successfully.");
                break;

            case 2:
                // Add Clothing
                System.out.print("Enter Clothing size: ");
                String size = scanner.next();
                System.out.print("Enter Clothing color: ");
                String color = scanner.next();

                while (true){
                    System.out.print("Enter product ID: ");
                    productId = scanner.next();
                    if (isProductIdExists(productId)) {
                        System.out.println("Product ID already exists in the system. Please enter a different product ID.");
                    } else {
                        break;
                    }
                }

                System.out.print("Enter Product Name: ");
                productName = scanner.next();
                System.out.print("Enter Available Items: ");
                availableItems = scanner.nextInt();
                System.out.print("Enter Price: ");
                price = scanner.nextDouble();

                // Create and add Clothing to the product list
                Clothing clothing = new Clothing(productId, productName, availableItems, price, size, color);
                products.add(clothing);

                System.out.println("Clothing added successfully.");
                break;

            default:
                System.out.println("Invalid choice. Product not added.");
                break;
        }
    }

    private boolean isProductIdExists(String productId) {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return true; // Product ID already exists
            }
        }
        return false; // Product ID does not exist
    }


    private void deleteProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the product ID to delete: ");
        String productIdToDelete = scanner.next();

        // Search for the product with the given ID
        Product productToDelete = null;
        for (Product product : products) {
            if (product.getProductId().equals(productIdToDelete)) {
                productToDelete = product;
                break;
            }
        }

        if (productToDelete != null) {
            // Display information about the product being deleted
            System.out.println("Deleting product:");
            System.out.println(productToString(productToDelete));

            // Remove the product from the list
            products.remove(productToDelete);

            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Product with ID '" + productIdToDelete + "' not found. No product deleted.");
        }
    }


    private void printProductList() {
        // Implement logic to print the list of products in alphabetical order based on the product ID
        Collections.sort(products, Comparator.comparing(Product::getProductId));

        for (Product product : products) {
            System.out.println(productToString(product));
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("productList.csv"))) {
            // Write header
            writer.println("ProductID,ProductName,AvailableItems,Price,Type,Brand,WarrantyPeriod,Size,Color");

            // Write each product
            for (Product product : products) {
                if (product instanceof Electronics) {
                    writer.println(product.getProductId() + "," + product.getProductName() + "," +
                            product.getAvailableItems() + "," + product.getPrice() + ",Electronics," +
                            ((Electronics) product).getBrand() + "," + ((Electronics) product).getWarrantyPeriod());
                } else if (product instanceof Clothing) {
                    writer.println(product.getProductId() + "," + product.getProductName() + "," +
                            product.getAvailableItems() + "," + product.getPrice() + ",Clothing," +
                            ((Clothing) product).getSize() + "," + ((Clothing) product).getColor());
                }
            }

            System.out.println("Product list saved to file successfully.");
        } catch (IOException e) {
            System.err.println("Error saving product list to file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try (Scanner scanner = new Scanner(new File("productList.csv"))) {
            // Skip the header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                // Extract product information and create instances of Electronics or Clothing
                if (fields.length >= 6) {
                    String productId = fields[0];
                    String productName = fields[1];
                    int availableItems = Integer.parseInt(fields[2]);
                    double price = Double.parseDouble(fields[3]);
                    String type = fields[4];

                    if ("Electronics".equals(type)) {
                        String brand = fields[5];
                        int warrantyPeriod = Integer.parseInt(fields[6]);
                        products.add(new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod));
                    } else if ("Clothing".equals(type)) {
                        String size = fields[5];
                        String color = fields[6];
                        products.add(new Clothing(productId, productName, availableItems, price, size, color));
                    }
                }
            }

            System.out.println("Product list loaded from file successfully.");
        } catch (IOException e) {
            System.err.println("Error loading product list from file: " + e.getMessage());
        }
    }

    private String productToString(Product product) {
        if (product instanceof Electronics) {
            return "Electronics: " + product.getProductId() + " - " + product.getProductName() +
                    " (Brand: " + ((Electronics) product).getBrand() + ", Warranty: " + ((Electronics) product).getWarrantyPeriod() + ")";
        } else if (product instanceof Clothing) {
            return "Clothing: " + product.getProductId() + " - " + product.getProductName() +
                    " (Size: " + ((Clothing) product).getSize() + ", Color: " + ((Clothing) product).getColor() + ")";
        } else {
            return "Unknown Product Type";
        }
    }

    private static void loadShoppingCartGUI() {
        SwingUtilities.invokeLater(() -> {
            ShoppingCartGUI shoppingCartGUI = new ShoppingCartGUI();
            shoppingCartGUI.setSize(800, 600);
            shoppingCartGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            shoppingCartGUI.setVisible(true);
        });
    }
}
