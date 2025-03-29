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
import javafx.scene.layout.VBox;


import java.io.*;
import java.net.Socket;
import java.sql.*;

public class FileRetreivalView {

    @FXML
    private TextField strFilename;

    @FXML
    private Button btnRetrieve, btnbackward;


    private static final String SERVER_ADDRESS = "localhost";  // The server address
    private static final int SERVER_PORT = 55000;  // The port the server is listening on

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
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            // Send the request for the file
            DataOutputStream dos = new DataOutputStream(outputStream);
            dos.writeUTF("RETRIEVE");
            dos.writeUTF(filename);  // Send the filename to the server

            // Receive the file content from the server
            DataInputStream dis = new DataInputStream(inputStream);
            long fileSize = dis.readLong();  // Get the file size from the server

            if (fileSize > 0) {
                byte[] fileContent = new byte[(int) fileSize];
                dis.readFully(fileContent);
                return fileContent;
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve file: " + e.getMessage());
        }
        return null;
    }

    private void showFileContent(String filename, byte[] fileContent) {
        // Show the file content in a new window based on file type
        Stage stage = new Stage();
        stage.setTitle("File: " + filename);

        if (filename.endsWith(".txt")) {
            // Show text files in a TextArea
            TextArea textArea = new TextArea(new String(fileContent));
            textArea.setWrapText(true);
            textArea.setEditable(false);

            VBox vbox = new VBox(textArea);
            vbox.setPadding(new Insets(10));

            Scene scene = new Scene(vbox, 500, 400);
            stage.setScene(scene);
            stage.show();
        } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            // Show image files in an ImageView
            Image image = new Image(new ByteArrayInputStream(fileContent));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(500);
            imageView.setPreserveRatio(true);

            VBox vbox = new VBox(imageView);
            vbox.setPadding(new Insets(10));

            Scene scene = new Scene(vbox, 500, 500);
            stage.setScene(scene);
            stage.show();
        } else {
            showAlert(Alert.AlertType.ERROR, "Unsupported File Type", "File type is not supported for preview.");
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

    private void navigateToHome() {


    }
}
