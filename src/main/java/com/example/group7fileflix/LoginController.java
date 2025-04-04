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
import java.net.Socket;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Logging.log("Login failed - missing fields.");
            showAlert(Alert.AlertType.WARNING,"Login Failed", "Please enter all fields");
            return;
        }

        try (Socket socket = new Socket("localhost", 55000);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            Logging.log("Attempting login for user: " + username);
            output.writeUTF("LOGIN");
            output.writeUTF(username);
            output.writeUTF(password);

            String response = input.readUTF();
            if (response.equals("AUTH_SUCCESS")) {
                // Set the username in UserSession
                UserSession.setUsername(username);
                Logging.log("Login successful for user: " + username);
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back!");
                navigateTo("home-view.fxml");
            } else {
                Logging.log("Login failed - invalid credentials for user: " + username);
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials.");
            }
        } catch (IOException e) {
            Logging.log("Login failed - communication error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to server.");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
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
}
