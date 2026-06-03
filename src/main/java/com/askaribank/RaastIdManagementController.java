package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RaastIdManagementController {

    private Label accountNumberLabel;
    private Label currentRaastLabel;
    private Label statusLabel;
    private TextField mobileNumberField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Raast ID Management"),
                new Label("Link or unlink your registered mobile number as a simple Raast ID.")
        );

        accountNumberLabel = new Label("-");
        currentRaastLabel = new Label("-");
        statusLabel = new Label("-");
        mobileNumberField = UiFactory.textField("Registered mobile number");

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(560);
        grid.add(new Label("Account Number"), 0, 0);
        grid.add(accountNumberLabel, 1, 0);
        grid.add(new Label("Current Raast ID"), 0, 1);
        grid.add(currentRaastLabel, 1, 1);
        grid.add(new Label("Status"), 0, 2);
        grid.add(statusLabel, 1, 2);
        grid.add(new Label("Mobile/Raast ID"), 0, 3);
        UiFactory.addGrowing(grid, mobileNumberField, 1, 3);

        Button linkButton = UiFactory.button("Link Registered Mobile");
        linkButton.setOnAction(event -> handleLinkRaastId());
        Button unlinkButton = UiFactory.button("Unlink Raast ID");
        unlinkButton.setOnAction(event -> handleUnlinkRaastId());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(linkButton, unlinkButton);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, actions, messageLabel);
        refresh();
        return page;
    }

    private void handleLinkRaastId() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String mobileNumber = mobileNumberField.getText().trim();
        if (mobileNumber.isEmpty()) {
            showError("Mobile/Raast ID cannot be empty.");
            return;
        }

        if (!cleanDigits(mobileNumber).equals(cleanDigits(customer.getPhoneNumber()))) {
            showError("Please link your registered mobile number only.");
            return;
        }

        Customer existingCustomer = BankDataStore.findCustomerByRaastId(mobileNumber);
        if (existingCustomer != null && !existingCustomer.getAccountNumber().equals(customer.getAccountNumber())) {
            showError("This mobile/Raast ID is already linked with another customer.");
            return;
        }

        customer.setRaastId(mobileNumber);
        customer.setRaastLinked(true);
        FileDataStore.saveAll();
        AuditLogger.log("RAAST_ID_LINKED", customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Your Raast ID was linked successfully.");
        refresh();
        showInfo("Raast ID linked successfully.");
    }

    private void handleUnlinkRaastId() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        customer.setRaastId("");
        customer.setRaastLinked(false);
        FileDataStore.saveAll();
        AuditLogger.log("RAAST_ID_UNLINKED", customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Your Raast ID was unlinked.");
        refresh();
        showInfo("Raast ID unlinked successfully.");
    }

    private void refresh() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            return;
        }

        accountNumberLabel.setText(customer.getAccountNumber());
        currentRaastLabel.setText(customer.isRaastLinked() ? customer.getRaastId() : "-");
        statusLabel.setText(customer.isRaastLinked() ? "LINKED" : "NOT LINKED");
        mobileNumberField.setText(customer.isRaastLinked() ? customer.getRaastId() : customer.getPhoneNumber());
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Raast ID", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Raast ID", message);
    }

    private String cleanDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
