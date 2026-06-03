package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class MobileTopUpController {

    private final TransactionService transactionService = new TransactionService();

    private ComboBox<String> networkCombo;
    private TextField mobileNumberField;
    private TextField amountField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Mobile Top-up"),
                new Label("Pay mobile top-up directly from your account balance.")
        );

        networkCombo = UiFactory.comboBox("Select network");
        mobileNumberField = UiFactory.textField("e.g. 03001234567");
        amountField = UiFactory.textField("e.g. 500");

        GridPane formGrid = UiFactory.grid(14, 12);
        formGrid.setMaxWidth(520);
        formGrid.add(new Label("Network"), 0, 0);
        UiFactory.addGrowing(formGrid, networkCombo, 1, 0);
        formGrid.add(new Label("Mobile Number"), 0, 1);
        UiFactory.addGrowing(formGrid, mobileNumberField, 1, 1);
        formGrid.add(new Label("Amount"), 0, 2);
        UiFactory.addGrowing(formGrid, amountField, 1, 2);
        Button payButton = UiFactory.button("Pay Top-up");
        payButton.setOnAction(event -> handlePayTopUp());
        formGrid.add(payButton, 1, 3);

        messageLabel = UiFactory.messageLabel();

        page.getChildren().addAll(formGrid, messageLabel);

        initialize();
        return page;
    }

    private void initialize() {
        networkCombo.setItems(FXCollections.observableArrayList("Jazz", "Zong", "Telenor", "Ufone"));
    }

    private void handlePayTopUp() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String network = networkCombo.getValue();
        String mobileNumber = mobileNumberField.getText().trim();
        String amountText = amountField.getText().trim();

        if (network == null || mobileNumber.isEmpty() || amountText.isEmpty()) {
            showError("Please select network, enter mobile number, and enter amount.");
            return;
        }

        if (!mobileNumber.matches("\\d{10,12}")) {
            showError("Mobile number must contain 10 to 12 digits.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            showError("Amount must be a valid number.");
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Mobile Top-up", customer.getAccountNumber(), mobileNumber,
                amount, network)) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "mobile top-up")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processTopUp(customer, mobileNumber, network, amount));
    }

    private void processTopUp(Customer customer, String mobileNumber, String network, double amount) {
        try {
            Transaction transaction = transactionService.mobileTopUp(customer.getAccountNumber(), mobileNumber, network, amount);
            mobileNumberField.clear();
            amountField.clear();
            messageLabel.setText("Mobile top-up successful.");
            ToastManager.show(messageLabel, "Mobile top-up successful");
            ReceiptHelper.showReceipt(transaction, "Mobile Top-up");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Mobile Top-up Error", message);
    }

    private void showInfo(String message) {
        AlertHelper.showInfo("Mobile Top-up", message);
    }
}
