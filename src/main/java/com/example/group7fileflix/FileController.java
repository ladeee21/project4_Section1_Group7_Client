package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.*;
import java.nio.file.*;
import java.awt.Desktop;

public class FileController {

    @FXML private ListView<String> fileListView;
    @FXML private Button btnOpen;
    @FXML private Button btnDelete, btnbackward;


    private final String username = UserSession.getUsername();
    private final Path userDirectory = Paths.get("client_retrieves", username);

    @FXML
    public void initialize() {
        loadFiles();

        btnOpen.setOnAction(event -> openSelectedFile());
        btnDelete.setOnAction(event -> deleteSelectedFile());

        if(btnbackward!=null) {
            btnbackward.setOnAction(event -> navigateTo("home2-view.fxml"));
        }else{
            System.out.println("backward is Null!");
        }
    }

    private void loadFiles() {
        fileListView.getItems().clear();
        try {
            if (Files.exists(userDirectory)) {
                Files.list(userDirectory)
                        .filter(Files::isRegularFile)
                        .forEach(path -> fileListView.getItems().add(path.getFileName().toString()));
                Logging.log("Files loaded for user: " + username);
            }
        } catch (IOException e) {
            Logging.log("Error loading files for user: " + username + ". " + e.getMessage());  // Log error
            showError("Error loading files.");
        }
    }

    // Open the selected file and preview its content
    private void openSelectedFile() {
        String filename = fileListView.getSelectionModel().getSelectedItem();
        if (filename == null) return;

        Path filePath = userDirectory.resolve(filename);
        try {
            byte[] fileContent = Files.readAllBytes(filePath); // Read the content from the file
            Logging.log("File opened: " + filename);
            showFileContent(filename, filePath, fileContent); // Pass filePath and content to the method
        } catch (IOException e) {
            Logging.log("Error reading file: " + filename + ". " + e.getMessage());
            showError("Error reading the file.");
        }
    }



    // Save the file and preview based on file type
    private void showFileContent(String filename, Path filePath, byte[] fileContent) {
        // Preview based on file type
        Stage stage = new Stage();
        stage.setTitle("File: " + filename);

        if (filename.endsWith(".txt")) {
            TextArea textArea = new TextArea(new String(fileContent));
            textArea.setWrapText(true);
            textArea.setEditable(false);

            VBox vbox = new VBox(textArea);
            vbox.setPadding(new Insets(10));

            Scene scene = new Scene(vbox, 500, 400);
            stage.setScene(scene);
            stage.show();

        } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            Image image = new Image(new ByteArrayInputStream(fileContent));
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            double maxWidth = 800;
            double maxHeight = 600;
            double scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());

            imageView.setFitWidth(image.getWidth() * scale);
            imageView.setFitHeight(image.getHeight() * scale);

            VBox vbox = new VBox(imageView);
            vbox.setPadding(new Insets(10));

            Scene scene = new Scene(vbox);
            stage.setScene(scene);
            stage.sizeToScene(); // Resize window to fit content
            stage.show();

        } else {
            // Open other file types with system's default application
            Logging.log("Opening file with system default app: " + filename);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Retrieved");
            alert.setHeaderText("File retrieved successfully.");
            alert.setContentText("The file will now open with the default app.");
            alert.showAndWait();

            try {
                Desktop.getDesktop().open(new File(filePath.toString())); // Open file with system default app
            } catch (IOException e) {
                Logging.log("Error opening file with system default app: " + filename + ". " + e.getMessage());
                showError("Could not open file: " + e.getMessage());
            }
        }
    }


    // Delete the selected file
    private void deleteSelectedFile() {
        String filename = fileListView.getSelectionModel().getSelectedItem();
        if (filename == null) return;

        Path filePath = userDirectory.resolve(filename);
        try {
            Files.delete(filePath);
            loadFiles(); // Refresh file list
            Logging.log("File deleted: " + filename);
            showInfo("File deleted successfully.");
        } catch (IOException e) {
            Logging.log("Error deleting file: " + filename + ". " + e.getMessage());
            showError("Error deleting file.");
        }
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
            Logging.log("Failed to navigate to " + fxmlFile + ". " + e.getMessage());
            System.out.println("Failed to load " + fxmlFile);
        }
    }


    // Show an error message
    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    // Show an information message
    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}
