package com.example.group7fileflix;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

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
            Logging.log("Selected file: " + file.getAbsolutePath() + " (Size: " + fileSize + " bytes)");
            if (fileSize < 1048576) {
                Logging.log("File too small: " + file.getName());
                showAlert(Alert.AlertType.ERROR, "File Too Small", "Please select a file larger than 1MB.");
                return;
            }

            selectedFile = file;
            imageView.setImage(new Image(file.toURI().toString()));
            Logging.log("Image loaded and displayed: " + selectedFile.getName());
            System.out.println("Selected Image: " + file.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Image Selected", "Selected Image: " + selectedFile.getName());
        } else {
            Logging.log("File selection canceled.");
        }

    }
    private void uploadFile() {
        if (selectedFile == null) {
            Logging.log("Upload attempted with no photo selected.");
            showAlert(Alert.AlertType.WARNING, "No Photo Selected", "Please select a photo before sending.");
            return;
        }
        promptForFileName();
    }

    private void promptForFileName() {
        String extension = getFileExtension(selectedFile);
        String originalName = removeExtension(selectedFile.getName());

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Provide File Name");
        dialog.setHeaderText("Enter a file name (extension will be added automatically)");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameInput = new TextField(originalName);
        Label previewLabel = new Label("Final file name: " + originalName + extension);
        nameInput.textProperty().addListener((obs, oldVal, newVal) ->
                previewLabel.setText("Final file name: " + newVal.trim() + extension));

        grid.add(new Label("File Name:"), 0, 0);
        grid.add(nameInput, 1, 0);
        grid.add(previewLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameInput::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                return nameInput.getText().trim();

        TextInputDialog nameDialog = new TextInputDialog(selectedFile.getName());
        nameDialog.setTitle("Provide File Name");
        nameDialog.setHeaderText("Enter a file name for upload.");
        nameDialog.setContentText("File Name:");

        Optional<String> result = nameDialog.showAndWait();
        result.ifPresent(fileName -> {
            String trimmedFileName = fileName.trim();
            Logging.log("User entered file name: " + trimmedFileName);

            //Check for invalid characters in the filename
            if (trimmedFileName.matches(".*[\\\\/:*?\"<>|].*")) {
                showAlert(Alert.AlertType.ERROR, "Invalid File Name", "File name cannot contain: \\ / : * ? \" < > |");
                return; // Stop further processing
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nameOnly -> {
            if (nameOnly.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Name", "Name cannot be empty.");
            } else if (nameOnly.matches(".*[\\\\/:*?\"<>|].*")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Name", "File name cannot contain: \\ / : * ? \" < > |");
            } else {
                String finalName = nameOnly + extension;
                sendFileWithMetadata(finalName);
            }
        });
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf(".");
        return (lastDot != -1) ? name.substring(lastDot) : "";
    }

    private String removeExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return (lastDot != -1) ? filename.substring(0, lastDot) : filename;

    private String ensureUniqueFileName(String fileName) {
        File destination = new File("uploads/" + fileName);

        while (destination.exists()) {
            Logging.log("File name conflict detected: " + fileName);

            TextInputDialog nameDialog = new TextInputDialog(fileName);
            nameDialog.setTitle("Duplicate Photo Name");
            nameDialog.setHeaderText("A photo with this name already exists.");
            nameDialog.setContentText("Please enter a new name for your photo:");

            Optional<String> result = nameDialog.showAndWait();
            if (result.isPresent()) {
                fileName = result.get().trim();
                Logging.log("User entered new file name: " + fileName);
                destination = new File("uploads/" + fileName);
            } else {
                Logging.log("User cancelled upload due to duplicate name.");
                showAlert(Alert.AlertType.WARNING, "Operation Cancelled", "Photo upload cancelled.");
                return null;
            }
        }
        return fileName;
    }

    private void sendFileWithMetadata(String fileName) {
        String username = UserSession.getUsername();

        try (Socket socket = new Socket("localhost", 55000);
             OutputStream os = socket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(os);
             FileInputStream fis = new FileInputStream(selectedFile)) {

            Logging.log("Starting upload for user: " + username + ", file: " + fileName);

            // Send upload command to server
            dos.writeUTF("UPLOAD");
            dos.writeUTF(username);
            dos.writeUTF(fileName);
            dos.writeLong(selectedFile.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            dos.flush();

            String serverResponse = new DataInputStream(socket.getInputStream()).readUTF();

            switch (serverResponse) {
                case "UPLOAD_SUCCESS":
                    showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "Your file has been uploaded successfully!");
                    navigateBackToHome();
                    break;

                case "DUPLICATE_FILE":
                    Platform.runLater(() -> {
                        Alert duplicateAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        duplicateAlert.setTitle("Duplicate File");
                        duplicateAlert.setHeaderText("A file with this name already exists.");
                        duplicateAlert.setContentText("Rename the file and try again, or cancel the upload?");

                        ButtonType renameButton = new ButtonType("Rename");
                        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                        duplicateAlert.getButtonTypes().setAll(renameButton, cancelButton);

                        Optional<ButtonType> result = duplicateAlert.showAndWait();
                        if (result.isPresent() && result.get() == renameButton) {
                            promptForFileName();
                        } else {
                            showAlert(Alert.AlertType.INFORMATION, "Upload Cancelled", "File upload has been cancelled.");
                            navigateBackToHome();
                        }
                    });
                    break;

                default:
                    showAlert(Alert.AlertType.ERROR, "Upload Failed", "An unexpected error occurred. Server response: " + serverResponse);
                    break;
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading file: " + e.getMessage());
            Logging.log("Upload completed for file: " + fileName);
            showAlert(Alert.AlertType.INFORMATION, "Upload Successful", "Your photo has been uploaded successfully!");
            navigateBackToHome();

        } catch (IOException e) {
            Logging.log("Error uploading file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Error uploading photo: " + e.getMessage());
        }
    }


    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnbackward.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root, 400, 420));
            stage.show();
            Logging.log("Navigated to " + fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            Logging.log("Navigation failed - unable to load " + fxmlFile);
            System.out.println("Failed to load " + fxmlFile);
        }
    }

    private void navigateBackToHome() {
        // Add a short delay to show the success message
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("home2-view.fxml"));
        pause.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
