package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CustomerTransferController {

    private final TransactionService transactionService = new TransactionService();
    private boolean mobileTransferMode = false;

    private ToggleButton accountTransferButton;
    private ToggleButton mobileTransferButton;
    private ComboBox<Beneficiary> beneficiaryCombo;
    private Label receiverLabel;
    private TextField receiverField;
    private TextField amountField;
    private TextField descriptionField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Transfer Money"),
                new Label("Transfer funds to another Askari account by account number or registered mobile/Raast number.")
        );

        accountTransferButton = UiFactory.toggleButton("Account Number", 150);
        accountTransferButton.setOnAction(event -> selectAccountMode());
        mobileTransferButton = UiFactory.toggleButton("Mobile/Raast", 150);
        mobileTransferButton.setOnAction(event -> selectMobileMode());
        HBox modeButtons = UiFactory.hbox(10);
        modeButtons.getChildren().addAll(accountTransferButton, mobileTransferButton);

        beneficiaryCombo = UiFactory.comboBox("Select saved payee");
        beneficiaryCombo.setPrefWidth(360);
        beneficiaryCombo.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, beneficiary) -> handleBeneficiarySelected(beneficiary));
        HBox beneficiaryRow = UiFactory.hbox(10);
        beneficiaryRow.getChildren().addAll(new Label("Saved Payee"), beneficiaryCombo);

        receiverLabel = new Label("Receiver Account Number");
        receiverField = UiFactory.textField("e.g. ASK-2026-00002");
        amountField = UiFactory.textField("e.g. 5000");
        descriptionField = UiFactory.textField("Optional note");

        GridPane formGrid = UiFactory.grid(14, 12);
        formGrid.setMaxWidth(620);
        formGrid.add(receiverLabel, 0, 0);
        UiFactory.addGrowing(formGrid, receiverField, 1, 0);
        formGrid.add(new Label("Amount"), 0, 1);
        UiFactory.addGrowing(formGrid, amountField, 1, 1);
        formGrid.add(new Label("Description"), 0, 2);
        UiFactory.addGrowing(formGrid, descriptionField, 1, 2);
        Button transferButton = UiFactory.button("Transfer");
        transferButton.setOnAction(event -> handleTransfer());
        formGrid.add(transferButton, 1, 3);

        messageLabel = UiFactory.messageLabel();

        page.getChildren().addAll(
                modeButtons,
                UiFactory.separator(),
                beneficiaryRow,
                formGrid,
                messageLabel
        );

        initialize();
        return page;
    }

    private void initialize() {
        loadBeneficiaries();
        selectAccountMode();
    }

    private void selectAccountMode() {
        mobileTransferMode = false;
        accountTransferButton.setSelected(true);
        mobileTransferButton.setSelected(false);
        receiverLabel.setText("Receiver Account Number");
        receiverField.setPromptText("e.g. ASK-2026-00002");
        messageLabel.setText("");
    }

    private void selectMobileMode() {
        mobileTransferMode = true;
        accountTransferButton.setSelected(false);
        mobileTransferButton.setSelected(true);
        receiverLabel.setText("Receiver Mobile/Raast Number");
        receiverField.setPromptText("e.g. 03007654321");
        messageLabel.setText("");
    }

    private void handleTransfer() {
        Customer sender = LoginSession.getLoggedInCustomer();
        if (sender == null) {
            showError("No customer is logged in.");
            return;
        }

        String receiverInput = receiverField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (receiverInput.isEmpty() || amountText.isEmpty()) {
            showError("Please enter receiver details and amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            showError("Amount must be a valid number.");
            return;
        }

        Customer receiver = mobileTransferMode
                ? findRaastOrMobileReceiver(receiverInput)
                : BankDataStore.findCustomerByAccountNumber(receiverInput);

        if (receiver == null) {
            showError("Receiver account was not found.");
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Funds Transfer", sender.getAccountNumber(), receiverInput,
                amount, description)) {
            return;
        }

        if (!TransactionPinDialog.confirm(sender, "transfer")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...", () -> processTransfer(
                sender, receiver, receiverInput, amount, description));
    }

    private void processTransfer(Customer sender, Customer receiver, String receiverInput,
                                 double amount, String description) {
        try {
            Transaction transaction = mobileTransferMode
                    ? transactionService.raastTransfer(sender.getAccountNumber(), receiverInput, amount, description)
                    : transactionService.transfer(sender.getAccountNumber(), receiver.getAccountNumber(), amount, description);
            File receiptFile = writeReceipt(transaction, receiverInput);
            clearForm();
            String successMessage = "Transfer successful. Receipt: " + receiptFile.getPath();
            messageLabel.setText(successMessage);
            ToastManager.show(messageLabel, "Transfer successful");
            ReceiptHelper.showReceipt(transaction, "Funds Transfer");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private Customer findRaastOrMobileReceiver(String receiverInput) {
        Customer receiver = BankDataStore.findCustomerByRaastId(receiverInput);
        if (receiver != null) {
            return receiver;
        }
        return BankDataStore.findCustomerByPhoneNumber(receiverInput);
    }

    private void loadBeneficiaries() {
        Customer sender = LoginSession.getLoggedInCustomer();
        String ownerAccountNumber = sender == null ? "" : sender.getAccountNumber();
        beneficiaryCombo.setItems(FXCollections.observableArrayList(
                new BeneficiaryService().getBeneficiariesForOwner(ownerAccountNumber)));
    }

    private void handleBeneficiarySelected(Beneficiary beneficiary) {
        if (beneficiary == null) {
            return;
        }

        if (beneficiary.getBeneficiaryAccountNumber() != null
                && !beneficiary.getBeneficiaryAccountNumber().isBlank()) {
            selectAccountMode();
            receiverField.setText(beneficiary.getBeneficiaryAccountNumber());
        } else if (beneficiary.getBeneficiaryMobileNumber() != null
                && !beneficiary.getBeneficiaryMobileNumber().isBlank()) {
            selectMobileMode();
            receiverField.setText(beneficiary.getBeneficiaryMobileNumber());
        }
    }

    private File writeReceipt(Transaction transaction, String receiverInput) {
        File receiptDirectory = new File("data/receipts");
        if (!receiptDirectory.exists()) {
            receiptDirectory.mkdirs();
        }

        File receiptFile = new File(receiptDirectory, "receipt_" + transaction.getTransactionId() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile))) {
            writer.write(BankDataStore.appName);
            writer.newLine();
            writer.write("Transaction ID: " + transaction.getTransactionId());
            writer.newLine();
            writer.write("Date/Time: " + transaction.getTransactionDateTime());
            writer.newLine();
            writer.write("Sender Account: " + transaction.getFromAccountNumber());
            writer.newLine();
            writer.write("Receiver Account/Mobile: " + receiverInput);
            writer.newLine();
            writer.write("Amount: PKR " + String.format("%.2f", transaction.getAmount()));
            writer.newLine();
            writer.write("Description: " + transaction.getDescription());
            writer.newLine();
            writer.write("Status: Successful");
            writer.newLine();
        } catch (IOException exception) {
            messageLabel.setText("Transfer successful, but receipt could not be created.");
        }
        return receiptFile;
    }

    private void clearForm() {
        receiverField.clear();
        amountField.clear();
        descriptionField.clear();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Transfer Error", message);
    }

    private void showInfo(String message) {
        AlertHelper.showInfo("Transfer Successful", message);
    }
}
