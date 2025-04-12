package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class SettingsController {
    @FXML private Button btnLogout, btnbackward;

    @FXML
    public void initialize() {
        if (btnLogout != null) {
            btnLogout.setOnAction(event -> logout());
        } else {
            System.out.println("btnLogout is NULL!");
        }

        if (btnbackward != null) {
            btnbackward.setOnAction(event -> navigateTo("home-view.fxml"));
        } else {
            System.out.println("backward is Null!");
        }
    }

    private void logout() {
        try {
            String username = UserSession.getUsername();
            ClientConnection connection = ClientConnection.getInstance();

            // Send logout command
            connection.getOutput().writeUTF("LOGOUT");
            connection.getOutput().writeUTF(username);
            connection.getOutput().flush();

            // Wait for acknowledgment with timeout
            try {
                String response = connection.getInput().readUTF();
                if ("LOGOUT_SUCCESS".equals(response)) {
                    System.out.println("User logged out: " + username);
                    Logging.log("User logged out: " + username);
                } else {
                    System.out.println("Unexpected logout response: " + response);
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Logout acknowledgment timeout");
            }

            // Clear session and close connection
            UserSession.clearSession();
            connection.closeConnection();

        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            Logging.log("Logout error: " + e.getMessage());
        } finally {
            switchScene("hello-view.fxml");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnbackward.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Navigation failed", "Failed to load " + fxmlFile);
        }
    }

    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root, 320, 420));
            stage.setTitle("Welcome!");
        } catch (IOException e) {
            showErrorAlert("Navigation failed", "Failed to load " + fxmlFile);
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}