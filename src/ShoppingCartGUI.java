import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShoppingCartGUI extends JFrame {
    private JComboBox<String> productTypeComboBox;
    private JTable productTable;
    private JTextArea productDetailsTextArea;
    private JButton addToCartButton;
    private JButton viewShoppingCartButton;

    private DefaultTableModel tableModel;
    private List<Product> productList;
    private ShoppingCart shoppingCart;

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
                        productList.add(new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod));
                    } else if ("Clothing".equals(type)) {
                        String size = fields[5];
                        String color = fields[6];
                        productList.add(new Clothing(productId, productName, availableItems, price, size, color));
                    }
                }
            }

            System.out.println("Product list loaded from file successfully.");
        } catch (IOException e) {
            System.err.println("Error loading product list from file: " + e.getMessage());
        }
    }

    public ShoppingCartGUI() {
        // Initialize data
        productList = new ArrayList<>();
        loadFromFile();
        shoppingCart = new ShoppingCart();

        // Create UI components
        productTypeComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothes"});
        productTable = new JTable();
        productDetailsTextArea = new JTextArea();
        addToCartButton = new JButton("Add to Cart");
        viewShoppingCartButton = new JButton("Shopping Cart");

        // Set up table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Available Items");
        tableModel.addColumn("Price");
        productTable.setModel(tableModel);

        // Set up layout
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Product Type: "));
        topPanel.add(productTypeComboBox);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.add(new JLabel("Product Details:"), BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(productDetailsTextArea), BorderLayout.CENTER);
        detailsPanel.add(addToCartButton, BorderLayout.SOUTH);

        add(detailsPanel, BorderLayout.EAST);

        add(viewShoppingCartButton, BorderLayout.SOUTH);

        // Set up event handlers
        productTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProductsByType((String) productTypeComboBox.getSelectedItem());
            }
        });

        productTable.getSelectionModel().addListSelectionListener(e -> {
            List<Product> electronics = new ArrayList<>();
            List<Product> clothing = new ArrayList<>();
            for (Product product : productList){
                if (product instanceof Electronics){
                    electronics.add(product);
                }else {
                    clothing.add(product);
                }
            }

            if ("All".equals((String) productTypeComboBox.getSelectedItem())) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(productList.get(selectedRow));
                }
            } else if ("Electronics".equals((String) productTypeComboBox.getSelectedItem())) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(electronics.get(selectedRow));
                }
            } else {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(clothing.get(selectedRow));
                }
            }

        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Product> electronics = new ArrayList<>();
                List<Product> clothing = new ArrayList<>();
                for (Product product : productList){
                    if (product instanceof Electronics){
                        electronics.add(product);
                    }else {
                        clothing.add(product);
                    }
                }

                if ("All".equals((String) productTypeComboBox.getSelectedItem())) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(productList.get(selectedRow));
                    }
                } else if ("Electronics".equals((String) productTypeComboBox.getSelectedItem())) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(electronics.get(selectedRow));
                    }
                } else {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(clothing.get(selectedRow));
                    }
                }
            }
        });

        viewShoppingCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showShoppingCart();
            }
        });
    }

    private void filterProductsByType(String productType) {
        // Clear existing rows in the table model
        tableModel.setRowCount(0);

        if ("All".equals(productType)) {
            // Add all products to the table
            for (Product product : productList) {
                addProductToTableModel(product);
            }
        } else {
            // Add only products of the selected type to the table
            for (Product product : productList) {
                if (("Electronics".equals(productType) && product instanceof Electronics) ||
                        ("Clothes".equals(productType) && product instanceof Clothing)) {
                    addProductToTableModel(product);
                }
            }
        }
    }

    private void addProductToTableModel(Product product) {
        // Add a row to the table model with product information
        tableModel.addRow(new Object[]{product.getProductId(), product.getProductName(),
                product.getAvailableItems(), product.getPrice()});
    }

    private void showProductDetails(Product selectedProduct) {
        if (selectedProduct != null) {
            StringBuilder details = new StringBuilder();
            details.append("Product ID: ").append(selectedProduct.getProductId()).append("\n");
            details.append("Product Name: ").append(selectedProduct.getProductName()).append("\n");
            details.append("Available Items: ").append(selectedProduct.getAvailableItems()).append("\n");
            details.append("Price: $").append(selectedProduct.getPrice()).append("\n");

            if (selectedProduct instanceof Electronics) {
                details.append("Brand: ").append(((Electronics) selectedProduct).getBrand()).append("\n");
                details.append("Warranty Period: ").append(((Electronics) selectedProduct).getWarrantyPeriod()).append(" months\n");
            } else if (selectedProduct instanceof Clothing) {
                details.append("Size: ").append(((Clothing) selectedProduct).getSize()).append("\n");
                details.append("Color: ").append(((Clothing) selectedProduct).getColor()).append("\n");
            }

            productDetailsTextArea.setText(details.toString());
        } else {
            productDetailsTextArea.setText(""); // Clear the text area if no product is selected
        }
    }

    private void addProductToCart(Product selectedProduct) {
        if (selectedProduct != null) {
            shoppingCart.addProduct(selectedProduct);
            double finalPrice = shoppingCart.calculateTotalCost();

            String discountMessage = "";
            discountMessage += "20% discount will apply for buying at least three products of the same category.\n";
            discountMessage += "10% discount will apply for the very first purchase.\n";

            String message = String.format("Product added to the shopping cart.\nPrice: $%.2f\n%s", selectedProduct.getPrice(), discountMessage);
            JOptionPane.showMessageDialog(this, message, "Product Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int countProductsOfTypeElectronics() {
        int electroCount = 0;
        for (Product product : shoppingCart.getProducts()) {
            if (product instanceof Electronics){
                electroCount++;
            }
        }
        return electroCount;
    }

    private int countProductsOfTypeClothing() {
        int clothCount = 0;
        for (Product product : shoppingCart.getProducts()) {
            if (product instanceof Clothing){
                clothCount++;
            }
        }
        return clothCount;
    }


    private void showShoppingCart() {
        List<Product> cartProducts = shoppingCart.getProducts();
        double finalPrice = shoppingCart.calculateTotalCost();
        double totalCost = finalPrice;

        int electroCount = countProductsOfTypeElectronics();
        int clothCount = countProductsOfTypeClothing();

        double discount = 0;
        if (electroCount >= 3 || clothCount >=3){
            finalPrice *= 0.8;
            discount = finalPrice * 0.2;
        }

        // Create a DecimalFormat object with a pattern for two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the total cost, discount and final cost to a string with two decimal places
        String totalCostString = decimalFormat.format(totalCost);
        String discountString = decimalFormat.format(discount);
        String finalPriceString = decimalFormat.format(finalPrice);

        StringBuilder cartDetails = new StringBuilder();
        cartDetails.append("Shopping Cart:\n");

        for (Product product : cartProducts) {
            cartDetails.append(" - ").append(product.getProductName()).append(" - $").append(product.getPrice()).append("\n");
        }
        cartDetails.append("\nTotal Price: $").append(totalCostString);
        cartDetails.append("\nDiscount: $").append(discountString);
        cartDetails.append("\nFinal Price: $").append(finalPriceString);

        JOptionPane.showMessageDialog(this, cartDetails.toString(), "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ShoppingCartGUI shoppingCartGUI = new ShoppingCartGUI();
                shoppingCartGUI.setSize(900, 700);
                shoppingCartGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                shoppingCartGUI.setVisible(true);
            }
        });
    }
}