package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.User.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.List;

public class ProductController {

    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField stockQuantityField;
    @FXML
    private Label imagePathLabel;
    @FXML
    private GridPane productGrid;
    @FXML
    private Button addProductButton;
    @FXML
    private ComboBox<String> productComboBox;
    @FXML
    private VBox notificationBox;
    @FXML
    private Label errorMessage;

    private String role = UserSession.getRole();

    public void initialize() {
        if (role == null) {
            role = "guest";
            UserSession.setRole("guest");
            UserSession.setId(0);
        }
        if (role.equals("user") || role.equals("guest")) {
            addProductButton.setVisible(false);
        }
        refreshUI();
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


    @FXML
    private void displayProductsInGrid() {
        productGrid.getChildren().clear();
        ObservableList<Product> products = getProductsFromDatabase();
        int column = 0;
        int row = 0;

        for (Product product : products) {
            VBox productBox = createProductBox(product);
            productGrid.add(productBox, column, row);

            column++;
            if (column == 4) {
                column = 0;
                row++;
            }
        }
    }
    @FXML
    private void displayProductsInGrid(String name) {
        productGrid.getChildren().clear();
        ObservableList<Product> products = getProductsFromDatabase(name);
        int column = 0;
        int row = 0;

        for (Product product : products) {
            VBox productBox = createProductBox(product);
            productGrid.add(productBox, column, row);

            column++;
            if (column == 4) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createProductBox(Product product) {
        VBox productBox = new VBox(20);
        productBox.setAlignment(Pos.CENTER);
        ImageView productImage = createProductImageView(product);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setWrapText(true);
        Label priceLabel = new Label("Prix: " + product.getPrice() + "€");
        productBox.getChildren().addAll(productImage, nameLabel, descriptionLabel, priceLabel);
        if (role.equals("admin")) {
            Label stockLabel = new Label("Stock: " + product.getStock());
            productBox.getChildren().add(stockLabel);
        }
        HBox actionsBox = new HBox(20);
        actionsBox.setAlignment(Pos.CENTER);
        if (role.equals("admin") || role.equals("manager")) {
            StackPane deleteIcon = createIcon(
                    "M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5",
                    event -> deleteProduct(product)
            );
            deleteIcon.setPrefWidth(40);
            deleteIcon.setPrefHeight(40);

            actionsBox.getChildren().addAll(deleteIcon);
        }
        if (role.equals("guest") || role.equals("user")) {
            StackPane buyIcon = createIcon("M.5 1a.5.5 0 0 0 0 1h1.11l.401 1.607 1.498 7.985A.5.5 0 0 0 4 12h1a2 2 0 1 0 0 4 2 2 0 0 0 0-4h7a2 2 0 1 0 0 4 2 2 0 0 0 0-4h1a.5.5 0 0 0 .491-.408l1.5-8A.5.5 0 0 0 14.5 3H2.89l-.405-1.621A.5.5 0 0 0 2 1zm3.915 10L3.102 4h10.796l-1.313 7zM6 14a1 1 0 1 1-2 0 1 1 0 0 1 2 0m7 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0", event -> buyProduct(product, UserSession.getId()));
            if (role.equals("user")){
                StackPane wishlistIcon = createIcon("m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15", event -> addToWishlist(product));
                actionsBox.getChildren().addAll(buyIcon, wishlistIcon);
            }else{
                actionsBox.getChildren().addAll(buyIcon);
            }
        }
        productBox.getChildren().add(actionsBox);

        return productBox;
    }

    private StackPane createIcon(String svgContent, EventHandler<MouseEvent> onClickAction) {
        StackPane icon = new StackPane();
        icon.setOnMouseClicked(onClickAction);
        icon.getStyleClass().add("circle-icon");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(javafx.scene.paint.Color.BLACK);
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        icon.getChildren().add(svgPath);
        icon.setPrefWidth(40);
        icon.setPrefHeight(40);

        return icon;
    }

    private ImageView createProductImageView(Product product) {
        String imagePath = "/com/shop/projet_shop/Images/" + product.getImagePath();
        URL imageUrl = getClass().getResource(imagePath);

        ImageView productImage = new ImageView();
        productImage.setFitWidth(200);
        productImage.setFitHeight(200);
        productImage.setImage(new Image(imageUrl.toExternalForm()));

        return productImage;
    }

    private ObservableList<Product> getProductsFromDatabase() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT id, name, description, price, stock_quantity, created_at, image_path FROM products";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("created_at"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits "+ e.getMessage());
        }

        return products;
    }

    private ObservableList<Product> getProductsFromDatabase(String productname) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT id, name, description, price, stock_quantity, created_at, image_path FROM products WHERE name LIKE '%" + productname + "%'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("created_at"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits "+ e.getMessage());
        }

        return products;
    }

    @FXML
    private void addProductView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/projet_shop/Admin/addProduct.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Stage) addProductButton.getScene().getWindow()));
            stage.setOnHidden(event -> refreshUI());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addProduct() {
        if (name.getText().isEmpty() || description.getText().isEmpty() || stockQuantityField.getText().isEmpty() || priceTextField.getText().isEmpty() || imagePathLabel.getText().isEmpty()) {
            errorMessage.setVisible(true);
            errorMessage.setText("Veuillez remplir tous les champs");
        } else {
            try {
                String productName = name.getText();
                String descriptionText = description.getText();
                int stockQuantity = Integer.parseInt(stockQuantityField.getText()); // Lancer une exception si c'est une valeur non numérique
                double price = Double.parseDouble(priceTextField.getText()); // Lancer une exception si c'est une valeur non numérique
                String imagePath = imagePathLabel.getText();

                String sql = "INSERT INTO products (name, description, price, stock_quantity, created_at, image_path) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, productName);
                    stmt.setString(2, descriptionText);
                    stmt.setDouble(3, price);
                    stmt.setInt(4, stockQuantity);
                    stmt.setString(5, imagePath);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        Stage stage = (Stage) name.getScene().getWindow();
                        stage.close();
                        refreshUI();
                    } else {
                        System.out.println("Erreur: Échec de l'ajout du produit.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur: Quantité ou prix incorrects.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'ajout du produit.");
            }
        }
    }


    @FXML
    public void deleteProduct(Product product) {
        String sql = "DELETE FROM products WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showNotification("Produit supprimé avec succès.");
                refreshUI();
            } else {
                showNotification("Échec de la suppression du produit.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression "+ e.getMessage());
        }
    }

    private void buyProduct(Product product, int userId) {
        String query = "INSERT INTO panier (user_id, product_id, quantity) " +
                "VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, product.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showNotification("Produit ajouté au panier avec succès.");
            } else {
                showNotification("Erreur lors de l'ajout du produit au panier.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du produit au panier.");
        }
    }

    private void addToWishlist(Product product) {
        int userId = UserSession.getId();
        String checkQuery = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, product.getId());

            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                showNotification("Ce produit est déjà dans votre wishlist.");
                return;
            }

            String insertQuery = "INSERT INTO wishlist (user_id, product_id, added_at) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, product.getId());
                insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                insertStmt.executeUpdate();
                showNotification("Produit ajouté à la wishlist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showNotification("Erreur de base de données : " + e.getMessage());
        }
    }

    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) imagePathLabel.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getName());
            saveImage(selectedFile);
        } else {
            errorMessage.setText("Aucun fichier sélectionné. Veuillez sélectionner une image valide.");
        }
    }

    private void saveImage(File sourceFile) {
        String targetDirectory = "src/main/resources/com/shop/projet_shop/Images";
        File targetFile = new File(targetDirectory, sourceFile.getName());

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copiée avec succès : " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erreur lors de la copie de l'image" +  e.getMessage());
        }
    }

    private ObservableList<String> getProductNames(String searche) {
        ObservableList<String> products = FXCollections.observableArrayList();
        String sql = "SELECT name FROM products WHERE name LIKE '%" + searche + "%'";
        System.out.println(sql);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString("name"));
                products.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des noms des produits "+ e.getMessage());
        }

        return products;
    }
    private ObservableList<String> getProductNames() {
        ObservableList<String> products = FXCollections.observableArrayList();
        String sql = "SELECT name FROM products";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des noms des produits " + e.getMessage());
        }

        return products;
    }

    private void refreshUI() {
        if (productComboBox != null) {
            productComboBox.setItems(getProductNames());
        }
        if (productGrid != null) {
            displayProductsInGrid();
        }
    }
    public void refreshUI(String search) {
        if (productComboBox != null) {
            productComboBox.setItems(getProductNames(search));
            System.out.println("test1");
        }
        if (productGrid != null) {
            displayProductsInGrid(search);
            System.out.println("test2");
        }
    }


}