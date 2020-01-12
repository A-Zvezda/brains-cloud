package com.geekbrains.brains.cloud.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/main.fxml"));
        primaryStage.setTitle("Brains Cloud Client");
        controller = loader.getController();
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            controller.Dispose();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}