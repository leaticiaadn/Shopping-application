package com.shop.projet_shop;

import com.shop.projet_shop.Login.LoginView;
import com.shop.projet_shop.Login.SignInView;
import com.shop.projet_shop.User.UserSession;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
public class AppController {
    private static AppController instance;

    @FXML
    BorderPane contentPane;
    @FXML
    private HBox topNavNotConnected;
    @FXML
    private HBox topNavConnected;
    @FXML
    private ScrollPane autoScrollPane;
    @FXML
    private Label userLabel;
    @FXML
    private StackPane productIcon;
    @FXML
    private StackPane userIcon;
    @FXML
    private StackPane wishIcon;
    @FXML
    private StackPane cartIcon;
    @FXML
    private StackPane commandeIcon;
    @FXML
    private MenuButton userMenu;
    @FXML
    private MenuItem commandeItem;
    @FXML
    private TextField search;
    @FXML
    public void initialize() {
        if (topNavNotConnected != null) {
            if (UserSession.getRole()== null) {
                UserSession.setRole("guest");
                UserSession.setId(-1);
            }
            if (UserSession.getRole().equals("guest")){
                topNavNotConnected.setVisible(true);
                System.out.print("je ne suis pas connecté ");
                topNavConnected.setVisible(false);
            }else{
                topNavNotConnected.setVisible(false);
                System.out.print("je suis connecté ");
                topNavConnected.setVisible(true);
            }
        }

        if (userMenu != null) {
            String username = UserSession.getCurrentUser();
            userLabel.setText(username);
            String role = UserSession.getRole();
            if (role.equals("admin")) {
                userIcon.setVisible(true);
                productIcon.setVisible(true);
                commandeIcon.setVisible(true);
                userMenu.setVisible(true);
                wishIcon.setVisible(false);
                cartIcon.setVisible(false);
                commandeItem.setVisible(false);
            } else if (role.equals("user")) {
                userIcon.setVisible(false);
                productIcon.setVisible(false);
                commandeIcon.setVisible(false);
                userMenu.setVisible(true);
                wishIcon.setVisible(true);
                cartIcon.setVisible(true);
                commandeItem.setVisible(true);
            } else if (role.equals("gestionnaire")) {
                userIcon.setVisible(false);
                productIcon.setVisible(true);
                commandeIcon.setVisible(true);
                userMenu.setVisible(true);
                wishIcon.setVisible(false);
                cartIcon.setVisible(false);
                commandeItem.setVisible(false);
            } else {
                userIcon.setVisible(false);
                productIcon.setVisible(false);
                commandeIcon.setVisible(false);
                userMenu.setVisible(false);
            }
        }
        if (autoScrollPane != null) {
            double[] pages = {0, 0.5, 1};
            Timeline timeline = new Timeline();
            Duration pauseDuration = Duration.seconds(5);
            Duration currentTime = Duration.ZERO;
            for (int i = 0; i < pages.length; i++) {
                KeyValue kv = new KeyValue(autoScrollPane.hvalueProperty(), pages[i], Interpolator.EASE_BOTH);
                KeyFrame kf = new KeyFrame(currentTime.add(pauseDuration), kv);
                timeline.getKeyFrames().add(kf);
                currentTime = currentTime.add(pauseDuration);
            }
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public AppController() {
        instance = this;
    }
    public void showAcceuil(MouseEvent event) throws IOException {
        Parent managerPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/cartui.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(managerPage);
    }
    public void showInterface(ActionEvent event) throws IOException {
        Parent managerPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/cartui.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(managerPage);

    }

    public void closeApp() {
        App.getWindow().close();
    }

    public void showHomeView() throws IOException {
        Parent productPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewProducts.fxml"));
        contentPane.setCenter(productPage);
    }

    public void showCartView() throws IOException {
        Parent productPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/panierView.fxml"));
        contentPane.setCenter(productPage);
    }

    public void showLoginView() throws IOException {
        contentPane.setCenter(new LoginView().getView());
    }

    public void showSignInView() throws IOException {
        contentPane.setCenter(new SignInView().getView());
    }
    public void showConnectionMethode(MouseEvent event) throws IOException {
        Parent connection = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Login/login.fxml"));
        contentPane.setCenter(connection);
    }
    @FXML
    public void UserView(MouseEvent event) throws IOException {
        Parent addUser = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewUsers.fxml"));
        contentPane.setCenter(addUser);
    }

    // GESTION DE PRODUIT :
    public void ProductView(MouseEvent event) throws IOException {
        Parent productPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewProducts.fxml"));
        contentPane.setCenter(productPage);
    }

    // GESTION DES COMMANDES
    public void CommandeView(MouseEvent event) throws IOException {
        Parent commandePage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/Admin/viewCommandes.fxml"));
        contentPane.setCenter(commandePage);
    }
    //WISH LIST
    @FXML
    public void WishListView(MouseEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/wishListView.fxml"));
        contentPane.setCenter(setCommande);
    }

    //Modifier le profil d'un user
    public void editProfile(ActionEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/setProfil.fxml"));
        contentPane.setCenter(setCommande);
    }
    // modifier le mot de passe de la personne
    public void editPassword(ActionEvent event) throws IOException {
        Parent setCommande = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/User/setPassword.fxml"));
        contentPane.setCenter(setCommande);
    }
    @FXML
    private void handleSearch() {
        String query = search.getText().trim();
        if (!query.isEmpty()) {
           try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/projet_shop/Admin/viewProducts.fxml"));
               Parent productPage = loader.load();
               ProductController productController = loader.getController();
               contentPane.setCenter(productPage);
               productController.refreshUI(query);
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de ProductView : " + e.getMessage());
            }
        }
    }

    // LOGOUT
    @FXML
    public void logout(ActionEvent event) {
        try {
            Parent managerPage = FXMLLoader.load(getClass().getResource("/com/shop/projet_shop/cartui.fxml"));
            UserSession.setRole("guest");
            Scene scene = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow().getScene();
            scene.setRoot(managerPage);
            scene.setRoot(managerPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
