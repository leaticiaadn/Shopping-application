package com.shop.projet_shop.User;

import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserProductController {

    @FXML
    private GridPane productGrid;

    public void initialize() {
        List<Product> products = getProductsFromDatabase();

        int column = 0;
        int row = 0;

        for (Product product : products) {
            VBox productBox = createProductBox(product);
            productGrid.add(productBox, column++, row);

            // Passer à la ligne suivante après 3 colonnes
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createProductBox(Product product) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10; -fx-alignment: center;");
        box.setPrefWidth(200);

        // Image du produit
        ImageView imageView = new ImageView(new Image(product.getImagePath()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        // Nom du produit
        Text productName = new Text(product.getName());
        productName.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Prix du produit
        Text productPrice = new Text(String.format("%.2f €", product.getPrice()));

        // Boutons d'action
        Button addToCartButton = new Button("Ajouter au Panier");
        addToCartButton.setOnAction(e -> handleAddToCart(product));

        Button addToWishlistButton = new Button("Ajouter à la Wishlist");
        addToWishlistButton.setOnAction(e -> handleAddToWishlist(product));

        box.getChildren().addAll(imageView, productName, productPrice, addToCartButton, addToWishlistButton);
        return box;
    }

    private List<Product> getProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, description, price, stock_quantity, image_path FROM products";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");
                String creationDate = rs.getString("created_at");
                String imagePath = rs.getString("image_path");
                Product product = new Product(id, name, description, price, stock,creationDate, imagePath);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }

        return products;
    }

    private void handleAddToCart(Product product) {
        System.out.println("Ajouté au panier : " + product.getName());
    }

    private void handleAddToWishlist(Product product) {
        System.out.println("Ajouté à la wishlist : " + product.getName());
    }
}
