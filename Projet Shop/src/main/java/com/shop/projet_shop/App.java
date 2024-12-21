package com.shop.projet_shop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage window;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("cartui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("La parisienne k");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getWindow() {
        return window;
    }
}