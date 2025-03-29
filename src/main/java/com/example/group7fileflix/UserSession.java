package com.example.group7fileflix;

public class UserSession {
    // Static variable to hold the username
    private static String username;

    // Getter method to retrieve the username
    public static String getUsername() {
        return username;
    }

    // Setter method to set the username
    public static void setUsername(String username) {
        UserSession.username = username;
    }

    // Clear the session (e.g., for logging out)
    public static void clear() {
        username = null;
    }
}
