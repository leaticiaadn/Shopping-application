package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.User.UserSession;
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
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

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


    public void initialize() {
        if (UserSession.getRole().equals("user")) {
            addProductButton.setVisible(false);
        }
        refreshUI();

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

    private VBox createProductBox(Product product) {
        VBox productBox = new VBox(10);
        productBox.setAlignment(Pos.CENTER);

        ImageView productImage = createProductImageView(product);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setWrapText(true);
        Label priceLabel = new Label("Prix: " + product.getPrice() + "€");

        productBox.getChildren().addAll(productImage, nameLabel, descriptionLabel, priceLabel);

        if (!UserSession.getRole().equals("user")) {
            Label stockLabel = new Label("Stock: " + product.getStock());
            productBox.getChildren().add(stockLabel);
        }

        HBox actionsBox = new HBox(20);
        actionsBox.setAlignment(Pos.CENTER);

        if (UserSession.getRole().equals("admin") || UserSession.getRole().equals("manager")) {
//            StackPane modifyIcon = createIcon("M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z", event -> modifyProduct(product));

            StackPane deleteIcon = createIcon(
                    "M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z",
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            deleteProduct(product); // Appel à la méthode deleteProduct avec l'objet product
                        }
                    }
            );
            deleteIcon.setPrefWidth(40);
            deleteIcon.setPrefHeight(40);

            actionsBox.getChildren().addAll(deleteIcon);
        }

        if (UserSession.getRole().equals("user")) {
            StackPane buyIcon = createIcon("M8 1.5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5V2a.5.5 0 0 1 .5-.5h2zM4.5 9.5h1v3h-1v-3zM5.5 10v2h-1v-2h1z", event -> buyProduct(product,UserSession.getId()));

            StackPane wishlistIcon = createIcon("M5 3l1 1 3-3 3 3 1-1-4-4-4 4z", event -> addToWishlist(product));

            actionsBox.getChildren().addAll(buyIcon, wishlistIcon);
        }

        productBox.getChildren().add(actionsBox);

        return productBox;
    }

    private StackPane createIcon(String svgContent, EventHandler<MouseEvent> onClickAction) {
        StackPane icon = new StackPane();
        icon.setOnMouseClicked(onClickAction);
        icon.getStyleClass().add("circle-icon");
        icon.setMaxSize(40, 40);

        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(javafx.scene.paint.Color.BLACK);
        svgPath.setScaleX(1.5);
        svgPath.setScaleY(1.5);

        icon.getChildren().add(svgPath);
        return icon;
    }

    private ImageView createProductImageView(Product product) {
        String imagePath = "/com/shop/projet_shop/Images/" + product.getImagePath();
        System.out.println(imagePath);
        URL imageUrl = getClass().getResource(imagePath);

        ImageView productImage = new ImageView();
        productImage.setFitWidth(200);
        productImage.setFitHeight(200);

        if (imageUrl != null) {
            productImage.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            loadDefaultImage(productImage);
        }

        return productImage;
    }

    private void loadDefaultImage(ImageView productImage) {
        URL defaultImageUrl = getClass().getResource("/com/shop/projet_shop/Images/default.jpg");
        if (defaultImageUrl != null) {
            productImage.setImage(new Image(defaultImageUrl.toExternalForm()));
        }
    }

    private ObservableList<Product> getProductsFromDatabase() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT id,name, description, price, stock_quantity, created_at, image_path FROM products";

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
            showErrorAlert("Erreur lors de la récupération des produits", e.getMessage());
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addProduct() {
        try {
            String productName = name.getText();
            String descriptionText = description.getText();
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            double price = Double.parseDouble(priceTextField.getText());
            String imagePath = imagePathLabel.getText();

            if (productName.isEmpty() || descriptionText.isEmpty() || imagePath.isEmpty()) {
                showErrorAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

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
                    showErrorAlert("Erreur", "Échec de l'ajout du produit.");
                }
            }
        } catch (NumberFormatException | SQLException e) {
            showErrorAlert("Erreur lors de l'ajout du produit", e.getMessage());
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
                showInfoAlert("Succès", "Produit supprimé avec succès.");
                refreshUI();
            } else {
                showErrorAlert("Erreur", "Échec de la suppression du produit.");
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la suppression", e.getMessage());
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

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du produit au panier.");
        }
    }
    private void addToWishlist(Product product) {
        int userId = UserSession.getId();
        System.out.println(userId);

        // Vérification si le produit est déjà dans la wishlist de l'utilisateur
        String checkQuery = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND product_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, product.getId());

            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Si un doublon est trouvé
                System.out.println("Ce produit est déjà dans votre wishlist.");
                return; // On quitte la méthode sans ajouter de doublon
            }

            // Si aucune ligne n'est retournée, le produit peut être ajouté à la wishlist
            String insertQuery = "INSERT INTO wishlist (user_id, product_id, added_at) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, product.getId());
                insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                insertStmt.executeUpdate();
                System.out.println("Produit ajouté à la wishlist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de base de données : " + e.getMessage());
        }
    }















    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getName());
            saveImage(selectedFile);
        } else {
            showErrorAlert("Aucun fichier sélectionné", "Veuillez sélectionner une image valide.");
        }
    }

    private void saveImage(File sourceFile) {
        String targetDirectory = "src/main/resources/com/shop/projet_shop/Images";
        File targetFile = new File(targetDirectory, sourceFile.getName());

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copiée avec succès : " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            showErrorAlert("Erreur lors de la copie de l'image", e.getMessage());
        }
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
            showErrorAlert("Erreur lors de la récupération des noms des produits", e.getMessage());
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

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
