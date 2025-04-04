package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;

import javax.swing.text.html.ImageView;
import java.io.IOException;

public class HomeController {

    @FXML private Button btnSend;
    @FXML private Button btnReceive;
    @FXML private Button btnFiles;
    @FXML private Button btnSettings;


    @FXML
    public void initialize() {
        if (btnSend != null) {
            btnSend.setOnAction(event -> switchScene("home2-view.fxml"));
        } else {
            System.out.println("btnSend is NULL! Check FXML file.");
        }

        if (btnReceive != null) {
            btnReceive.setOnAction(event -> switchScene("fileRetreival-view.fxml"));
        } else {
            System.out.println("btnReceive is NULL!");
        }

        if (btnFiles != null) {
            btnFiles.setOnAction(event -> switchScene("file-view.fxml"));
        } else {
            System.out.println("btnFiles is NULL!");
        }

        if (btnSettings != null) {
            btnSettings.setOnAction(event -> navigateTo("settings-view.fxml"));
        } else {
            System.out.println("btnLogout is NULL!");
        }


    }

    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnSend.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnSettings.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
            Logging.log("Navigated to: " + fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            Logging.log("Failed to navigate to " + fxmlFile + ". " + e.getMessage());
            System.out.println("Failed to load " + fxmlFile);
        }
    }
}
