package com.shop.projet_shop.User;

import com.shop.projet_shop.DataBase.Users;
import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {

    @FXML
    private TableView<Users> userTable;
    @FXML
    private TableColumn<Users, Integer> idColumn;
    @FXML
    private TableColumn<Users, String> nameColumn;
    @FXML
    private TableColumn<Users, String> emailColumn;
    @FXML
    private TableColumn<Users, String> roleColumn;
    @FXML
    private TableColumn<Users, String> dateColumn;
    @FXML
    private TableColumn<Users, Void> deleteColumn;
    @FXML
    private Label messageLabel;
    @FXML
    private TextField nameField, emailField, phoneField, adresseField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;

    public void initialize() {
        if (idColumn != null) {
            setupTableColumns();
        }
        if (userTable != null) {
            loadUsersIntoTable();
        }

    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        deleteUser(user.getName());
                        loadUsersIntoTable();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void loadUsersIntoTable() {
        userTable.setItems(fetchUsersFromDatabase());
    }

    private ObservableList<Users> fetchUsersFromDatabase() {
        ObservableList<Users> users = FXCollections.observableArrayList();
        String sql = "SELECT id, username, email, role, created_at FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new Users(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return users;
    }

    @FXML
    private void deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();

            messageLabel.setText(rowsAffected > 0
                    ? "Utilisateur supprimé : " + username
                    : "Aucun utilisateur supprimé. Vérifiez les données.");
            messageLabel.setVisible(true);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    @FXML
    private void saveProfile(ActionEvent event) {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newAddress = adresseField.getText().trim();

        if (newName.isEmpty() && newEmail.isEmpty() && newPhone.isEmpty() && newAddress.isEmpty()) {
            messageLabel.setText("Veuillez remplir au moins un champ pour mettre à jour.");
            messageLabel.setVisible(true);
            return;
        }

        StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET ");
        boolean first = true;

        if (!newName.isEmpty()) {
            sqlBuilder.append("username = ?");
            first = false;
        }
        if (!newEmail.isEmpty()) {
            sqlBuilder.append(first ? "email = ?" : ", email = ?");
            first = false;
        }
        if (!newPhone.isEmpty()) {
            sqlBuilder.append(first ? "phone_number = ?" : ", phone_number = ?");
            first = false;
        }
        if (!newAddress.isEmpty()) {
            sqlBuilder.append(first ? "address = ?" : ", address = ?");
        }
        sqlBuilder.append(" WHERE username = ?");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlBuilder.toString())) {

            int index = 1;

            if (!newName.isEmpty()) stmt.setString(index++, newName);
            if (!newEmail.isEmpty()) stmt.setString(index++, newEmail);
            if (!newPhone.isEmpty()) stmt.setString(index++, newPhone);
            if (!newAddress.isEmpty()) stmt.setString(index++, newAddress);

            stmt.setString(index, UserSession.getCurrentUser());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Profil mis à jour avec succès !");
            } else {
                messageLabel.setText("Erreur : aucune mise à jour effectuée. Vérifiez vos données.");
            }
            messageLabel.setVisible(true);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du profil : " + e.getMessage());
            messageLabel.setText("Une erreur est survenue lors de la mise à jour.");
            messageLabel.setVisible(true);
        }
    }


    // MODIFIER LE MOT DE PASSE
    @FXML
    private void savePassword(ActionEvent event) {
        if (passwordField.getText().equals(confirmPasswordField.getText())) {
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            try (Connection connection = DatabaseConnection.getConnection()){
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, passwordField.getText());
                stmt.setString(2,UserSession.getCurrentUser());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    messageLabel.setText("Profil mis à jour avec succès !");
                } else {
                    messageLabel.setText("Erreur : aucune mise à jour effectuée. Vérifiez vos données.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            messageLabel.setText("les deux mots de passes ne sont pas cohérents ");
            messageLabel.setVisible(true);
        }
    }

}