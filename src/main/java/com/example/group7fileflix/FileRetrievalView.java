package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.*;
import java.io.*;
import java.net.Socket;

public class FileRetrievalView {

    @FXML
    private TextField strFilename;

    @FXML
    private Button btnRetrieve, btnbackward;


    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 55000;

    @FXML
    private void initialize() {
        if (btnRetrieve == null) {
            System.out.println("btnRetrieve is not initialized!");
        } else {
            System.out.println("btnRetrieve is initialized correctly!");
        }


        btnRetrieve.setOnAction(event -> retrieveFile());
        if(btnbackward!=null) {
            btnbackward.setOnAction(event -> navigateTo("home-view.fxml"));
        }else{
            System.out.println("backward is Null!");
        }
    }

    @FXML
    private void retrieveFile() {
        String filename = strFilename.getText().trim();

        if (filename.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Filename cannot be empty.");
            return;
        }

        // Request the file from the server
        byte[] fileContent = requestFileFromServer(filename);

        if (fileContent != null) {
            // Show file content in a new window
            showFileContent(filename, fileContent);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found.");
        }
    }

    private byte[] requestFileFromServer(String filename) {
        String username = UserSession.getUsername(); // Get the logged-in user's username

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            socket.setSoTimeout(5000); // Set a timeout of 5 seconds to avoid infinite blocking

            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dos = new DataOutputStream(outputStream);
                 DataInputStream dis = new DataInputStream(inputStream)) {

                // Send the request for the file with the username
                dos.writeUTF("RETRIEVE");
                dos.writeUTF(username);
                dos.writeUTF(filename);
                dos.flush();  // Ensure data is sent

                // Receive server response
                boolean accessGranted = dis.readBoolean(); // Check if access is granted

                if (!accessGranted) {
                    String message = dis.readUTF();  // Read the error message
                    showAlert(Alert.AlertType.ERROR, "Access Denied", message);  // Show the message
                    return null;
                }

                long fileSize = dis.readLong(); // Get the file size
                if (fileSize <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "File not found or empty.");
                    return null;
                }

                // Read file content safely
                byte[] fileContent = new byte[(int) fileSize];
                int bytesRead = 0;
                int offset = 0;

                while (offset < fileSize) {
                    bytesRead = dis.read(fileContent, offset, (int) fileSize - offset);
                    if (bytesRead == -1) {
                        break; // Prevent infinite loops
                    }
                    offset += bytesRead;
                }

                if (offset < fileSize) {
                    showAlert(Alert.AlertType.ERROR, "Error", "File transfer incomplete.");
                    return null;
                }

                return fileContent;

            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve file: " + e.getMessage());
        }
        return null;
    }


    private void showFileContent(String filename, byte[] fileContent) {
        String username = UserSession.getUsername(); // Get the currently logged-in user

        // Step 1: Save the file under client_retrieves/<username>/
        File userDir = new File("client_retrieves", username);
        if (!userDir.exists()) {
            userDir.mkdirs(); // Create user-specific directory if not exists
        }

        File savedFile = new File(userDir, filename);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(fileContent);
            System.out.println("File saved to: " + savedFile.getAbsolutePath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save the file: " + e.getMessage());
            return;
        }

        // Step 2: Preview based on file type
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
            // Step 3: Preview for other file types using system viewer
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText("File retrieved and saved successfully.");
            alert.setContentText("The file will now open with the default app.");
            alert.showAndWait();

            try {
                Desktop.getDesktop().open(savedFile); // Let OS handle preview
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Open Error", "Could not open file: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}
