package com.shop.projet_shop.Login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignInView {

    private Parent view;

    public SignInView() throws IOException {
        this.view = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Login/signin.fxml"));

    }

    public Parent getView() {
        return view;
    }
}
