package com.example.group7fileflix;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SendFilesController {
    @FXML
    private Button pdfButton, docButton, pptButton, sendButton, btnbackward;

    private File selectedFile;

    @FXML
    public void initialize() {
        pdfButton.setOnAction(e -> selectFile("PDF Files", "*.pdf"));
        docButton.setOnAction(e -> selectFile("Word Documents", "*.docx", "*.doc"));
        pptButton.setOnAction(e -> selectFile("PowerPoint Presentations", "*.ppt", "*.pptx"));
        sendButton.setOnAction(e -> uploadFile());
        btnbackward.setOnAction(event->navigateTo("home2-view.fxml"));
    }

    private void selectFile(String fileType, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType, extensions));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            long fileSize = selectedFile.length();
            if (fileSize < 808400) { // 1MB = 1048576 bytes
                showAlert(Alert.AlertType.ERROR, "File Too Small", "Please select a file larger than 1MB.");
                selectedFile = null; // Reset selection
            } else {
                showAlert(Alert.AlertType.INFORMATION, "File Selected", "Selected File: " + selectedFile.getName());
            }
        }
    }

    private void uploadFile() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select a file before sending.");
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
            showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "Photo uploaded successfully!");
            navigateBackToHome();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading file: " + e.getMessage());
        }

    }

    private void navigateBackToHome() {
        // Add a short delay to show the success message
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("home2-view.fxml"));
        pause.play();
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

