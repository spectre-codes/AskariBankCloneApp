package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransactionsController {

    private final TransactionService transactionService = new TransactionService();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ComboBox<String> transactionTypeCombo;
    private TextField accountNumberField;
    private Label receiverAccountLabel;
    private TextField receiverAccountNumberField;
    private TextField amountField;
    private TextField descriptionField;
    private Label messageLabel;
    private TableView<Transaction> transactionTable;
    private TableColumn<Transaction, String> transactionIdColumn;
    private TableColumn<Transaction, LocalDateTime> dateTimeColumn;
    private TableColumn<Transaction, TransactionType> transactionTypeColumn;
    private TableColumn<Transaction, String> fromAccountColumn;
    private TableColumn<Transaction, String> toAccountColumn;
    private TableColumn<Transaction, Double> amountColumn;
    private TableColumn<Transaction, String> descriptionColumn;
    private TableColumn<Transaction, Double> balanceAfterColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Transactions"),
                new Label("Process deposits, withdrawals, and account transfers.")
        );

        GridPane formGrid = UiFactory.grid(14, 12);
        transactionTypeCombo = UiFactory.comboBox("Select transaction type");
        accountNumberField = UiFactory.textField("e.g. ASK-2026-00001");
        receiverAccountLabel = new Label("Receiver Account");
        receiverAccountNumberField = UiFactory.textField("For transfer only");
        amountField = UiFactory.textField("e.g. 5000");
        descriptionField = UiFactory.textField("Optional transaction note");
        GridPane.setColumnSpan(descriptionField, 2);

        formGrid.add(new Label("Transaction Type"), 0, 0);
        UiFactory.addGrowing(formGrid, transactionTypeCombo, 1, 0);
        formGrid.add(new Label("Account Number"), 2, 0);
        UiFactory.addGrowing(formGrid, accountNumberField, 3, 0);
        formGrid.add(receiverAccountLabel, 0, 1);
        UiFactory.addGrowing(formGrid, receiverAccountNumberField, 1, 1);
        formGrid.add(new Label("Amount"), 2, 1);
        UiFactory.addGrowing(formGrid, amountField, 3, 1);
        formGrid.add(new Label("Description"), 0, 2);
        UiFactory.growHorizontally(descriptionField);
        formGrid.add(descriptionField, 1, 2);

        Button processButton = UiFactory.button("Process Transaction");
        processButton.setOnAction(event -> handleProcessTransaction());
        Button clearButton = UiFactory.button("Clear");
        clearButton.setOnAction(event -> handleClearForm());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(processButton, clearButton);
        formGrid.add(actions, 3, 2);

        messageLabel = UiFactory.messageLabel();

        transactionIdColumn = UiFactory.column("Transaction ID", 100);
        dateTimeColumn = UiFactory.column("Date/Time", 135);
        transactionTypeColumn = UiFactory.column("Type", 120);
        fromAccountColumn = UiFactory.column("From Account", 145);
        toAccountColumn = UiFactory.column("To Account", 145);
        amountColumn = UiFactory.column("Amount", 125);
        descriptionColumn = UiFactory.column("Description", 240);
        balanceAfterColumn = UiFactory.column("Balance After", 150);
        transactionTable = UiFactory.table(380, transactionIdColumn, dateTimeColumn, transactionTypeColumn,
                fromAccountColumn, toAccountColumn, amountColumn, descriptionColumn, balanceAfterColumn);

        page.getChildren().addAll(
                formGrid,
                messageLabel,
                UiFactory.sectionTitle("Recent Transactions"),
                transactionTable
        );

        initialize();
        return UiFactory.scroll(page);
    }

    private void initialize() {
        transactionTypeCombo.setItems(FXCollections.observableArrayList("Deposit", "Withdrawal", "Transfer"));
        transactionTypeCombo.getSelectionModel().selectFirst();
        transactionTypeCombo.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, selectedType) -> updateReceiverField(selectedType));

        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDateTime"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        fromAccountColumn.setCellValueFactory(new PropertyValueFactory<>("fromAccountNumber"));
        toAccountColumn.setCellValueFactory(new PropertyValueFactory<>("toAccountNumber"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        balanceAfterColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfterTransaction"));

        TableColumnFormatters.dateTime(dateTimeColumn, dateTimeFormatter);
        TableColumnFormatters.transactionType(transactionTypeColumn);
        TableColumnFormatters.accountNumberOrDash(fromAccountColumn);
        TableColumnFormatters.accountNumberOrDash(toAccountColumn);
        TableColumnFormatters.currency(amountColumn);
        TableColumnFormatters.currency(balanceAfterColumn);

        updateReceiverField(transactionTypeCombo.getValue());
        refreshTransactionTable();
    }

    private void handleProcessTransaction() {
        String selectedType = transactionTypeCombo.getValue();
        String accountNumber = accountNumberField.getText().trim();
        String receiverAccountNumber = receiverAccountNumberField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (selectedType == null || accountNumber.isEmpty() || amountText.isEmpty()) {
            showError("Please select transaction type, enter account number, and enter amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            showError("Amount must be a valid number.");
            return;
        }

        try {
            switch (selectedType) {
                case "Deposit" -> transactionService.deposit(accountNumber, amount, description);
                case "Withdrawal" -> transactionService.withdraw(accountNumber, amount, description);
                case "Transfer" -> {
                    if (receiverAccountNumber.isEmpty()) {
                        showError("Please enter receiver account number for transfer.");
                        return;
                    }
                    transactionService.transfer(accountNumber, receiverAccountNumber, amount, description);
                }
                default -> {
                    showError("Please select a valid transaction type.");
                    return;
                }
            }
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
            return;
        }

        refreshTransactionTable();
        clearAmountAndDescriptionFields();
        showMessage(selectedType + " processed successfully.", false);
    }

    private void handleClearForm() {
        accountNumberField.clear();
        receiverAccountNumberField.clear();
        amountField.clear();
        descriptionField.clear();
        transactionTypeCombo.getSelectionModel().selectFirst();
        showMessage("", false);
    }

    private void updateReceiverField(String selectedType) {
        boolean transferSelected = "Transfer".equals(selectedType);
        receiverAccountLabel.setDisable(!transferSelected);
        receiverAccountNumberField.setDisable(!transferSelected);

        if (!transferSelected) {
            receiverAccountNumberField.clear();
        }
    }

    private void refreshTransactionTable() {
        ArrayList<Transaction> recentTransactions = new ArrayList<>();
        for (int index = BankDataStore.allTransactions.size() - 1; index >= 0; index--) {
            recentTransactions.add(BankDataStore.allTransactions.get(index));
        }
        transactionTable.setItems(FXCollections.observableArrayList(recentTransactions));
    }

    private void clearAmountAndDescriptionFields() {
        amountField.clear();
        descriptionField.clear();
    }

    private void showError(String message) {
        showMessage(message, true);
        AlertHelper.showError("Transaction Error", message);
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
    }

}
