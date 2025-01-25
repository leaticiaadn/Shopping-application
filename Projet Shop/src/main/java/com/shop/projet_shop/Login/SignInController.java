package com.shop.projet_shop.Login;

import java.io.IOException;
import java.sql.*;

import com.shop.projet_shop.AppController;
import com.shop.projet_shop.DatabaseConnection;
import com.shop.projet_shop.User.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;

public class SignInController extends AppController {

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Label errorMessage;

    @FXML
    private BorderPane contentPane;

    @FXML
    public void handleSignUp(ActionEvent event) {
        errorMessage.setVisible(false);

        if (!validateInputs()) return;

        if (!password.getText().equals(confirmPassword.getText())) {
            showError("Passwords do not match");
            return;
        }

        if (!isUserUnique()) return;

        String query = "INSERT INTO `shopping_schema`.`users` (`username`, `email`, `password`, `role`, `created_at`) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username.getText());
            statement.setString(2, email.getText());
            statement.setString(3, password.getText());
            statement.setString(4, "user");
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            statement.executeUpdate(); // Exécuter l'insertion
            UserSession.setCurrentUser(username.getText());
            UserSession.setRole("user");

            showInterface(event); // Charger l'interface utilisateur après inscription réussie

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Failed to register user. Please try again later.");
        }
    }

    private boolean validateInputs() {
        if (username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            showError("Please fill all the inputs");
            return false;
        }
        return true;
    }

    private boolean isUserUnique() {
        String emailQuery = "SELECT 1 FROM `shopping_schema`.`users` WHERE email = ?";
        String usernameQuery = "SELECT 1 FROM `shopping_schema`.`users` WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement emailCheck = connection.prepareStatement(emailQuery);
             PreparedStatement usernameCheck = connection.prepareStatement(usernameQuery)) {

            emailCheck.setString(1, email.getText());
            usernameCheck.setString(1, username.getText());

            if (emailCheck.executeQuery().next()) {
                showError("Email already exists");
                return false;
            }

            if (usernameCheck.executeQuery().next()) {
                showError("Username already exists");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }


    private void showInterface(ActionEvent event) throws IOException {
        Parent userPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/users.fxml"));
        contentPane.setCenter(userPage);
    }
}
