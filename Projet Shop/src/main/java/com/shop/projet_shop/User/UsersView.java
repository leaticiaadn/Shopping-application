package com.shop.projet_shop.User;

import com.shop.projet_shop.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersView {

    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private BorderPane contentPane;
    @FXML
    private Label userLabel;
    @FXML
    private MenuButton userGestionMenu;
    @FXML
    private MenuButton productGestionMenu;
    @FXML
    private MenuButton commandeGestionMenu;
    @FXML
    private MenuButton userMenu;


    @FXML
    public void initialize() throws SQLException {
        String username = UserSession.getCurrentUser();
        System.out.println("Utilisateur connecté : " + username);
        userLabel.setText(username);

        String role = UserSession.getRole();
        System.out.println("Rôle de l'utilisateur : " + role);

        // Configuration des éléments en fonction du rôle
        if (role.equals("admin")) {
            // Options pour l'administrateur
            userGestionMenu.setVisible(true);
            productGestionMenu.setVisible(true);
            commandeGestionMenu.setVisible(true);
            userMenu.setVisible(false);
        } else if (role.equals("user")) {
            // Options pour un utilisateur standard
            userGestionMenu.setVisible(false);
            productGestionMenu.setVisible(false);
            commandeGestionMenu.setVisible(false);
            userMenu.setVisible(true);
        } else if (role.equals("gestionnaire")) {
            // Options pour un gestionnaire
            userGestionMenu.setVisible(false);
            productGestionMenu.setVisible(true);
            commandeGestionMenu.setVisible(true);
            userMenu.setVisible(false);
        } else {
            // Rôle inconnu, tout cacher par sécurité
            userGestionMenu.setVisible(false);
            productGestionMenu.setVisible(false);
            commandeGestionMenu.setVisible(false);
            userMenu.setVisible(false);
        }
    }


    @FXML
    public void UserView(ActionEvent event) throws IOException {
        Parent addUser = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewUsers.fxml"));
        contentPane.setCenter(addUser);
    }
    @FXML
    public void AddUserView(ActionEvent event) throws IOException {
        Parent addUser = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/addUser.fxml"));
        contentPane.setCenter(addUser);
    }

    @FXML
    public void DeleteUserView(ActionEvent event) throws IOException {
        Parent DeleteUser = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/deleteUser.fxml"));
        contentPane.setCenter(DeleteUser);
    }

    // GESTION DE PRODUIT :
    public void ProductView(ActionEvent event) throws IOException {
        Parent productPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewProducts.fxml"));
        contentPane.setCenter(productPage);
    }


    public void AddProductView(ActionEvent event) throws IOException {
        Parent addProduct = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/addProduct.fxml"));
        contentPane.setCenter(addProduct);
    }

    public void DeleteProductView(ActionEvent event) throws IOException {
        Parent deleteProduct = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/deleteProduct.fxml"));
        contentPane.setCenter(deleteProduct);
    }

    // GESTION DES COMMANDES
    public void CommandeView(ActionEvent event) throws IOException {
        Parent commandePage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewCommandes.fxml"));
        contentPane.setCenter(commandePage);
    }

    public void SetCommandeView(ActionEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/setCommandes.fxml"));
        contentPane.setCenter(setCommande);
    }

    // PANIER
    public void PanierView(MouseEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/panierView.fxml"));
        contentPane.setCenter(setCommande);
    }
    //WISH LIST
    @FXML
    public void WishListView(MouseEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/wishListView.fxml"));
        contentPane.setCenter(setCommande);
    }

    //Modifier le profil d'un user
    public void editProfile(ActionEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/setProfil.fxml"));
        contentPane.setCenter(setCommande);
    }
    // modifier le mot de passe de la personne
    public void editPassword(ActionEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/setPassword.fxml"));
        contentPane.setCenter(setCommande);
    }



    // LOGOUT
    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/projet_shop/cartui.fxml"));
        Parent acceuilPage = loader.load();
        contentPane.setTop(null);
        contentPane.setCenter(acceuilPage);
    }
}
