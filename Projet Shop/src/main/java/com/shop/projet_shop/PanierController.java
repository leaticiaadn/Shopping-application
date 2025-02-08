package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.OrderItem;
import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.User.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    private VBox notificationBox;

    @FXML
    public void initialize() {
        loadPanier(UserSession.getId());
    }
    @FXML
    public void showNotification(String message) {
        Platform.runLater(() -> {
            Label notification = new Label(message);
            notification.getStyleClass().add("notification");
            notificationBox.getChildren().add(notification);
            notificationBox.setVisible(true);
            notificationBox.toFront();
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), notification);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    notificationBox.getChildren().remove(notification);
                });
                fadeOut.play();
            });

            pause.play();
        });
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
            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(20));

            double totalPrice = 0.0;
            int totalQuantity = 0;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                String imagePath = resultSet.getString("image_path");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                totalPrice += price * quantity;
                totalQuantity += quantity;

                HBox productContainer = new HBox(15);
                productContainer.setPadding(new Insets(10));
                productContainer.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15;");
                productContainer.setAlignment(Pos.CENTER);

                ImageView imageView = new ImageView(new Image(getClass().getResource("/com/shop/projet_shop/Images/" + imagePath).toString()));
                imageView.setFitHeight(200);
                imageView.setFitWidth(200);

                VBox productDetails = new VBox(50);
                Label nameLabel = new Label(productName);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label priceLabel = new Label(quantity + " x " + price + "€");
                priceLabel.setStyle("-fx-font-size: 12px;");

                HBox quantityBox = new HBox(10);
                quantityBox.setAlignment(Pos.CENTER);

                StackPane decreaseIcon = createIcon("M5.5 8a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1H6a.5.5 0 0 1-.5-.5", "Black", 2);
                decreaseIcon.setOnMouseClicked(event -> updateOrDeleteFromPanier(id, "-"));

                Text quantityText = new Text(String.valueOf(quantity));

                StackPane increaseIcon = createIcon("M8 7.5a.5.5 0 0 1 .5.5v1.5H10a.5.5 0 0 1 0 1H8.5V12a.5.5 0 0 1-1 0v-1.5H6a.5.5 0 0 1 0-1h1.5V8a.5.5 0 0 1 .5-.5", "Black", 2);
                increaseIcon.setOnMouseClicked(event -> updateOrDeleteFromPanier(id, "+"));

                quantityBox.getChildren().addAll(decreaseIcon, quantityText, increaseIcon);
                productDetails.getChildren().addAll(nameLabel, priceLabel,quantityBox);
                HBox actionsBox = new HBox(10);
                actionsBox.setAlignment(Pos.TOP_CENTER);

                StackPane removeIcon = createIcon("M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5", "Black", 2);
                removeIcon.setOnMouseClicked(event -> deleteFromPanier(id));

                StackPane wishlistIcon = createIcon("m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15", "Black", 2);
                wishlistIcon.setOnMouseClicked(event -> addToWishList(id,UserSession.getId()));

                actionsBox.getChildren().addAll(wishlistIcon,removeIcon);

                HBox detailsAndActions = new HBox(50,productDetails, actionsBox);
                detailsAndActions.setAlignment(Pos.CENTER);
                HBox.setHgrow(detailsAndActions, Priority.ALWAYS);

                productContainer.getChildren().addAll(imageView, detailsAndActions);

                mainContainer.getChildren().add(productContainer);
            }

            // Résumé du panier
            VBox summaryBox = new VBox(15);
            summaryBox.setPadding(new Insets(200));
            summaryBox.setAlignment(Pos.CENTER);
            summaryBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15; -fx-border-radius: 10;");

            if (totalPrice == 0) {
                Label emptyMessage = new Label("Ton Panier est vide");
                emptyMessage.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                summaryBox.getChildren().add(emptyMessage);
            } else {
                Label totalLabel = new Label("Total : " + totalPrice + "€");
                totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                Label totalItemsLabel = new Label("Articles : " + totalQuantity);
                totalItemsLabel.setStyle("-fx-font-size: 14px;");

                Button buyButton = new Button("Acheter");
                buyButton.setStyle("-fx-background-color: #050505; -fx-text-fill: white; -fx-font-size: 14px;");
                buyButton.setOnAction(event -> buyProducts());

                summaryBox.getChildren().addAll(totalLabel, totalItemsLabel, buyButton);
            }

            panierGrid.getChildren().clear();
            panierGrid.add(new VBox(mainContainer), 0, 0);
            panierGrid.add(new VBox(summaryBox), 1, 0);

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
                refreshUI();
                showNotification("Produit supprimé avec succès du panier.");
            } else {
                showNotification("Aucun produit trouvé avec l'id donné.");
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
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, id);
                checkStatement.setInt(2, UserSession.getId());

                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    int quantity = resultSet.getInt("quantity");
                    if (quantity >= 1) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, id);
                            updateStatement.setInt(2, UserSession.getId());
                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                refreshUI();
                                showNotification("Quantité changé dans le panier.");
                            } else {
                                showNotification("Erreur lors de la diminution de la quantité.");
                            }
                        }
                    }
                    if (action.equals("-") && quantity ==1) {
                        deleteFromPanier(id);
                        refreshUI();
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
        String queryCheck = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND product_id = ?";
        String queryInsert = "INSERT INTO wishlist (user_id, product_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, product_id);

            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                showNotification("Le produit est déjà dans la wishlist.");
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(queryInsert)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, product_id);
                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        refreshUI();
                        showNotification("Produit ajouté à la wishlist avec succès.");
                    }
                }
            }

            deleteFromPanier(product_id);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du produit à la wishlist.");
        }
    }
    private void buyProducts() {
        String fetchCartQuery = "SELECT p.id, p.name, p.price, c.quantity, p.stock_quantity " +
                "FROM panier c " +
                "JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";

        String createOrderQuery = "INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (?, ?, ?, 'en attente')";
        String fetchLastOrderIdQuery = "SELECT LAST_INSERT_ID() AS order_id";
        String addOrderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String createInvoiceQuery = "INSERT INTO factures (order_id, facture_date, amount, paid) VALUES (?, ?, ?, FALSE)";
        String updateStockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?"; // Requête pour mettre à jour le stock

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement fetchCartStmt = connection.prepareStatement(fetchCartQuery);
             PreparedStatement createOrderStmt = connection.prepareStatement(createOrderQuery);
             PreparedStatement fetchLastOrderIdStmt = connection.prepareStatement(fetchLastOrderIdQuery);
             PreparedStatement addOrderItemStmt = connection.prepareStatement(addOrderItemQuery);
             PreparedStatement createInvoiceStmt = connection.prepareStatement(createInvoiceQuery);
             PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {

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
                int quantityInStock = cartResultSet.getInt("stock_quantity");
                int requestedQuantity = quantity;

                if (requestedQuantity > quantityInStock) {
                    showNotification("La quantité demandée pour " + productName + " est supérieure à la quantité disponible. Vous allez acheter " + quantity + " au lieu de " + quantityInStock + ".");
                    requestedQuantity = quantityInStock; // Ajuste la quantité à la quantité en stock disponible
                }

                totalAmount += price * requestedQuantity;
                products_id.add(productId);
                orderItems.add(new OrderItem(productId, 0, productName, requestedQuantity, price));

                // Mettre à jour le stock après chaque produit acheté
                updateStockStmt.setInt(1, requestedQuantity); // Quantité achetée
                updateStockStmt.setInt(2, productId); // ID du produit
                updateStockStmt.executeUpdate();
            }

            if (orderItems.isEmpty()) {
                System.out.println("Le panier est vide. Aucune commande créée.");
                return;
            }

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            createOrderStmt.setInt(1, UserSession.getId());
            createOrderStmt.setString(2, currentDate);
            createOrderStmt.setDouble(3, totalAmount);
            createOrderStmt.executeUpdate();

            ResultSet orderIdResultSet = fetchLastOrderIdStmt.executeQuery();
            if (!orderIdResultSet.next()) {
                System.out.println("Erreur lors de la création de la commande.");
                return;
            }

            int orderId = orderIdResultSet.getInt("order_id");
            for (OrderItem item : orderItems) {
                addOrderItemStmt.setInt(1, orderId);
                addOrderItemStmt.setInt(2, item.getId());
                addOrderItemStmt.setInt(3, item.getQuantity());
                addOrderItemStmt.setDouble(4, item.getPrice());
                addOrderItemStmt.executeUpdate();
            }

            createInvoiceStmt.setInt(1, orderId);
            createInvoiceStmt.setString(2, currentDate);
            createInvoiceStmt.setDouble(3, totalAmount);
            createInvoiceStmt.executeUpdate();

            for (int id : products_id) {
                deleteFromPanier(id);
            }

            System.out.println("Commande créée avec succès. Facture générée.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la création de la commande.");
        }
    }



    private void refreshUI() {
        if (panierGrid != null) {
            loadPanier(UserSession.getId());
        }
    }
}
