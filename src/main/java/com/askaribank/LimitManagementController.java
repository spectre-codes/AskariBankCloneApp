package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LimitManagementController {

    private TextField transferLimitField;
    private TextField billLimitField;
    private TextField topUpLimitField;
    private TextField cardlessLimitField;
    private Label usageLabel;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Limit Management"),
                new Label("Set simple daily limits for your customer transactions.")
        );

        transferLimitField = UiFactory.textField("Daily transfer limit");
        billLimitField = UiFactory.textField("Daily bill payment limit");
        topUpLimitField = UiFactory.textField("Daily mobile top-up limit");
        cardlessLimitField = UiFactory.textField("Daily cardless cash limit");

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(560);
        grid.add(new Label("Daily Transfer Limit"), 0, 0);
        UiFactory.addGrowing(grid, transferLimitField, 1, 0);
        grid.add(new Label("Daily Bill Payment Limit"), 0, 1);
        UiFactory.addGrowing(grid, billLimitField, 1, 1);
        grid.add(new Label("Daily Mobile Top-up Limit"), 0, 2);
        UiFactory.addGrowing(grid, topUpLimitField, 1, 2);
        grid.add(new Label("Daily Cardless Cash Limit"), 0, 3);
        UiFactory.addGrowing(grid, cardlessLimitField, 1, 3);

        Button saveButton = UiFactory.button("Save Limits");
        saveButton.setOnAction(event -> handleSaveLimits());
        grid.add(saveButton, 1, 4);

        usageLabel = UiFactory.messageLabel();
        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, UiFactory.sectionTitle("Used Today"), usageLabel, messageLabel);
        loadLimits();
        return page;
    }

    private void loadLimits() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            return;
        }

        LimitService.resetDailyUsageIfNeeded(customer);
        transferLimitField.setText(String.format("%.0f", customer.getDailyTransferLimit()));
        billLimitField.setText(String.format("%.0f", customer.getDailyBillPaymentLimit()));
        topUpLimitField.setText(String.format("%.0f", customer.getDailyTopUpLimit()));
        cardlessLimitField.setText(String.format("%.0f", customer.getDailyCardlessCashLimit()));
        usageLabel.setText("Transfer: PKR " + String.format("%.2f", customer.getUsedTransferToday())
                + " | Bills: PKR " + String.format("%.2f", customer.getUsedBillPaymentToday())
                + " | Top-up: PKR " + String.format("%.2f", customer.getUsedTopUpToday())
                + " | Cardless Cash: PKR " + String.format("%.2f", customer.getUsedCardlessCashToday()));
    }

    private void handleSaveLimits() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        try {
            customer.setDailyTransferLimit(readPositiveAmount(transferLimitField.getText(), "transfer limit"));
            customer.setDailyBillPaymentLimit(readPositiveAmount(billLimitField.getText(), "bill payment limit"));
            customer.setDailyTopUpLimit(readPositiveAmount(topUpLimitField.getText(), "mobile top-up limit"));
            customer.setDailyCardlessCashLimit(readPositiveAmount(cardlessLimitField.getText(), "cardless cash limit"));
            FileDataStore.saveAll();
            AuditLogger.log("LIMITS_UPDATED", customer.getAccountNumber());
            loadLimits();
            showInfo("Limits updated successfully.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private double readPositiveAmount(String value, String fieldName) {
        try {
            double amount = Double.parseDouble(value.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("The " + fieldName + " must be greater than zero.");
            }
            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please enter a valid number for " + fieldName + ".");
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Limit Management", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Limit Management", message);
    }
}
