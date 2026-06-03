package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MTagPaymentController {

    private final TransactionService transactionService = new TransactionService();

    private TextField mtagIdField;
    private TextField amountField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("M-Tag Payment"),
                new Label("Recharge your M-Tag or vehicle ID from your account balance.")
        );

        mtagIdField = UiFactory.textField("M-Tag ID / Vehicle ID");
        amountField = UiFactory.textField("e.g. 1000");

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(520);
        grid.add(new Label("M-Tag ID"), 0, 0);
        UiFactory.addGrowing(grid, mtagIdField, 1, 0);
        grid.add(new Label("Amount"), 0, 1);
        UiFactory.addGrowing(grid, amountField, 1, 1);

        Button payButton = UiFactory.button("Pay M-Tag");
        payButton.setOnAction(event -> handlePayment());
        grid.add(payButton, 1, 2);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, messageLabel);
        return page;
    }

    private void handlePayment() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String mtagId = mtagIdField.getText().trim();
        if (mtagId.isEmpty()) {
            showError("Please enter M-Tag ID / Vehicle ID.");
            return;
        }

        double amount = readAmount();
        if (amount <= 0) {
            return;
        }

        if (!TransactionConfirmationDialog.confirm("M-Tag Payment", customer.getAccountNumber(), mtagId,
                amount, "M-Tag recharge")) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "M-Tag payment")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processPayment(customer, mtagId, amount));
    }

    private void processPayment(Customer customer, String mtagId, double amount) {
        try {
            Transaction transaction = transactionService.customerPayment(customer.getAccountNumber(), mtagId,
                    TransactionType.MTAG_PAYMENT, amount, "M-Tag recharge for ID: " + mtagId,
                    "MTAG_PAYMENT", "M-Tag payment of PKR " + String.format("%.2f", amount)
                            + " was successful.");
            File receipt = ReceiptHelper.saveReceipt("mtag", "M-Tag Payment Receipt", List.of(
                    "Transaction ID: " + transaction.getTransactionId(),
                    "M-Tag ID: " + mtagId,
                    "Amount: PKR " + String.format("%.2f", amount),
                    "Status: Successful"));
            mtagIdField.clear();
            amountField.clear();
            messageLabel.setText("M-Tag payment successful. Receipt: " + receipt.getPath());
            ToastManager.show(messageLabel, "M-Tag payment successful");
            ReceiptHelper.showReceipt(transaction, "M-Tag Payment");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException exception) {
            showInfo("M-Tag payment successful, but receipt could not be created.");
        }
    }

    private double readAmount() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException exception) {
            showError("Amount must be a valid number.");
            return -1;
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("M-Tag Payment", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("M-Tag Payment", message);
    }
}
