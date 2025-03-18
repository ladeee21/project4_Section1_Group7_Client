package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PhotoSelectionController {

    @FXML private Button btnPhoto, btnSendFiles, btnBack;
    @FXML private ImageView imageView;

    private File selectedFile;
    @FXML private Button btnbackward;

    @FXML
    public void initialize() {
        btnPhoto.setOnAction(event -> selectPhoto());
        btnSendFiles.setOnAction(event -> uploadFile());

        if(btnbackward!=null) {
            btnbackward.setOnAction(event -> navigateTo("home2-view.fxml"));
        }else{
            System.out.println("backward is Null!");
        }

    }

    private void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) btnPhoto.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            long fileSize = file.length();
            if (fileSize < 1048576) {
                showAlert(Alert.AlertType.ERROR, "File Too Small", "Please select a file larger than 1MB.");
                return;
            }

            selectedFile = file;
            imageView.setImage(new Image(file.toURI().toString()));
            System.out.println("Selected Image: " + file.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "File Selected", "Selected File: " + selectedFile.getName());
        }
    }

    private void uploadFile() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select a photo before sending.");
            return;
        }

        try {
            File destination = new File("uploads/" + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "Photo uploaded successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading file: " + e.getMessage());
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/home2-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 420));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load home2-view.fxml");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnbackward.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
