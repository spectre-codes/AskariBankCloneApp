package com.askaribank;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.Optional;

public final class TransactionPinDialog {

    private TransactionPinDialog() {
    }

    public static boolean confirm(Customer customer, String actionName) {
        if (customer == null) {
            showError("No customer is logged in.");
            return false;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Transaction PIN");
        dialog.setHeaderText("Confirm " + actionName);

        ButtonType confirmButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("4-digit PIN");
        pinField.setEditable(false);
        pinField.setMaxWidth(180);

        GridPane pinPad = new GridPane();
        pinPad.setHgap(8);
        pinPad.setVgap(8);
        pinPad.setAlignment(Pos.CENTER);

        addDigitButton(pinPad, pinField, "1", 0, 0);
        addDigitButton(pinPad, pinField, "2", 1, 0);
        addDigitButton(pinPad, pinField, "3", 2, 0);
        addDigitButton(pinPad, pinField, "4", 0, 1);
        addDigitButton(pinPad, pinField, "5", 1, 1);
        addDigitButton(pinPad, pinField, "6", 2, 1);
        addDigitButton(pinPad, pinField, "7", 0, 2);
        addDigitButton(pinPad, pinField, "8", 1, 2);
        addDigitButton(pinPad, pinField, "9", 2, 2);

        Button clearButton = keypadButton("Clear");
        clearButton.setOnAction(event -> pinField.clear());
        pinPad.add(clearButton, 0, 3);
        addDigitButton(pinPad, pinField, "0", 1, 3);

        VBox content = new VBox(12);
        content.setPadding(new Insets(8));
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(new Label("Enter your 4-digit transaction PIN."), pinField, pinPad);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(buttonType -> buttonType == confirmButton ? pinField.getText().trim() : null);

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return false;
        }

        String enteredPin = result.get();
        if (enteredPin.equals(customer.getTransactionPin())) {
            return true;
        }

        showError("Transaction PIN is incorrect.");
        return false;
    }

    private static void showError(String message) {
        AlertHelper.showError("PIN Verification", message);
    }

    private static void addDigitButton(GridPane pinPad, PasswordField pinField, String digit, int column, int row) {
        Button digitButton = keypadButton(digit);
        digitButton.setOnAction(event -> {
            if (pinField.getText().length() < 4) {
                pinField.setText(pinField.getText() + digit);
            }
        });
        pinPad.add(digitButton, column, row);
    }

    private static Button keypadButton(String text) {
        Button button = UiFactory.button(text);
        button.setMinSize(64, 44);
        button.setPrefSize(64, 44);
        return button;
    }
}
