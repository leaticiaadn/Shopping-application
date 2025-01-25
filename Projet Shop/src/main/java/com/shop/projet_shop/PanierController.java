package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.OrderItem;
import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.User.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanierController {

    @FXML
    private GridPane panierGrid;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        loadPanier(UserSession.getId());
    }

    private void loadPanier(int userId) {
        String query = "SELECT p.id, p.name, p.image_path, p.price, c.quantity " +
                "FROM panier c " +
                "JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            int row = 0;
            double totalPrice = 0.0; // Variable pour accumuler le prix total

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                String imagePath = resultSet.getString("image_path");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                totalPrice += price * quantity; // Calculer le prix total

                VBox productBox = new VBox(10);
                productBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 10;");

                // Image du produit
                ImageView imageView = new ImageView(new Image(getClass().getResource("/com/shop/projet_shop/Images/" + imagePath).toString()));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                productBox.getChildren().add(imageView);

                Label nameLabel = new Label(productName);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                productBox.getChildren().add(nameLabel);

                Text priceText = new Text("Prix: " + price + "€");
                productBox.getChildren().add(priceText);

                // Quantité
                Text quantityText = new Text("Quantité: " + quantity);
                productBox.getChildren().add(quantityText);

                // Supprimer du panier
                StackPane removeIcon = createIcon(
                        "M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z",
                        "Black",
                        2
                );
                removeIcon.setOnMouseClicked(event -> {
                    deleteFromPanier(id);
                });
                productBox.getChildren().add(removeIcon);

                // Diminuer la quantité
                StackPane diminuer = createIcon(
                        "M5.5 8a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1H6a.5.5 0 0 1-.5-.5",
                        "Black",
                        2
                );
                diminuer.setOnMouseClicked(event -> {
                    updateOrDeleteFromPanier(id, "-");
                });
                productBox.getChildren().add(diminuer);

                // Augmenter la quantité
                StackPane augmenter = createIcon(
                        "M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z",
                        "Black",
                        2
                );
                augmenter.setOnMouseClicked(event -> {
                    updateOrDeleteFromPanier(id, "+");
                });
                productBox.getChildren().add(augmenter);

                // Ajouter à la wishlist
                StackPane sendToWishList = createIcon(
                        "M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314",
                        "Black",
                        2
                );
                sendToWishList.setOnMouseClicked(event -> {
                    addToWishList(id, UserSession.getId());
                });
                productBox.getChildren().add(sendToWishList);

                // Ajouter le produit dans la grille
                panierGrid.add(productBox, 0, row++);
            }

            // Ajouter le prix total
            Text totalPriceText = new Text("Prix total : " + totalPrice + "€");
            totalPriceText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            panierGrid.add(totalPriceText, 0, row++);

            // Ajouter le bouton "Acheter"
            Button buyButton = new Button("Acheter");
            buyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
            buyButton.setOnAction(event -> {
                buyProducts(); // Appeler une fonction `checkout` pour traiter l'achat
            });
            panierGrid.add(buyButton, 0, row);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    
    private void deleteFromPanier(int id){
        String query = "DELETE FROM panier WHERE product_id = ? and user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, UserSession.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit supprimé avec succès du panier.");
            } else {
                System.out.println("Aucun produit trouvé avec l'id donné.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression du produit du panier.");
        }
    }
    private void updateOrDeleteFromPanier(int id, String action) {
        String checkQuery = "SELECT quantity FROM panier WHERE product_id = ? AND user_id = ?";
        String updateQuery = "UPDATE panier SET quantity = quantity "+action+" 1 WHERE product_id = ? AND user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Vérifier la quantité actuelle
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, id);
                checkStatement.setInt(2, UserSession.getId());

                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    int quantity = resultSet.getInt("quantity");
                    if (quantity > 1) {
                        // Si la quantité est > 1, on la diminue
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, id);
                            updateStatement.setInt(2, UserSession.getId());

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Quantité diminuée dans le panier.");
                            } else {
                                System.out.println("Erreur lors de la diminution de la quantité.");
                            }
                        }
                    } else {
                        // Si la quantité est 1, on appelle deleteFromPanier pour supprimer le produit
                        deleteFromPanier(id);
                    }
                } else {
                    System.out.println("Aucun produit trouvé avec l'id donné.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour ou suppression du produit dans le panier.");
        }
    }
    private void addToWishList(int product_id, int userId) {
        String query = "INSERT INTO wishlist (user_id, product_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE user_id = user_id;"; // Empêche les doublons sans changer quoi que ce soit

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Remplacer les placeholders par les valeurs correspondantes
            preparedStatement.setInt(1, userId); // ID de l'utilisateur
            preparedStatement.setInt(2, product_id); // ID du produit

            int rowsAffected = preparedStatement.executeUpdate(); // Exécuter la requête

            if (rowsAffected > 0) {
                System.out.println("Produit ajouté à la wishlist avec succès.");
            } else {
                System.out.println("Le produit est déjà dans la wishlist.");
            }
            deleteFromPanier(product_id);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du produit à la wishlist.");
        }
    }

    private void buyProducts() {
        String fetchCartQuery = "SELECT p.id, p.name, p.price, c.quantity " +
                "FROM panier c " +
                "JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";

        String createOrderQuery = "INSERT INTO orders (user_id,order_date, total_amount, status) VALUES (?, ?,?, 'en attente')";
        String fetchLastOrderIdQuery = "SELECT LAST_INSERT_ID() AS order_id";
        String addOrderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String createInvoiceQuery = "INSERT INTO factures (order_id, facture_date,amount, paid) VALUES (?,?, ?, FALSE)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement fetchCartStmt = connection.prepareStatement(fetchCartQuery);
             PreparedStatement createOrderStmt = connection.prepareStatement(createOrderQuery);
             PreparedStatement fetchLastOrderIdStmt = connection.prepareStatement(fetchLastOrderIdQuery);
             PreparedStatement addOrderItemStmt = connection.prepareStatement(addOrderItemQuery);
             PreparedStatement createInvoiceStmt = connection.prepareStatement(createInvoiceQuery)) {

            // Récupérer les articles du panier
            fetchCartStmt.setInt(1, UserSession.getId());
            ResultSet cartResultSet = fetchCartStmt.executeQuery();

            double totalAmount = 0;
            List<OrderItem> orderItems = new ArrayList<>();
            List<Integer> products_id = new ArrayList<>();

            while (cartResultSet.next()) {
                int productId = cartResultSet.getInt("id");
                String productName = cartResultSet.getString("name");
                double price = cartResultSet.getDouble("price");
                int quantity = cartResultSet.getInt("quantity");
                totalAmount += price * quantity;
                products_id.add(productId);
                orderItems.add(new OrderItem(productId, 0, productName, quantity, price));
            }

            if (orderItems.isEmpty()) {
                System.out.println("Le panier est vide. Aucune commande créée.");
                return;
            }

            // Créer une commande
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            createOrderStmt.setInt(1, UserSession.getId());
            createOrderStmt.setString(2, currentDate);
            createOrderStmt.setDouble(3, totalAmount);
            createOrderStmt.executeUpdate();

            // Récupérer l'ID de la commande
            ResultSet orderIdResultSet = fetchLastOrderIdStmt.executeQuery();
            if (!orderIdResultSet.next()) {
                System.out.println("Erreur lors de la création de la commande.");
                return;
            }
            int orderId = orderIdResultSet.getInt("order_id");

            // Ajouter les articles à la commande
            for (OrderItem item : orderItems) {
                addOrderItemStmt.setInt(1, orderId);
                addOrderItemStmt.setInt(2, item.getId());
                addOrderItemStmt.setInt(3, item.getQuantity());
                addOrderItemStmt.setDouble(4, item.getPrice());
                addOrderItemStmt.executeUpdate();
            }

            // Créer la facture
            createInvoiceStmt.setInt(1, orderId);
            createInvoiceStmt.setString(2, currentDate);
            createInvoiceStmt.setDouble(3, totalAmount);
            createInvoiceStmt.executeUpdate();

            // Supprimer le panier
            for (int id : products_id) {
                deleteFromPanier(id);
            }


            System.out.println("Commande créée avec succès. Facture générée.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création de la commande.");
        }
    }




}
