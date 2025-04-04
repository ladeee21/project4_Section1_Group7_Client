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
    private Button btnLogout, btnbackward;


    @FXML
    public void initialize() {
        if (btnLogout != null) {
            btnLogout.setOnAction(event -> logout());
        } else {
            System.out.println("btnLogout is NULL! Check settings-view.fxml.");
        }

        if(btnbackward!=null) {
            btnbackward.setOnAction(event -> navigateTo("home-view.fxml"));
        }else{
            System.out.println("backward is Null!");
        }
    }

    private void logout() {
        System.out.println("User Logged Out!");
        Logging.log("User logged out.");
        switchScene("hello-view.fxml"); // Navigate to Welcome Screen
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnbackward.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
            Logging.log("Navigated to: " + fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
            Logging.log("Failed to load: " + fxmlFile + " | Error: " + e.getMessage());
        }
    }

    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root, 320, 420));
            stage.setTitle("Welcome!"); // Back to the first screen
            Logging.log("Switched scene to: " + fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
            Logging.log("Failed to switch scene to: " + fxmlFile + " | Error: " + e.getMessage());
        }
    }
}
