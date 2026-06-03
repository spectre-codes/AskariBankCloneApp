package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

final class BankScreens {

    private BankScreens() {
    }

    static Parent login() {
        return new LoginController().createView();
    }

    static Parent staffDashboard() {
        return new DashboardController().createView();
    }

    static Parent customerDashboard() {
        return new CustomerDashboardController().createView();
    }

    static void replaceScene(Stage stage, Parent root) {
        double width = 1100;
        double height = 720;
        if (stage.getScene() != null) {
            width = stage.getScene().getWidth();
            height = stage.getScene().getHeight();
        }

        stage.setScene(new Scene(root, width, height));
        stage.centerOnScreen();
    }
}
