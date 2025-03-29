package com.example.group7fileflix;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
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
            if (fileSize < 336114) {
                showAlert(Alert.AlertType.ERROR, "File Too Small", "Please select a file larger than 1MB.", true);
                return;
            }

            selectedFile = file;
            imageView.setImage(new Image(file.toURI().toString()));
            System.out.println("Selected Image: " + file.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "File Selected", "Selected File: " + selectedFile.getName(), true);
        }
    }

    private void uploadFile() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.ERROR, "No File Selected", "Please select a photo before sending.", true);
            return;
        }

        String username = UserSession.getUsername();

        try (Socket socket = new Socket("localhost", 55000);  // Connect to the server
             OutputStream os = socket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(os);
             FileInputStream fis = new FileInputStream(selectedFile)) {

            // Send upload command to server
            dos.writeUTF("UPLOAD");
            dos.writeUTF(username);  // Send the username
            dos.writeUTF(selectedFile.getName());  // Send the filename
            dos.writeLong(selectedFile.length());  // Send the file size

            // Send the file content
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            dos.flush();
            showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "Photo uploaded successfully!", false);
            navigateBackToHome();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading file: " + e.getMessage(), true);
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

    private void navigateBackToHome() {
        // Add a short delay to show the success message
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("home2-view.fxml"));
        pause.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message, boolean withOkButton) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
