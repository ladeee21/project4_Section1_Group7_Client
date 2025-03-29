package com.example.group7fileflix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Change if needed
    private static final int SERVER_PORT = 55000;

    private static ClientConnection instance;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private ClientConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public static ClientConnection getInstance() throws IOException {
        if (instance == null) {
            instance = new ClientConnection();
        }
        return instance;
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void closeConnection() throws IOException {
        if (socket != null) {
            socket.close();
        }
        instance = null;
    }
}
