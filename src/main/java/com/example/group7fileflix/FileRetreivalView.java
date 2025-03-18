package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.sql.*;

public class FileRetreivalView {

    @FXML
    private TextField strFilename;

    @FXML
    private Button btnRetrieve;

    // Database connection details (Replace with database details)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    @FXML
    private void initialize() {
        if (btnRetrieve == null) {
            System.out.println("btnRetrieve is not initialized!");
        } else {
            System.out.println("btnRetrieve is initialized correctly!");
        }

        btnRetrieve.setOnAction(event -> retrieveFile());
    }


    @FXML
    public void retrieveFile() {
        String filename = strFilename.getText().trim();


        if (filename.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Filename cannot be empty.");
            return;
        }

        String fileContent = getFileContent(filename);

        if (fileContent != null) {
            showFileContent(filename, fileContent);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "File not found.");
        }
    }

    private String getFileContent(String filename) {
        String query = "SELECT file_data FROM files WHERE filename = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, filename);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("file_data"); // Assuming file content is stored as TEXT for noe, change it according to file type
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred: " + e.getMessage());
        }
        return null;
    }

    private void showFileContent(String filename, String content) {
        Stage stage = new Stage();
        stage.setTitle("File: " + filename);

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        VBox vbox = new VBox(textArea);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

