package com.shop.projet_shop.Login;

import com.shop.projet_shop.Admin.AdminController;
import com.shop.projet_shop.Admin.AdminView;
import com.shop.projet_shop.AppController;
import com.shop.projet_shop.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController extends AppController {

    @FXML
    private TextField user;
    @FXML
    private PasswordField password;


    @FXML
    public void handleLogin(ActionEvent event) throws SQLException {
        String username = user.getText();
        String pwd = password.getText();

        if (authenticateUser(username, pwd)) {
            if (isAdmin(username)) {
                showAdminInterface(event);
            } else {
                showUserInterface();
            }
        } else {
            showErrorMessage("Invalid credentials");
        }
    }

    private boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }catch (Exception e) {
            return e.getMessage().equals("Invalid username or password");
        }
    }

    private boolean isAdmin(String username) throws SQLException {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return "admin".equals(resultSet.getString("role"));  // Si le rôle est "admin"
            }
            return false;
        }
    }

    private void showUserInterface() {
        System.out.println("User interface displayed");
    }

    private void showAdminInterface(ActionEvent event) {
        AdminView adminView = new AdminView();
        adminView.showInterface(event);
    }


}
