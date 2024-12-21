package com.shop.projet_shop.Admin;

import com.shop.projet_shop.DataBase.Commande;
import com.shop.projet_shop.DataBase.Facture;
import com.shop.projet_shop.DataBase.OrderItem;
import com.shop.projet_shop.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CommandeDetailController {

    @FXML private TableView<OrderItem> orderItemTable;
    @FXML private TableColumn<OrderItem, String> productColumn;
    @FXML private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML private TableColumn<OrderItem, Double> totalColumn;

    @FXML private TableView<Facture> factureTable;
    @FXML private TableColumn<Facture, Double> amountColumn;
    @FXML private TableColumn<Facture, String> dateFactureColumn;

    public void initialize(int orderId) {
        productColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        dateFactureColumn.setCellValueFactory(cellData -> cellData.getValue().factureDateProperty().asString());

        orderItemTable.setItems(getOrderItems(orderId));
        factureTable.setItems(getFactures(orderId));
    }


    private ObservableList<OrderItem> getOrderItems(int orderId) {
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_item INNER JOIN products ON order_item.product_id = products.id WHERE order_id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String productName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                orderItems.add(new OrderItem(rs.getInt("id"), orderId, productName, quantity, price));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des éléments de la commande : " + e.getMessage());
        }

        return orderItems;
    }

    private ObservableList<Facture> getFactures(int orderId) {
        ObservableList<Facture> factures = FXCollections.observableArrayList();
        String sql = "SELECT * FROM facture WHERE order_id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                Timestamp factureDate = rs.getTimestamp("facture_date");
                boolean paid = rs.getBoolean("paid");
                factures.add(new Facture(rs.getInt("id"), orderId, factureDate, amount, paid));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des factures : " + e.getMessage());
        }
        return factures;
    }

}
