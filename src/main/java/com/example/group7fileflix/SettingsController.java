package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Button btnLogout;

    @FXML
    public void initialize() {
        btnLogout.setOnAction(event -> logout());
    }

    private void logout() {
        System.out.println("User Logged Out!");
        switchScene("hello-view.fxml"); // Navigate to Welcome Screen
    }

    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root, 320, 240));
            stage.setTitle("Welcome!"); // Back to the first screen
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }
}
