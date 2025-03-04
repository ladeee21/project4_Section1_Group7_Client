package com.example.group7fileflix;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnLogin;

    @FXML
    public void initialize() {
        btnRegister.setOnAction(event -> loadPage("register-view.fxml"));
        btnLogin.setOnAction(event -> loadPage("login-view.fxml"));
    }

    private void loadPage(String fxmlFile) {
    try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage)btnRegister.getScene().getWindow();
        stage.setScene(new Scene(root, 400,300));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    }



}