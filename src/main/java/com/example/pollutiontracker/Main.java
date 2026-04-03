package com.example.pollutiontracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Welcome.fxml")
        );

        Scene scene = new Scene(loader.load(), 400, 450);
        stage.setTitle("Smart Pollution Tracker");

        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
