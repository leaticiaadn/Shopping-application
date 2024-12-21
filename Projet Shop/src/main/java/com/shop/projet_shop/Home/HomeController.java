package com.shop.projet_shop.Home;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HomeController {
    @FXML
    private GridPane productGridPane;

    @FXML
    public void initialize() {
        System.out.println("HomeController initialized");
        productGridPane.getChildren().clear();
        Label label1 = new Label(Product.APPLE.name());
        label1.setPadding(new Insets(5,5,5,5));

        productGridPane.add(label1, 0,0);

        Label label2 = new Label(Product.MILK.name());
        label2.setPadding(new Insets(5,5,5,5));

        productGridPane.add(label2, 1,0);

        Label label3 = new Label(Product.JUICE.name());
        label3.setPadding(new Insets(5,5,5,5));

        productGridPane.add(label3, 2,0);

        Label label4 = new Label(Product.LETTUCE.name());
        label4.setPadding(new Insets(5,5,5,5));

        productGridPane.add(label4, 0,1);

    }
}
