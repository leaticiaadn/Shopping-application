package com.shop.projet_shop.Login;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import com.shop.projet_shop.AppController;
import com.shop.projet_shop.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;

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
    public void handleSignUp(ActionEvent event) {
        String query = "INSERT INTO `shopping_schema`.`users` (`username`, `email`, `password`, `role`,`created_at`) VALUES (?, ?, ?, ?,?)";
        if (!password.getText().equals(confirmPassword.getText())) {
            System.out.println("Passwords do not match");
            return;
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username.getText());
            statement.setString(2, email.getText());
            statement.setString(3, password.getText());
            statement.setString(4, "user");
            statement.setString(5, new Date(System.currentTimeMillis()).toString());

            statement.executeUpdate();
            System.out.println("connection success");
            showUserInterface();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("connection failed");
        }
        
    }
    public void showUserInterface() {

    }

}
