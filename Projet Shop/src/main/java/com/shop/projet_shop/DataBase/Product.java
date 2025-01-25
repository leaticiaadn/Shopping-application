package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;

public class Product {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty description;
    private DoubleProperty price;
    private IntegerProperty stock;
    private StringProperty creationDate;
    private String imagePath;

    public Product(int id,String name, String description, double price, int stock, String ImagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        imagePath = ImagePath;
    }

    public Product(int id, String name, String description, double price, int stock, String creationDate,String ImagePath) {
        this(id,name, description, price, stock,ImagePath);
        this.creationDate = new SimpleStringProperty(creationDate);
    }

    // Getters and setters pour les propriétés
    public int getId() {
        return id.get();
    }
    public void setId(int id) {
        this.id.set(id);
    }
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getImagePath() {
        return imagePath;
    }
}
