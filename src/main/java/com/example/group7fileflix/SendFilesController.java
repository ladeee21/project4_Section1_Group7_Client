package com.example.group7fileflix;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SendFilesController {
    @FXML
    private Button pdfButton, docButton, pptButton, sendButton;

    private File selectedFile;

    @FXML
    public void initialize() {
        pdfButton.setOnAction(e -> selectFile("PDF Files", "*.pdf"));
        docButton.setOnAction(e -> selectFile("Word Documents", "*.docx", "*.doc"));
        pptButton.setOnAction(e -> selectFile("PowerPoint Presentations", "*.ppt", "*.pptx"));
        sendButton.setOnAction(e -> uploadFile());
    }

    private void selectFile(String fileType, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType, extensions));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            long fileSize = selectedFile.length();
            if (fileSize < 1048576) { // 1MB = 1048576 bytes
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

        try {
            File destination = new File("uploads/" + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "File uploaded successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading file: " + e.getMessage());
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

