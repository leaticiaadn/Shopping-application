package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DataBase.Users;
import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {
    @FXML
    private TextField user;
    @FXML
    private TextField email;
    @FXML
    private ComboBox<String> roleBox;
    @FXML
    private PasswordField password;
    @FXML
    private TableView<Users> userTable;
    @FXML
    private TableColumn<Users, Number> idColumn;
    @FXML
    private TableColumn<Users, String> nameColumn;
    @FXML
    private TableColumn<Users, String> emailColumn;
    @FXML
    private TableColumn<Users, String> roleColumn;
    @FXML
    private TableColumn<Users, String> dateColumn;
    @FXML
    private TableColumn<Users, String> deleteColumn;
    @FXML
    private Label errorMessage;
    @FXML
    private TextField priceTextField;
    private static String role;

    @FXML
    public void initialize() {
        // Validation pour les prix
        if (priceTextField != null){
            priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    priceTextField.setText(oldValue);
                }
            });
        }

        // Initialisation de la TableView
        if (userTable != null) {
            idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
            dateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());
            deleteColumn.setCellFactory(param -> new TableCell<Users, String>() {
                private final Button deleteButton = new Button();

                {
                    String trashSVG = "M3 6h18M8 6V4a1 1 0 011-1h6a1 1 0 011 1v2m4 0h-4m-6 0H3m3 0v12a2 2 0 002 2h8a2 2 0 002-2V6m-8 4v6m4-6v6";
                    StackPane trashIcon = createIcon(trashSVG, "#000000", 1.5);
                    deleteButton.setGraphic(trashIcon);
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                    deleteButton.setOnAction(event -> {
                        Users user = getTableView().getItems().get(getIndex());
                        if (user != null) {
                            showConfirmationDialog(user);
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new HBox(deleteButton));
                    }
                }

                private void showConfirmationDialog(Users user) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Supprimer l'utilisateur ?");
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer " + user.getName() + " ?");

                    ButtonType buttonYes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonYes, buttonNo);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == buttonYes) {
                            deleteUser(user.getId());
                            getTableView().getItems().remove(user);
                            System.out.println("Utilisateur supprimé : " + user.getName());
                        }
                    });
                }
            });
            userTable.setItems(getUsers(""));
        }

        if (roleBox != null) {
            roleBox.setOnAction(event -> {
                String selectedRole = roleBox.getSelectionModel().getSelectedItem();
                if (selectedRole != null) {
                    role = selectedRole;
                }
            });
        }
    }

    @FXML
    public void addUser() {
        String username = user.getText();
        String pwd = password.getText();
        String emailAddress = email.getText();
        if (username.isEmpty() || emailAddress.isEmpty() || pwd.isEmpty() || role == null) {
            errorMessage.setVisible(true);
            errorMessage.setText("Veuillez remplir tous les champs");
        }else{
            String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, username);
                stmt.setString(2, pwd);
                stmt.setString(3, emailAddress);
                stmt.setString(4, role);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    errorMessage.setVisible(true);
                    errorMessage.setStyle("-fx-background-color: green; -fx-cursor: hand;");
                    errorMessage.setText("Utilisateur ajouté avec succées !");
                    user.clear();
                    email.clear();
                    password.clear();
                    roleBox.getSelectionModel().clearSelection();

                    userTable.setItems(getUsers(""));

                } else {
                    errorMessage.setVisible(true);
                    errorMessage.setText("Échec de l'ajout de l'utilisateur.");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'insertion : " + e.getMessage());
            }
        }

    }

    public ObservableList<Users> getUsers(String searchText) {
        ObservableList<Users> users = FXCollections.observableArrayList();
        String sql = "SELECT id, username, email, role, created_at FROM users";

        if (!searchText.isEmpty()) {
            sql += " WHERE id LIKE ? OR username LIKE ? OR role LIKE ? OR email LIKE ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (!searchText.isEmpty()) {
                String searchValue = "%" + searchText + "%";
                stmt.setString(1, searchValue);
                stmt.setString(2, searchValue);
                stmt.setString(3, searchValue);
                stmt.setString(4, searchValue);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("username");
                    String email = rs.getString("email");
                    String role = rs.getString("role");
                    String creationDate = rs.getString("created_at");

                    users.add(new Users(id, name, email, role, creationDate));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
        }

        return users;
    }

    @FXML
    private void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
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


}
