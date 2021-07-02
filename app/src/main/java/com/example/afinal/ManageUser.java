package com.example.afinal;

public class ManageUser {
    public static User user;
    public static boolean isLoggedIn = false;
    public static void setUser(String name, String email){
        user = new User(name, email);
        isLoggedIn = true;
    }

    public static void clear() {
        user = null;
        isLoggedIn = false;
    }
}
