package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DataBase.Commande;
import com.shop.projet_shop.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandeController {

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
    private TableColumn<Commande, Void> editColumn;  // Colonne pour le bouton Modifier
    @FXML
    private TableColumn<Commande, Void> deleteColumn; // Colonne pour le bouton Supprimer
    @FXML
    private TextField searchField;

    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("annulée", "livrée", "expédiée", "en attente");

    public void initialize() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
            userColumn.setCellValueFactory(cellData -> cellData.getValue().userProperty());
            priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
            statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
            dateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());

            statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
            statusColumn.setOnEditCommit(event -> {
                Commande commande = event.getRowValue();
                commande.setStatus(event.getNewValue());
                updateCommandeStatusInDatabase(commande);
            });
            if (detailsColumn != null) {
                detailsColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
                    private final Button detailButton = new Button("Détails");

                    {
                        detailButton.setOnAction(event -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            if (commande != null) {
                                detailButton(commande);
                            }
                        });

                        setGraphic(detailButton);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(detailButton);
                        }
                    }
                });
            }

            if (editColumn != null) {
                editColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
                    private final Button editButton = new Button("Modifier");
                    {
                        editButton.setOnAction(event -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            EditCommand(commande);
                        });
                        setGraphic(editButton);
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(editButton);
                        }
                    }
                });
                deleteColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
                    private final Button deleteButton = new Button("Supprimer");
                    {
                        deleteButton.setOnAction(event -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            DeleteCommand(commande);
                        });
                        setGraphic(deleteButton);
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                });
            }
            commandeTable.setItems(getProductsFromDatabase());
        }
    }

    private ObservableList<Commande> getProductsFromDatabase() {
        ObservableList<Commande> commandes = FXCollections.observableArrayList();
        String sql = "SELECT * FROM orders INNER JOIN users ON users.id = orders.user_id";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                double price = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String creationDate = rs.getString("order_date");

                Commande commande = new Commande(id, user, price, status, creationDate);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }
        return commandes;
    }

    private ObservableList<Commande> getProductsFromDatabaseWithPresification() {
        String value = searchField.getText().trim();
        ObservableList<Commande> commandes = FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM orders INNER JOIN users ON users.id = orders.user_id WHERE 1=1"
        );

        if (!value.isEmpty()) {
            sql.append(" AND (orders.status LIKE ? OR users.username LIKE ? OR orders.order_date LIKE ? OR CAST(orders.id AS CHAR) LIKE ?)");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            if (!value.isEmpty()) {
                stmt.setString(1, "%" + value + "%");
                stmt.setString(2, "%" + value + "%");
                stmt.setString(3, "%" + value + "%");
                stmt.setString(4, "%" + value + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                double price = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String creationDate = rs.getString("order_date");

                Commande commande = new Commande(id, user, price, status, creationDate);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

    @FXML
    public void onSearch(javafx.event.ActionEvent actionEvent) {
        commandeTable.setItems(getProductsFromDatabaseWithPresification());
    }

    private void detailButton(Commande commande) {
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
            e.printStackTrace();
        }
    }


    private void EditCommand(Commande commande) {
        System.out.println("Modifier la commande : " + commande.getId());
    }

    private void DeleteCommand(Commande commande) {
        String deleteFacturesSql = "DELETE FROM facture WHERE order_id = ?";
        String deleteOrderItemsSql = "DELETE FROM order_item WHERE order_id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmtFactures = connection.prepareStatement(deleteFacturesSql);
            PreparedStatement stmtOrderItems = connection.prepareStatement(deleteOrderItemsSql);
            stmtFactures.setInt(1, commande.getId());
            stmtOrderItems.setInt(1, commande.getId());
            stmtFactures.executeUpdate();
            stmtOrderItems.executeUpdate();

            String sql = "DELETE FROM orders WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, commande.getId());
            stmt.executeUpdate();

            commandeTable.getItems().remove(commande);
            System.out.println("Commande et factures supprimées : " + commande.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la commande : " + e.getMessage());
        }
    }

    private void updateCommandeStatusInDatabase(Commande commande) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, commande.getStatus());
            stmt.setInt(2, commande.getId());
            stmt.executeUpdate();
            System.out.println("Statut de la commande mis à jour : " + commande.getStatus());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }
}
