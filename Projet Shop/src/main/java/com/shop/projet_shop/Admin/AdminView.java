package com.shop.projet_shop.Admin;

import com.shop.projet_shop.AppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminView {

    @FXML
    private ComboBox<String> userComboBox;

    private Stage stage = new Stage();
    @FXML
    public void showInterface(ActionEvent event) {
        try {
            Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/admin.fxml"));
            Scene adminScene = new Scene(adminPage);
            stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setWidth(800);
            stage.setHeight(600);
            stage.setScene(adminScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AddUserView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/addUser.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }

    @FXML
    public void DeleteUserView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/deleteUser.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }

    // GESTION DE PRODUIT :

    public void ProductView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewProducts.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }

    public void AddProductView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/addProduct.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }
    public void DeleteProductView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/deleteProduct.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }


    @FXML
    public void logout(ActionEvent event) throws IOException {
        AppController appController = new AppController();
        appController.showLoginView();
    }

    // GESTION DES COMMANDES
    public void CommandeView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewCommandes.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }

    public void SetCommandeView(ActionEvent event) throws IOException {
        Parent adminPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/setCommandes.fxml"));
        Scene adminScene = new Scene(adminPage);
        stage.setScene(adminScene);
        stage.show();
    }
}
