import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL file = new File(
                "/Users/Leati/OneDrive/Documents/Master1 MIAGE/Programmation Objet/Projet/Shopping-application/Shop/src/main/java/cartui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(file);

        stage.setTitle("Shopping Application");

        stage.setScene(new Scene(root, Color.TRANSPARENT));

        stage.show();
    }
}
