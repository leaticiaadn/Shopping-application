package com.shop.projet_shop.Login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignInView {

    private Parent view;

    public SignInView() throws IOException {
        URL url = new File("C:/Users/Leati/OneDrive/Documents/Master1 MIAGE/Programmation Objet/Projet/Shopping-application/Projet Shop/src/main/resources/com/shop/projet_shop/Login/signin.fxml").toURI().toURL();
        this.view =  FXMLLoader.load(url);

    }

    public Parent getView() {
        return view;
    }
}
