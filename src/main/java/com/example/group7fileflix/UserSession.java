package com.example.group7fileflix;

public class UserSession {
    private static String username;
    private static ClientConnection connection;

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void setConnection(ClientConnection connection) {
        UserSession.connection = connection;
    }

    public static ClientConnection getConnection() {
        return connection;
    }

    public static void clearSession() {
        username = null;
        connection = null;
    }
}
