package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;

public class Product {
    private StringProperty name;
    private StringProperty description;
    private DoubleProperty price;
    private IntegerProperty stock;
    private StringProperty creationDate;

    public Product(String name, String description, double price, int stock, String creationDate) {
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.creationDate = new SimpleStringProperty(creationDate);
    }

    public StringProperty nameProperty() {return name;}
    public StringProperty descriptionProperty() {return description;}
    public DoubleProperty priceProperty() {return price;}
    public IntegerProperty stockProperty() {return stock;}
    public StringProperty creationDateProperty() {return creationDate;}

    // Getters and setters pour les propriétés
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public String getCreationDate() {
        return creationDate.get();
    }

    public void setCreationDate(String creationDate) {
        this.creationDate.set(creationDate);
    }
}
