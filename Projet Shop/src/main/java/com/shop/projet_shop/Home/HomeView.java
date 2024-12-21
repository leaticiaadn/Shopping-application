package com.shop.projet_shop.Home;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class HomeView {

    private final Parent view;

    public HomeView() throws IOException {
        URL url = new File("C:/Users/Leati/OneDrive/Documents/Master1 MIAGE/Programmation Objet/Projet/Shopping-application/Projet Shop/src/main/resources/com/shop/projet_shop/Home/home.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        this.view = root;
    }

    public Parent getView() {
        return view;
    }
}
