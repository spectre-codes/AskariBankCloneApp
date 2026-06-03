package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BillPaymentController {

    private final TransactionService transactionService = new TransactionService();

    private ComboBox<String> billTypeCombo;
    private TextField billerNameField;
    private TextField consumerNumberField;
    private TextField amountField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Bill Payments"),
                new Label("Pay utility bills directly from your Askari Bank account.")
        );

        billTypeCombo = UiFactory.comboBox("Select bill type");
        billerNameField = UiFactory.textField("e.g. LESCO, SNGPL, PTCL");
        consumerNumberField = UiFactory.textField("Consumer number");
        amountField = UiFactory.textField("e.g. 4500");

        GridPane formGrid = UiFactory.grid(14, 12);
        formGrid.setMaxWidth(560);
        formGrid.add(new Label("Bill Type"), 0, 0);
        UiFactory.addGrowing(formGrid, billTypeCombo, 1, 0);
        formGrid.add(new Label("Biller Name"), 0, 1);
        UiFactory.addGrowing(formGrid, billerNameField, 1, 1);
        formGrid.add(new Label("Consumer Number"), 0, 2);
        UiFactory.addGrowing(formGrid, consumerNumberField, 1, 2);
        formGrid.add(new Label("Amount"), 0, 3);
        UiFactory.addGrowing(formGrid, amountField, 1, 3);

        Button payButton = UiFactory.button("Pay Bill");
        payButton.setOnAction(event -> handlePayBill());
        formGrid.add(payButton, 1, 4);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(formGrid, messageLabel);

        billTypeCombo.setItems(FXCollections.observableArrayList("Electricity", "Gas", "Internet/PTCL", "Water"));
        return page;
    }

    private void handlePayBill() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String billType = billTypeCombo.getValue();
        String billerName = billerNameField.getText().trim();
        String consumerNumber = consumerNumberField.getText().trim();
        String amountText = amountField.getText().trim();

        if (billType == null || billerName.isEmpty() || consumerNumber.isEmpty() || amountText.isEmpty()) {
            showError("Please fill all bill payment fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            showError("Amount must be a valid number.");
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Bill Payment", customer.getAccountNumber(), billerName,
                amount, billType + " - " + consumerNumber)) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "bill payment")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processBillPayment(customer, billType, billerName, consumerNumber, amount));
    }

    private void processBillPayment(Customer customer, String billType, String billerName,
                                    String consumerNumber, double amount) {
        try {
            Transaction transaction = transactionService.billPayment(customer.getAccountNumber(), billType,
                    billerName, consumerNumber, amount);
            File receipt = writeReceipt(transaction, billType, billerName, consumerNumber);
            clearForm();
            String successMessage = "Bill payment successful. Receipt: " + receipt.getPath();
            messageLabel.setText(successMessage);
            ToastManager.show(messageLabel, "Bill payment successful");
            ReceiptHelper.showReceipt(transaction, "Bill Payment");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private File writeReceipt(Transaction transaction, String billType, String billerName, String consumerNumber) {
        File receiptDirectory = new File("data/receipts");
        if (!receiptDirectory.exists()) {
            receiptDirectory.mkdirs();
        }

        File receiptFile = new File(receiptDirectory, "bill_" + transaction.getTransactionId() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile))) {
            writer.write(BankDataStore.appName);
            writer.newLine();
            writer.write("Bill Payment Receipt");
            writer.newLine();
            writer.write("Transaction ID: " + transaction.getTransactionId());
            writer.newLine();
            writer.write("Date/Time: " + transaction.getTransactionDateTime());
            writer.newLine();
            writer.write("Account: " + transaction.getFromAccountNumber());
            writer.newLine();
            writer.write("Bill Type: " + billType);
            writer.newLine();
            writer.write("Biller: " + billerName);
            writer.newLine();
            writer.write("Consumer Number: " + consumerNumber);
            writer.newLine();
            writer.write("Amount: PKR " + String.format("%.2f", transaction.getAmount()));
            writer.newLine();
            writer.write("Status: Successful");
            writer.newLine();
        } catch (IOException exception) {
            messageLabel.setText("Bill paid, but receipt could not be created.");
        }
        return receiptFile;
    }

    private void clearForm() {
        billTypeCombo.getSelectionModel().clearSelection();
        billerNameField.clear();
        consumerNumberField.clear();
        amountField.clear();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Bill Payment Error", message);
    }

    private void showInfo(String message) {
        AlertHelper.showInfo("Bill Payment", message);
    }
}
