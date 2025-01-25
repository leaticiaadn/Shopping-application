package com.shop.projet_shop;

import com.shop.projet_shop.Home.HomeView;
import com.shop.projet_shop.Login.LoginView;
import com.shop.projet_shop.Login.SignInView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class AppController {
    private static AppController instance;

    @FXML
    BorderPane contentPane;

    @FXML
    private ComboBox<String> authOptions;

    @FXML
    private void handleAuthSelection(ActionEvent event) throws IOException {
        String selectedOption = authOptions.getValue();
        if (selectedOption != null) {
            switch (selectedOption) {
                case "Login":
                    showLoginView();
                    break;
                case "Sign In":
                    showSignInView();
                    break;
            }
        }
    }
    public AppController() {
        instance = this;
    }

    public static AppController getInstance() {
        return instance;
    }

    public void closeApp() {
        App.getWindow().close();
    }

    public void showHomeView() throws IOException {
        contentPane.setCenter(new HomeView().getView());
    }

    public void showCartView() {
        contentPane.setCenter(new Label("Cart View"));
    }

    public void showLoginView() throws IOException {
        contentPane.setCenter(new LoginView().getView());
    }

    public void showSignInView() throws IOException {
        contentPane.setCenter(new SignInView().getView());
    }





    protected void showErrorMessage(String message) {
        System.out.println(message);
    }
}
