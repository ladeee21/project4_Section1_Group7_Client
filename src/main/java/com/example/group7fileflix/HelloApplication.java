package com.example.group7fileflix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // Establish connection before opening GUI
        try {
            ClientConnection.getInstance();
            System.out.println("Connected to server!");
        } catch (IOException e) {
            System.out.println("Failed to connect to server.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 420);
        stage.setTitle("Welcome!"); //Name of the page
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}