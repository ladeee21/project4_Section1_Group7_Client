package com.example.group7fileflix;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Home2Controller  {
    @FXML private Button btnPhotos;
    @FXML
    private Button Fileset;

    @FXML
    public void initialize() {
        if (Fileset != null) {
            Fileset.setOnAction(event -> switchScene("sendfiles-view.fxml"));
        } else {
            System.out.println("btnSend is NULL! Check FXML file.");
        }
    }

    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) Fileset.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }
}
