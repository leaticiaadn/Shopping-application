package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.User.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishListController {

    @FXML
    private VBox wishListContainer;

    @FXML
    public void initialize() {
        // Charger la wishlist pour l'utilisateur connecté
        viewWishList(UserSession.getId());
    }

    public void viewWishList(int userId) {
        // Récupérer les produits depuis la base de données
        List<Product> products = fetchWishListFromDatabase(userId);

        for (Product product : products) {
            addProductToWishList(product);
        }
    }

    private List<Product> fetchWishListFromDatabase(int userId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id,p.name, p.description, p.price, p.stock_quantity, p.image_path " +
                "FROM wishList w " +
                "JOIN products p ON w.product_id = p.id " +
                "WHERE w.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");
                String imagePath = rs.getString("image_path");

                Product product = new Product(id,name, description, price, stock, imagePath);
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    private void addProductToWishList(Product product) {
        GridPane productGrid = new GridPane();
        productGrid.setHgap(10);
        productGrid.setVgap(5);
        productGrid.setPadding(new Insets(50));
        productGrid.setStyle("-fx-border-color: gray; -fx-border-radius: 10; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        ImageView productImage = null;
        URL imageUrl = getClass().getResource("/com/shop/projet_shop/Images/" + product.getImagePath());

        if (imageUrl != null) {
            Image image = new Image(imageUrl.toString());
            productImage = new ImageView(image);
            productImage.setFitHeight(100);
            productImage.setFitWidth(100);
        } else {
            System.out.println("Image not found.");
        }
        if (productImage != null) {
            productGrid.add(productImage, 0, 0, 1, 2); // Colonne 0, ligne 0 et 1 (prend 2 lignes)
        }
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        productGrid.add(nameLabel, 2, 0); // Colonne 1, ligne 0

        // Description
        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setWrapText(true);
        productGrid.add(descriptionLabel, 2, 1); // Colonne 1, ligne 1

        // Prix
        Label priceLabel = new Label("Prix: " + product.getPrice() + "€");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        productGrid.add(priceLabel, 2, 2); // Colonne 2, ligne 0

        // ajouter au panier
        StackPane addCartIcon = createIcon(
                "M9 5.5a.5.5 0 0 0-1 0V7H6.5a.5.5 0 0 0 0 1H8v1.5a.5.5 0 0 0 1 0V8h1.5a.5.5 0 0 0 0-1H9zM.5 1a.5.5 0 0 0 0 1h1.11l.401 1.607 1.498 7.985A.5.5 0 0 0 4 12h1a2 2 0 1 0 0 4 2 2 0 0 0 0-4h7a2 2 0 1 0 0 4 2 2 0 0 0 0-4h1a.5.5 0 0 0 .491-.408l1.5-8A.5.5 0 0 0 14.5 3H2.89l-.405-1.621A.5.5 0 0 0 2 1zm3.915 10L3.102 4h10.796l-1.313 7zM6 14a1 1 0 1 1-2 0 1 1 0 0 1 2 0m7 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0",
                "black",
                1.5
        );
        addCartIcon.setOnMouseClicked(event -> {
            buyProduct(product,UserSession.getId());
        });

        productGrid.add(addCartIcon, 3, 2);
        //supprimer de la wishList
        StackPane removeIcon = createIcon(
                "M8.867 14.41c13.308-9.322 4.79-16.563.064-13.824L7 3l1.5 4-2 3L8 15a38 38 0 0 0 .867-.59m-.303-1.01-.971-3.237 1.74-2.608a1 1 0 0 0 .103-.906l-1.3-3.468 1.45-1.813c1.861-.948 4.446.002 5.197 2.11.691 1.94-.055 5.521-6.219 9.922m-1.25 1.137a36 36 0 0 1-1.522-1.116C-5.077 4.97 1.842-1.472 6.454.293c.314.12.618.279.904.477L5.5 3 7 7l-1.5 3zm-2.3-3.06-.442-1.106a1 1 0 0 1 .034-.818l1.305-2.61L4.564 3.35a1 1 0 0 1 .168-.991l1.032-1.24c-1.688-.449-3.7.398-4.456 2.128-.711 1.627-.413 4.55 3.706 8.229Z",
                "Black",
                1.5
        );
        removeIcon.setOnMouseClicked(event -> {
            deleteFromWishList(product.getId());
        });
        productGrid.add(removeIcon, 4, 2);
        wishListContainer.getChildren().add(productGrid);
    }

    private StackPane createIcon(String svgPath, String color, double scale) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setFill(Paint.valueOf(color));
        path.setScaleX(scale);
        path.setScaleY(scale);

        StackPane icon = new StackPane(path);
        icon.setMaxSize(40, 40);
        icon.setMinSize(40, 40);

        return icon;
    }

    private void deleteFromWishList(int id) {
        String query = "DELETE FROM wishlist WHERE product_id = ? and user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, UserSession.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit supprimé avec succès de la wishlist.");
            } else {
                System.out.println("Aucun produit trouvé avec l'id donné.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression du produit de la wishlist.");
        }
    }
    private void buyProduct(Product product, int userId) {
        String query = "INSERT INTO panier (user_id, product_id, quantity) " +
                "VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        try (Connection connection = DatabaseConnection.getConnection(); // Remplacez par votre méthode pour obtenir une connexion
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Remplacer les placeholders avec les valeurs correspondantes
            preparedStatement.setInt(1, userId);  // ID de l'utilisateur
            preparedStatement.setInt(2, product.getId()); // ID du produit

            int rowsAffected = preparedStatement.executeUpdate(); // Exécuter la requête

            if (rowsAffected > 0) {
                System.out.println("Produit ajouté au panier avec succès.");
            } else {
                System.out.println("Erreur lors de l'ajout du produit au panier.");
            }
            deleteFromWishList(product.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du produit au panier.");
        }
    }
}
