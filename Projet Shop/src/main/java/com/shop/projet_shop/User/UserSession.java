package com.shop.projet_shop.User;

public class UserSession {
    private static int id;
    private static String currentUser;
    private static String role;
    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
    public static void setRole(String currentrole) {role = currentrole;}
    public static String getRole() { return role;}
    public static int getId() {return id;}
    public static void setId(int identifier) {id = identifier;}
}

