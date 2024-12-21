package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;
import java.sql.Timestamp;

public class Facture {

    private final IntegerProperty id, orderId;
    private final DoubleProperty amount;
    private final BooleanProperty paid;
    private final ObjectProperty<Timestamp> factureDate;

    public Facture(int id, int orderId, Timestamp factureDate, double amount, boolean paid) {
        this.id = new SimpleIntegerProperty(id);
        this.orderId = new SimpleIntegerProperty(orderId);
        this.factureDate = new SimpleObjectProperty<>(factureDate);
        this.amount = new SimpleDoubleProperty(amount);
        this.paid = new SimpleBooleanProperty(paid);
    }

    // Getters et Setters
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty orderIdProperty() { return orderId; }
    public ObjectProperty<Timestamp> factureDateProperty() { return factureDate; }
    public DoubleProperty amountProperty() { return amount; }
    public BooleanProperty paidProperty() { return paid; }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public int getOrderId() { return orderId.get(); }
    public void setOrderId(int orderId) { this.orderId.set(orderId); }

    public Timestamp getFactureDate() { return factureDate.get(); }
    public void setFactureDate(Timestamp factureDate) { this.factureDate.set(factureDate); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }

    public boolean isPaid() { return paid.get(); }
    public void setPaid(boolean paid) { this.paid.set(paid); }
}
