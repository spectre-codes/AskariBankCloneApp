package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerSettingsController {

    private PasswordField currentPinField;
    private PasswordField newPinField;
    private PasswordField confirmPinField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Settings"),
                new Label("Change the transaction PIN used for transfers, bill payments, and top-ups.")
        );

        currentPinField = UiFactory.passwordField("Current PIN");
        newPinField = UiFactory.passwordField("New 4-digit PIN");
        confirmPinField = UiFactory.passwordField("Confirm new PIN");

        GridPane gridPane = UiFactory.grid(14, 12);
        gridPane.setMaxWidth(480);
        gridPane.add(new Label("Current Transaction PIN"), 0, 0);
        UiFactory.addGrowing(gridPane, currentPinField, 1, 0);
        gridPane.add(new Label("New Transaction PIN"), 0, 1);
        UiFactory.addGrowing(gridPane, newPinField, 1, 1);
        gridPane.add(new Label("Confirm New PIN"), 0, 2);
        UiFactory.addGrowing(gridPane, confirmPinField, 1, 2);

        Button changeButton = UiFactory.button("Change PIN");
        changeButton.setOnAction(event -> handleChangePin());
        gridPane.add(changeButton, 1, 3);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(gridPane, messageLabel);
        return page;
    }

    private void handleChangePin() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String currentPin = currentPinField.getText().trim();
        String newPin = newPinField.getText().trim();
        String confirmPin = confirmPinField.getText().trim();

        if (!currentPin.equals(customer.getTransactionPin())) {
            showError("Current transaction PIN is incorrect.");
            return;
        }

        if (!newPin.matches("\\d{4}")) {
            showError("New transaction PIN must be exactly 4 digits.");
            return;
        }

        if (!newPin.equals(confirmPin)) {
            showError("New PIN and confirmation do not match.");
            return;
        }

        customer.setTransactionPin(newPin);
        FileDataStore.saveAll();
        AuditLogger.log("TRANSACTION_PIN_CHANGED", customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Your transaction PIN was changed.");
        currentPinField.clear();
        newPinField.clear();
        confirmPinField.clear();
        messageLabel.setText("Transaction PIN changed successfully.");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Settings Error", message);
    }
}
