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

    @FXML
    public void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // Validate input fields
        if (!validateInputs(username, password, confirmPassword)) {
            return;
        }

        try {
            // Get the client connection
            ClientConnection connection = ClientConnection.getInstance();
            DataOutputStream output = connection.getOutput();
            DataInputStream input = connection.getInput();

            // Send registration request
            output.writeUTF("REGISTER");
            output.writeUTF(username);
            output.writeUTF(password);
            output.flush();

            // Handle server response
            handleRegistrationResponse(input, username);

        } catch (IOException e) {
            handleRegistrationError(e, username);
        }
    }

    private boolean validateInputs(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed",
                    "Please enter all required fields.");
            return false;
        }

        if (username.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed",
                    "Username must be at least 4 characters long.");
            txtUsername.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed",
                    "Password must be at least 6 characters long.");
            txtPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "Passwords do not match.");
            clearFields();
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void handleRegistrationResponse(DataInputStream input, String username) throws IOException {
        String response = input.readUTF();

        switch (response) {
            case "REGISTER_SUCCESS":
                UserSession.setUsername(username);
                Logging.log("Registration successful: " + username);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Registration successful! You can now login.");
                navigateTo("home-view.fxml");
                break;

            case "USERNAME_TAKEN":
                Logging.log("Registration failed - username taken: " + username);
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Username already taken. Please choose a different one.");
                txtUsername.requestFocus();
                break;

            case "REGISTER_FAILED":
                Logging.log("Registration failed - server error: " + username);
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Server error during registration. Please try again.");
                clearFields();
                break;

            default:
                Logging.log("Unknown registration response: " + response);
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Unexpected server response. Please try again.");
                clearFields();
        }
    }

    private void handleRegistrationError(IOException e, String username) {
        Logging.log("Registration failed for " + username + ": " + e.getMessage());

        try {
            // Attempt to reconnect
            ClientConnection.getInstance().reconnect();
            showAlert(Alert.AlertType.ERROR, "Connection Error",
                    "Temporary connection issue. Please try again.");
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Connection Error",
                    "Could not connect to server. Please check your connection.");
            clearFields();
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 420));
            stage.setTitle("Home - FileFlix");
            Logging.log("Navigated to " + fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            Logging.log("Navigation failed - unable to load " + fxmlFile);
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

    // New method to clear all input fields
    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();

        txtUsername.requestFocus();
    }
}





