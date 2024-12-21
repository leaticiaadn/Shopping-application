package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;

public class OrderItem {

    private final IntegerProperty id, orderId, quantity;
    private final StringProperty productName;
    private final DoubleProperty price;

    public OrderItem(int id, int orderId, String productName, int quantity, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.orderId = new SimpleIntegerProperty(orderId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty orderIdProperty() { return orderId; }
    public StringProperty productNameProperty() { return productName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty priceProperty() { return price; }

    public int getId() { return id.get(); }
    public int getOrderId() { return orderId.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }
}

