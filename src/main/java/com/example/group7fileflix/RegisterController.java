package com.example.group7fileflix;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegister;

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

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Please fill all fields");
        } else if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match");
        } else {
            System.out.println("User Registered Successfully!");
            navigateTo("home-view.fxml");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Home - FileFlix");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }
}
