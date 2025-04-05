package com.example.group7fileflix;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        // Load the Landing Page first
        FXMLLoader landingLoader = new FXMLLoader(getClass().getResource("landing-view.fxml"));
        Parent landingRoot = landingLoader.load();
        Scene landingScene = new Scene(landingRoot, 450, 400);
        stage.setTitle("FileFlix - Welcome");
        stage.setScene(landingScene);
        stage.show();

        PauseTransition wait = new PauseTransition(Duration.seconds(5));
        wait.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), landingRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(fadeEvent -> {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                    Scene loginScene = new Scene(loginLoader.load(), 320, 420);
                    stage.setScene(loginScene);
                    stage.setTitle("Welcome!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fadeOut.play();
        });
        wait.play();
    }

    public static void main(String[] args) {
        launch();
    }
}

