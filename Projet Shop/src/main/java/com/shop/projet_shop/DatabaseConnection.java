package com.shop.projet_shop;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/shopping_schema?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Core256789!";

    public static Connection getConnection() throws SQLException {
        try {
            // Charger explicitement le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC MySQL introuvable !");
            e.printStackTrace();
        }
        // Retourner la connexion
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
    // base he
    // script si c'edst la premiere je la crée
    // sinon répercuter les modif
    
