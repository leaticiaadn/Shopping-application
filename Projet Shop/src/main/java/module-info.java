module com.shop.projet_shop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    opens com.shop.projet_shop to javafx.fxml;
    exports com.shop.projet_shop;
    exports com.shop.projet_shop.Home;
    opens com.shop.projet_shop.Home to javafx.fxml;
    exports com.shop.projet_shop.Login;
    opens com.shop.projet_shop.Login to javafx.fxml;
    exports com.shop.projet_shop.Admin;
    opens com.shop.projet_shop.Admin to javafx.fxml;
    exports com.shop.projet_shop.User;
    opens com.shop.projet_shop.User to javafx.fxml;
    exports com.shop.projet_shop.DataBase;
    opens com.shop.projet_shop.DataBase to javafx.fxml;
}