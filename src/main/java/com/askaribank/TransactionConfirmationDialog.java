package com.askaribank;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Optional;

final class TransactionConfirmationDialog {

    private TransactionConfirmationDialog() {
    }

    static boolean confirm(String transactionType, String fromAccount, String receiver,
                           double amount, String description) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Confirm Transaction");
        dialog.setHeaderText("Please review transaction details");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = UiFactory.grid(14, 10);
        grid.add(new Label("Type"), 0, 0);
        grid.add(new Label(transactionType), 1, 0);
        grid.add(new Label("From"), 0, 1);
        grid.add(new Label(mask(fromAccount)), 1, 1);
        grid.add(new Label("To / Service"), 0, 2);
        grid.add(new Label(receiver == null || receiver.isBlank() ? "-" : receiver), 1, 2);
        grid.add(new Label("Amount"), 0, 3);
        grid.add(new Label("PKR " + String.format("%.2f", amount)), 1, 3);
        grid.add(new Label("Description"), 0, 4);
        Label descriptionLabel = new Label(description == null || description.isBlank() ? "-" : description);
        descriptionLabel.setWrapText(true);
        grid.add(descriptionLabel, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(buttonType -> buttonType == confirmButton);
        Optional<Boolean> result = dialog.showAndWait();
        return result.orElse(false);
    }

    private static String mask(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "-";
        }
        return "XXXXXX" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
    }
}
