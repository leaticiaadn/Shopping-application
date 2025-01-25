package com.shop.projet_shop.DataBase;

import javafx.beans.property.*;

public class Users {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty creationDate = new SimpleStringProperty();

    public Users(int id, String name, String email, String role, String creationDate) {
        this.id.set(id);
        this.name.set(name);
        this.email.set(email);
        this.role.set(role);
        this.creationDate.set(creationDate);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty roleProperty() { return role; }
    public StringProperty creationDateProperty() { return creationDate; }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }

    public String getCreationDate() { return creationDate.get(); }
    public void setCreationDate(String creationDate) { this.creationDate.set(creationDate); }
}
