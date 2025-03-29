package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegister;

    // Reference to the client socket (established on HelloApplication)
    private ClientConnection client;

    // Initialize the RegisterController with the client connection
    public void setClient(ClientConnection client) {
        this.client = client;
    }

    @FXML
    public void initialize() {
        if (btnRegister == null) {
            System.out.println("Error: btnRegister is NULL! Check FXML file.");
        } else {
            System.out.println("btnRegister successfully loaded.");
        }
    }

    @FXML
    public void handleRegister() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed", "Please enter all the fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Failed", "Passwords do not match.");
            return;
        }

        // Send registration request to the server
        try {
            // Construct the registration message

            ClientConnection.getInstance().getOutput().writeUTF("REGISTER");
            ClientConnection.getInstance().getOutput().writeUTF(username);
            ClientConnection.getInstance().getOutput().writeUTF(password);

            // Send message to the server via ClientConnection

            // Wait for response from the server
            String response = ClientConnection.getInstance().getInput().readUTF();
        if (response.equals("REGISTER_SUCCESS")) {
                UserSession.setUsername(username);
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Click ok to Continue.");
                navigateTo("home-view.fxml"); // Navigate to home screen
            } else if (response.equals("USERNAME_TAKEN")) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            }else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please try again.");
        }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed Communicating with the server.");
            e.printStackTrace();
        }


    }


    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 420));
            stage.setTitle("Home - FileFlix");
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
