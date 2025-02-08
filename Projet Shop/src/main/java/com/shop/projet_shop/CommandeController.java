package com.shop.projet_shop;

import com.shop.projet_shop.DataBase.Commande;
import com.shop.projet_shop.User.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandeController {

    private static final Logger LOGGER = Logger.getLogger(CommandeController.class.getName());

    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> idColumn;
    @FXML
    private TableColumn<Commande, String> userColumn;
    @FXML
    private TableColumn<Commande, Double> priceColumn;
    @FXML
    private TableColumn<Commande, String> statusColumn;
    @FXML
    private TableColumn<Commande, String> dateColumn;
    @FXML
    private TableColumn<Commande, Void> detailsColumn;
    @FXML
    private TableColumn<Commande, Void> editColumn;
    @FXML
    private TableColumn<Commande, Void> deleteColumn;
    @FXML
    private TextField searchField;

    private String role = UserSession.getRole();
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("annulée", "livrée", "expédiée", "en attente");

    public void initialize() {
        if (idColumn != null) {
            setupTableColumns();
            setupStatusColumn();
            configureColumnVisibility();
            setupDetailsButton();
            setupEditDeleteButtons();
            commandeTable.setItems(fetchCommandeData());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        userColumn.setCellValueFactory(cellData -> cellData.getValue().userProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());
    }

    private void setupStatusColumn() {
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(statusOptions)));
        statusColumn.setEditable(true);
        commandeTable.setEditable(true);
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        statusColumn.setOnEditCommit(event -> {
            Commande commande = event.getRowValue();
            commande.setStatus(event.getNewValue());
            updateCommandeStatusInDatabase(commande);
        });
    }

    private void configureColumnVisibility() {
        if (role.equals("user")) {
            commandeTable.getColumns().remove(userColumn);
            commandeTable.getColumns().remove(editColumn);
            commandeTable.getColumns().remove(deleteColumn);
        }
    }

    private void setupDetailsButton() {
        if (detailsColumn != null) {
            detailsColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
                private final Button detailButton = new Button("Détails");

                {
                    detailButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        if (commande != null) {
                            showDetails(commande);
                        }
                    });
                    setGraphic(detailButton);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : detailButton);
                }
            });
        }
    }

    private void setupEditDeleteButtons() {
        if (editColumn != null && !role.equals("user")) {
            setupEditButton();
            setupDeleteButton();
        }
    }

    private void setupEditButton() {
        editColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button editButton = new Button("Modifier");

            {
                editButton.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    editCommande(commande);
                });
                setGraphic(editButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
    }

    private void setupDeleteButton() {
        deleteColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    deleteCommande(commande);
                });
                setGraphic(deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private ObservableList<Commande> fetchCommandeData() {
        return fetchCommandeDataFromDatabase("SELECT * FROM orders INNER JOIN users ON users.id = orders.user_id", null);
    }

    private ObservableList<Commande> fetchCommandeDataWithSearch() {
        String searchValue = searchField.getText().trim();
        String sql = "SELECT * FROM orders INNER JOIN users ON users.id = orders.user_id WHERE 1=1";
        return fetchCommandeDataFromDatabase(sql + (searchValue.isEmpty() ? "" : " AND (orders.status LIKE ? OR users.username LIKE ? OR orders.order_date LIKE ? OR CAST(orders.id AS CHAR) LIKE ?)"), searchValue);
    }

    private ObservableList<Commande> fetchCommandeDataFromDatabase(String sql, String searchValue) {
        ObservableList<Commande> commandes = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (searchValue != null && !searchValue.isEmpty()) {
                stmt.setString(1, "%" + searchValue + "%");
                stmt.setString(2, "%" + searchValue + "%");
                stmt.setString(3, "%" + searchValue + "%");
                stmt.setString(4, "%" + searchValue + "%");
            } else if (role.equals("user")) {
                stmt.setString(1, UserSession.getCurrentUser());
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("order_date")
                );
                commandes.add(commande);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des commandes : " + e.getMessage(), e);
        }
        return commandes;
    }

    @FXML
    public void onSearch(javafx.event.ActionEvent actionEvent) {
        commandeTable.setItems(fetchCommandeDataWithSearch());
    }

    private void showDetails(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/projet_shop/Admin/commandeDetails.fxml"));
            Parent root = loader.load();
            CommandeDetailController controller = loader.getController();
            controller.initialize(commande.getId());
            Stage detailStage = new Stage();
            detailStage.setTitle("Détails de la commande");
            detailStage.setScene(new Scene(root, 600, 400));
            detailStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage des détails : " + e.getMessage(), e);
        }
    }

    private void editCommande(Commande commande) {

    }

    private void deleteCommande(Commande commande) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM orders WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, commande.getId());
                stmt.executeUpdate();
                commandeTable.getItems().remove(commande);
                System.out.println("Commande supprimée mais facture conservée : " + commande.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la commande : " + e.getMessage(), e);
        }
    }

    private void updateCommandeStatusInDatabase(Commande commande) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, commande.getStatus());
            stmt.setInt(2, commande.getId());
            stmt.executeUpdate();
            System.out.println("Statut de la commande mis à jour : " + commande.getStatus());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du statut : " + e.getMessage(), e);
        }
    }
}
