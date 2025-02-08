package com.shop.projet_shop.Login;

import com.shop.projet_shop.App;
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

import java.io.IOException;
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
    private Label errorMessage;

    @FXML
    public void handleLogin(ActionEvent event) throws SQLException, IOException {
        String username = user.getText();
        String pwd = password.getText();
        if (checkInputs()){
            if (authenticateUser(username, pwd)) {
                UserSession.setCurrentUser(username);
                UserSession.setRole(defineRole(username));
                showInterface(event);
            } else {
                errorMessage.setText("Invalid credentials");
                errorMessage.setVisible(true);
            }
        }

    }


    public String getUsername() throws SQLException {
        return user.getText();
    }
    private boolean checkInputs(){
        if (user.getText().isEmpty() || password.getText().isEmpty()) {
            errorMessage.setVisible(true);
            errorMessage.setText("Please enter your username and password");
            return false;
        }
        return true;
    }

    private boolean authenticateUser(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Veuillez remplir tous les champs.");
            errorMessage.setVisible(true);
            return false;
        }

        String query = "SELECT id,  username FROM users WHERE username = ? and password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                UserSession.setId(resultSet.getInt("id"));
                return true;
            } else {
                errorMessage.setText("Nom d'utilisateur ou mot de passe incorrect.");
                errorMessage.setVisible(true);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Pour le débogage
            errorMessage.setText("Erreur de connexion à la base de données.");
            errorMessage.setVisible(true);
            return false;
        }
    }


    private String defineRole(String username) throws SQLException {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("role");
            } else {
                return "unknown";
            }
        }
    }


}
