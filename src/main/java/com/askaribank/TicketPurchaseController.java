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

public class TicketPurchaseController {

    private final TransactionService transactionService = new TransactionService();

    private ComboBox<String> ticketTypeCombo;
    private ComboBox<String> providerCombo;
    private TextField quantityField;
    private TextField amountField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Purchase Tickets"),
                new Label("Simple ticket purchase simulation for bus, cinema, and events.")
        );

        ticketTypeCombo = UiFactory.comboBox("Select ticket type");
        ticketTypeCombo.setItems(FXCollections.observableArrayList("Bus", "Cinema", "Event"));
        providerCombo = UiFactory.comboBox("Select provider");
        providerCombo.setItems(FXCollections.observableArrayList("Daewoo", "Faisal Movers", "Cinema", "Event"));
        quantityField = UiFactory.textField("e.g. 2");
        amountField = UiFactory.textField("Total amount");

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(560);
        grid.add(new Label("Ticket Type"), 0, 0);
        UiFactory.addGrowing(grid, ticketTypeCombo, 1, 0);
        grid.add(new Label("Provider"), 0, 1);
        UiFactory.addGrowing(grid, providerCombo, 1, 1);
        grid.add(new Label("Quantity"), 0, 2);
        UiFactory.addGrowing(grid, quantityField, 1, 2);
        grid.add(new Label("Amount"), 0, 3);
        UiFactory.addGrowing(grid, amountField, 1, 3);

        Button purchaseButton = UiFactory.button("Purchase Ticket");
        purchaseButton.setOnAction(event -> handlePurchase());
        grid.add(purchaseButton, 1, 4);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, messageLabel);
        return page;
    }

    private void handlePurchase() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String ticketType = ticketTypeCombo.getValue();
        String provider = providerCombo.getValue();
        if (ticketType == null || provider == null) {
            showError("Please select ticket type and provider.");
            return;
        }

        int quantity = readQuantity();
        double amount = readAmount();
        if (quantity <= 0 || amount <= 0) {
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Ticket Purchase", customer.getAccountNumber(), provider,
                amount, ticketType + " x " + quantity)) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "ticket purchase")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processPurchase(customer, ticketType, provider, quantity, amount));
    }

    private void processPurchase(Customer customer, String ticketType, String provider, int quantity, double amount) {
        String ticketReference = "TKT-" + System.currentTimeMillis();
        try {
            Transaction transaction = transactionService.customerPayment(customer.getAccountNumber(), provider,
                    TransactionType.TICKET_PURCHASE, amount,
                    ticketType + " ticket purchase Ref: " + ticketReference,
                    "TICKET_PURCHASE", "Ticket purchase of PKR "
                            + String.format("%.2f", amount) + " was successful. Ref: " + ticketReference);
            File receipt = ReceiptHelper.saveReceipt("ticket", "Ticket Purchase Receipt", List.of(
                    "Transaction ID: " + transaction.getTransactionId(),
                    "Ticket Ref: " + ticketReference,
                    "Ticket Type: " + ticketType,
                    "Provider: " + provider,
                    "Quantity: " + quantity,
                    "Amount: PKR " + String.format("%.2f", amount),
                    "Status: Successful"));
            clearForm();
            messageLabel.setText("Ticket purchased. Ref: " + ticketReference + ". Receipt: " + receipt.getPath());
            ToastManager.show(messageLabel, "Ticket purchased");
            ReceiptHelper.showReceipt(transaction, "Ticket Purchase");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException exception) {
            showInfo("Ticket purchased, but receipt could not be created.");
        }
    }

    private int readQuantity() {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showError("Quantity must be greater than zero.");
                return -1;
            }
            return quantity;
        } catch (NumberFormatException exception) {
            showError("Quantity must be a valid whole number.");
            return -1;
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

    private void clearForm() {
        ticketTypeCombo.getSelectionModel().clearSelection();
        providerCombo.getSelectionModel().clearSelection();
        quantityField.clear();
        amountField.clear();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Purchase Tickets", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Purchase Tickets", message);
    }
}
