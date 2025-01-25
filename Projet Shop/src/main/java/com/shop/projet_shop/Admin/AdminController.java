package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;

public class AdminController {
    @FXML
    private TextField user;
    @FXML
    private TextField email;
    @FXML
    private MenuButton role;
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
        if (role != null){
            for (MenuItem item : role.getItems()) {
                item.setOnAction(event -> {
                    role.setText(item.getText());
                });
            }
        }
    }

    @FXML
    public void addUser(ActionEvent event){
        String username = user.getText();
        String pwd = password.getText();
        String emailAddress = email.getText();
        String roleName = role.getText();
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





}
