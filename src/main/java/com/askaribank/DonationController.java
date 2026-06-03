package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DonationController {

    private final TransactionService transactionService = new TransactionService();

    private ComboBox<String> organizationCombo;
    private TextField amountField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Donations"),
                new Label("Donate from your Askari account to a listed organization.")
        );

        organizationCombo = UiFactory.comboBox("Select organization");
        organizationCombo.setItems(FXCollections.observableArrayList(
                "Shaukat Khanum", "Edhi Foundation", "Akhuwat", "Saylani", "Flood Relief Fund"));
        amountField = UiFactory.textField("e.g. 2500");

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(540);
        grid.add(new Label("Organization"), 0, 0);
        UiFactory.addGrowing(grid, organizationCombo, 1, 0);
        grid.add(new Label("Amount"), 0, 1);
        UiFactory.addGrowing(grid, amountField, 1, 1);

        Button donateButton = UiFactory.button("Donate");
        donateButton.setOnAction(event -> handleDonation());
        grid.add(donateButton, 1, 2);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, messageLabel);
        return page;
    }

    private void handleDonation() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String organization = organizationCombo.getValue();
        if (organization == null || organization.isBlank()) {
            showError("Please select an organization.");
            return;
        }

        double amount = readAmount();
        if (amount <= 0) {
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Donation", customer.getAccountNumber(), organization,
                amount, "Donation")) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "donation")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processDonation(customer, organization, amount));
    }

    private void processDonation(Customer customer, String organization, double amount) {
        try {
            Transaction transaction = transactionService.customerPayment(customer.getAccountNumber(), organization,
                    TransactionType.DONATION, amount, "Donation to " + organization,
                    "DONATION", "Donation of PKR " + String.format("%.2f", amount)
                            + " to " + organization + " was successful.");
            File receipt = ReceiptHelper.saveReceipt("donation", "Donation Receipt", List.of(
                    "Transaction ID: " + transaction.getTransactionId(),
                    "Organization: " + organization,
                    "Amount: PKR " + String.format("%.2f", amount),
                    "Status: Successful"));
            organizationCombo.getSelectionModel().clearSelection();
            amountField.clear();
            messageLabel.setText("Donation successful. Receipt: " + receipt.getPath());
            ToastManager.show(messageLabel, "Donation successful");
            ReceiptHelper.showReceipt(transaction, "Donation");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException exception) {
            showInfo("Donation successful, but receipt could not be created.");
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
        AlertHelper.showError("Donations", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Donations", message);
    }
}
