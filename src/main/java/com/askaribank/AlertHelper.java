package com.askaribank;

import javafx.scene.control.Alert;

final class AlertHelper {

    private AlertHelper() {
    }

    static void showError(String title, String message) {
        show(Alert.AlertType.ERROR, title, null, message);
    }

    static void showInfo(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, null, message);
    }

    static void showInfo(String title, String header, String message) {
        show(Alert.AlertType.INFORMATION, title, header, message);
    }

    static void show(Alert.AlertType alertType, String title, String message) {
        show(alertType, title, null, message);
    }

    static void show(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
