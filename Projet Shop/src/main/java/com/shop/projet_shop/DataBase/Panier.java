package com.shop.projet_shop.DataBase;

import java.util.ArrayList;

public class Panier {
    private int id;
    private  String name;
    private  String product;
    private int quantity;

    public Panier(int id, String name, String product, int quantity) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.quantity = quantity;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
