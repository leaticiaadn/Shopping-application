package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;

public class AdminController {
    @FXML
    private TextField user;
    @FXML
    private TextField email;
    @FXML
    private ComboBox<String> role;
    @FXML
    private PasswordField password;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private TextField priceTextField;


    @FXML
    public void initialize() {
        if (userComboBox != null){
            userComboBox.setItems(getUsers());
        }
        if (priceTextField != null){
            priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {  // On permet uniquement les chiffres
                    priceTextField.setText(oldValue);  // Annuler la modification si ce n'est pas un nombre
                }
            });
        }
    }

    @FXML
    public void addUser(ActionEvent event){
        String username = user.getText();
        String pwd = password.getText();
        String emailAddress = email.getText();
        String roleName = role.getSelectionModel().getSelectedItem();
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, pwd);
            stmt.setString(3, emailAddress);
            stmt.setString(4, roleName);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilisateur ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout de l'utilisateur.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
        }
    }

    public static ObservableList<String> getUsers() {
        ObservableList<String> users = FXCollections.observableArrayList(); // Correction ici
        String sql = "SELECT username, role FROM users";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Parcourir les résultats et les ajouter à la liste
            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                users.add(username + " - " + role);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users from the database", e);
        }
        return users;
    }


    @FXML
    public void deleteUser(ActionEvent event){
        String selectedUser = userComboBox.getValue();
        String [] tab = selectedUser.split(" - ");
        System.out.println(selectedUser);
        System.out.println(tab[0]);
        System.out.println(tab[1]);
        String sql = "DELETE FROM users WHERE username = ? and role = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tab[0]);
            stmt.setString(2, tab[1]);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Suppression réussie !");
            } else {
                System.out.println("Aucun utilisateur supprimé. Vérifiez les données.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
        }
    }


}
