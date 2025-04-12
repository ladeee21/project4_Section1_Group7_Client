package com.example.group7fileflix;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientConnection {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 55000;
    private static final long HEARTBEAT_INTERVAL = 30000;
    private static final int SOCKET_TIMEOUT = 5000;

    private static ClientConnection instance;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Timer heartbeatTimer;

    private ClientConnection() throws IOException {
        connect(); // Separate connection logic
        startHeartbeat();
    }

    private void connect() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public static synchronized ClientConnection getInstance() throws IOException {
        if (instance == null || !instance.isConnected()) {
            instance = new ClientConnection();
        }
        return instance;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public synchronized void reconnect() throws IOException {
        closeConnection();
        connect();
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public Socket getSocket() {
        return socket;
    }

    private void startHeartbeat() {
        heartbeatTimer = new Timer(true);
        heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (socket != null && !socket.isClosed()) {
                        output.writeUTF("HEARTBEAT");
                        output.flush();
                    }
                } catch (IOException e) {
                    System.err.println("Heartbeat failed: " + e.getMessage());
                    cancel();
                }
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL);
    }

    public synchronized void closeConnection() throws IOException {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        instance = null;
    }
}