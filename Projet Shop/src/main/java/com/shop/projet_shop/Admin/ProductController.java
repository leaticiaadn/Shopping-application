package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn; // Colonne pour le prix
    @FXML
    private TableColumn<Product, Integer> stockColumn; // Colonne pour le stock
    @FXML
    private TableColumn<Product, String> dateColumn;
    @FXML
    private TextField name;
    @FXML
    private TextField description;

    @FXML
    private TextField priceTextField;
    @FXML
    private TextField stockQuantityField;

    private double price = 0.0;
    private int stockQuantity = 0;

    @FXML
    private ComboBox<String> ProductComboBox;


    public void initialize() {
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
            priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
            stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
            dateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());

            productTable.setItems(getProductsFromDatabase());
        }
        if (priceTextField != null) {
            priceTextField.setText(String.valueOf(price));
            stockQuantityField.setText(String.valueOf(stockQuantity));
        }
        if (ProductComboBox != null) {
            ProductComboBox.setItems(getProducts());
        }
    }

    private ObservableList<Product> getProductsFromDatabase() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT name, description, price, stock_quantity, created_at FROM products";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");
                String creationDate = rs.getString("created_at");

                Product product = new Product(name, description, price, stock, creationDate);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }

        return products;
    }
    @FXML
    public void increasePrice() {
        price += 1.0; // You can adjust this value
        priceTextField.setText(String.valueOf(price));
    }

    // Method to decrease the price
    @FXML
    public void decreasePrice() {
        price -= 1.0; // Decrease by 1 (adjust if needed)
        if (price < 0) price = 0; // Prevent negative price
        priceTextField.setText(String.valueOf(price));
    }

    // Method to increase the stock quantity
    @FXML
    public void increaseStock() {
        stockQuantity += 1; // Increase stock by 1
        stockQuantityField.setText(String.valueOf(stockQuantity));
    }

    // Method to decrease the stock quantity
    @FXML
    public void decreaseStock() {
        stockQuantity -= 1; // Decrease stock by 1
        if (stockQuantity < 0) stockQuantity = 0; // Prevent negative stock
        stockQuantityField.setText(String.valueOf(stockQuantity));
    }

    @FXML
    public void addProduct() {
        String productname = name.getText();
        String descriptionText = description.getText();
        String stockQuantity = stockQuantityField.getText();
        String price = priceTextField.getText();
        String sql = "INSERT INTO products (name, description, price, stock_quantity,created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productname);
            stmt.setString(2, descriptionText);
            stmt.setString(3, price);
            stmt.setString(4, stockQuantity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du produit.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
        }
    }
    public static ObservableList<String> getProducts() {
        ObservableList<String> products = FXCollections.observableArrayList(); // Correction ici
        String sql = "SELECT name FROM products";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                products.add(name);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users from the database", e);
        }
        return products;
    }

    @FXML
    void deleteProduct() {
        String selectedProduct = ProductComboBox.getValue();
        String sql = "DELETE FROM products WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, selectedProduct);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Suppression réussie !");
            } else {
                System.out.println("Aucun produit supprimé. Vérifiez les données.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
        }
    }
}
