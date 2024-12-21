package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;

public class Commande {
    private IntegerProperty id;
    private StringProperty user;
    private DoubleProperty price;
    private StringProperty status;
    private StringProperty creationDate;

    public Commande(int id, String user, double price, String status, String creationDate) {
        this.id = new SimpleIntegerProperty(id);
        this.user = new SimpleStringProperty(user);
        this.price = new SimpleDoubleProperty(price);
        this.status = new SimpleStringProperty(status);
        this.creationDate = new SimpleStringProperty(creationDate);
    }
    public IntegerProperty idProperty() {return id;}
    public StringProperty userProperty() {return user;}
    public DoubleProperty priceProperty() {return price;}
    public StringProperty statusProperty() {return status;}
    public StringProperty creationDateProperty() {return creationDate;}

    // Getters and setters pour les propriétés

    public int getId() {return id.get();}
    public void setId(int id) {this.id.set(id);}
    public String getUser() {return user.get();}
    public void setUser(String user) {this.user.set(user);}
    public double getPrice() {return price.get();}
    public void setPrice(double price) {this.price.set(price);}
    public String getStatus() {return status.get();}
    public void setStatus(String status) {this.status.set(status);}
    public String getCreationDate() {return creationDate.get();}
    public void setCreationDate(String creationDate) {this.creationDate.set(creationDate);}
}
