package com.askaribank;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BankDataStore.initialize();

        Parent root = BankScreens.login();
        Scene scene = new Scene(root, 1100, 720);

        stage.setTitle(BankDataStore.appName);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
