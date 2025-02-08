package com.shop.projet_shop.services;


import com.shop.projet_shop.DataBase.Product;
import com.shop.projet_shop.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishListService {

    public List<Product> fetchWishListFromDatabase(int userId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.description, p.price, p.stock_quantity, p.image_path " +
                "FROM wishList w " +
                "JOIN products p ON w.product_id = p.id " +
                "WHERE w.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");
                String imagePath = rs.getString("image_path");

                Product product = new Product(id, name, description, price, stock, imagePath);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void deleteFromWishList(int productId, int userId) {
        String query = "DELETE FROM wishlist WHERE product_id = ? AND user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}