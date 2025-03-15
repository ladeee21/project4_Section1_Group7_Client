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
            System.out.println("Please fill in all fields");
        } else {
            System.out.println("Login Successful!");
            navigateTo("home-view.fxml");
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/group7fileflix/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Home - FileFlix");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load " + fxmlFile);
        }
    }
}
