package com.shop.projet_shop;


import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Notification {

    public static void showNotification(VBox notificationBox, String message) {
        Platform.runLater(() -> {
            Label notification = new Label(message);
            notification.getStyleClass().add("notification");
            notificationBox.getChildren().add(notification);
            notificationBox.setVisible(true);
            notificationBox.toFront();

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), notification);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> notificationBox.getChildren().remove(notification));
                fadeOut.play();
            });

            pause.play();
        });
    }
}