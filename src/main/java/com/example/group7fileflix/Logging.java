package com.example.group7fileflix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {
    private static final String LOG_DIR = "logs/";
    private static final String LOG_FILE = LOG_DIR + "packet_log.txt";

    // Ensures the logs directory exists before writing
    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            System.err.println("Error creating log directory: " + e.getMessage());
        }
    }

    // Logs packet details when a file is sent
    public static void logPacket(String username, String filename, int packetSize) {
        logToFile("SENT", username, filename, packetSize);
    }

    // Logs packet details when a file is received
    public static void logReceivedFile(String username, String filename, int packetSize) {
        logToFile("RECEIVED", username, filename, packetSize);
    }

    // Generic logging function to avoid code duplication
    private static void logToFile(String action, String username, String filename, int packetSize) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("%s | %s | User: %s | File: %s | Size: %d bytes%n",
                timestamp, action, username, filename, packetSize);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }
    //Use this code in the upload file logic:
    //PacketLogger.logPacket(username, filename, packetSize);

}