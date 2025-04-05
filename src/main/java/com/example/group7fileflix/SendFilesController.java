package com.example.group7fileflix;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.geometry.Insets;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class SendFilesController {
    @FXML private Button pdfButton, docButton, pptButton, sendButton, btnbackward;

    private File selectedFile;

    @FXML
    public void initialize() {
        pdfButton.setOnAction(e -> selectFile("PDF Files", "*.pdf"));
        docButton.setOnAction(e -> selectFile("Word Documents", "*.docx", "*.doc"));
        pptButton.setOnAction(e -> selectFile("PowerPoint Presentations", "*.ppt", "*.pptx"));
        sendButton.setOnAction(e -> uploadFile());
        btnbackward.setOnAction(e -> navigateTo("home2-view.fxml"));
    }

    private void selectFile(String fileType, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType, extensions));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            long fileSize = selectedFile.length();
            if (fileSize < 1048576) {
                showAlert(Alert.AlertType.ERROR, "File Too Small", "Please select a file larger than 1MB.");
                selectedFile = null;
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
    }

    private void sendFileWithMetadata(String fileName) {
        String username = UserSession.getUsername();

        try (Socket socket = new Socket("localhost", 55000);
             OutputStream os = socket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(os);
             FileInputStream fis = new FileInputStream(selectedFile)) {

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
        }
    }


    private void navigateBackToHome() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> navigateTo("home2-view.fxml"));
        pause.play();
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnbackward.getScene().getWindow();
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
