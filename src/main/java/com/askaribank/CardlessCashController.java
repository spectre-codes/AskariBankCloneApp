package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
import java.util.Random;

public class CardlessCashController {

    private final TransactionService transactionService = new TransactionService();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Random random = new Random();

    private TextField amountField;
    private Label messageLabel;
    private TableView<CardlessCashRequest> requestTable;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Cardless Cash Withdrawal"),
                new Label("Generate a 6-digit withdrawal code, then simulate ATM withdrawal.")
        );

        amountField = UiFactory.textField("e.g. 5000");
        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(520);
        grid.add(new Label("Amount"), 0, 0);
        UiFactory.addGrowing(grid, amountField, 1, 0);

        Button generateButton = UiFactory.button("Generate Code");
        generateButton.setOnAction(event -> handleGenerateCode());
        Button withdrawButton = UiFactory.button("Simulate ATM Withdraw");
        withdrawButton.setOnAction(event -> handleSimulateWithdraw());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(generateButton, withdrawButton);
        grid.add(actions, 1, 1);

        messageLabel = UiFactory.messageLabel();

        TableColumn<CardlessCashRequest, String> requestIdColumn = UiFactory.column("Request ID", 115);
        TableColumn<CardlessCashRequest, Double> amountColumn = UiFactory.column("Amount", 110);
        TableColumn<CardlessCashRequest, String> codeColumn = UiFactory.column("Code", 90);
        TableColumn<CardlessCashRequest, LocalDateTime> expiresColumn = UiFactory.column("Expires", 145);
        TableColumn<CardlessCashRequest, String> statusColumn = UiFactory.column("Status", 110);
        requestTable = UiFactory.table(320, requestIdColumn, amountColumn, codeColumn, expiresColumn, statusColumn);

        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("withdrawalCode"));
        expiresColumn.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumnFormatters.currency(amountColumn);
        TableColumnFormatters.dateTime(expiresColumn, dateTimeFormatter);

        page.getChildren().addAll(grid, messageLabel, UiFactory.sectionTitle("My Cardless Cash Requests"), requestTable);
        refreshTable();
        return page;
    }

    private void handleGenerateCode() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        double amount = readAmount();
        if (amount <= 0) {
            return;
        }

        if (customer.isLocked() || customer.isFrozen()) {
            showError("Your account is temporarily restricted. Please contact branch staff.");
            return;
        }

        if (customer.getAccountBalance() < amount) {
            showError("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "cardless cash code generation")) {
            return;
        }

        LocalDateTime createdAt = LocalDateTime.now();
        CardlessCashRequest request = new CardlessCashRequest(
                BankDataStore.generateCardlessCashRequestId(),
                customer.getAccountNumber(),
                amount,
                String.format("%06d", random.nextInt(1_000_000)),
                createdAt,
                createdAt.plusMinutes(10),
                "ACTIVE");

        BankDataStore.allCardlessCashRequests.add(request);
        FileDataStore.saveAll();
        AuditLogger.log("CARDLESS_CASH_CODE", request.getRequestId() + " for " + customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(),
                "Cardless cash code " + request.getWithdrawalCode() + " was generated.");
        amountField.clear();
        refreshTable();
        showInfo("Code generated: " + request.getWithdrawalCode() + ". Valid until "
                + request.getExpiresAt().format(dateTimeFormatter) + ".");
    }

    private void handleSimulateWithdraw() {
        Customer customer = LoginSession.getLoggedInCustomer();
        CardlessCashRequest request = requestTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        if (request == null) {
            showError("Please select an active cardless cash request.");
            return;
        }

        if (!"ACTIVE".equals(request.getStatus())) {
            showError("Only ACTIVE requests can be withdrawn.");
            return;
        }

        if (LocalDateTime.now().isAfter(request.getExpiresAt())) {
            request.setStatus("EXPIRED");
            FileDataStore.saveAll();
            refreshTable();
            showError("This cardless cash code has expired.");
            return;
        }

        if (!TransactionConfirmationDialog.confirm("Cardless Cash", customer.getAccountNumber(), "ATM Withdrawal",
                request.getAmount(), "Code " + request.getWithdrawalCode())) {
            return;
        }

        if (!TransactionPinDialog.confirm(customer, "cardless cash withdrawal")) {
            return;
        }

        ProcessingOverlay.run(messageLabel, "Processing secure transaction...",
                () -> processWithdraw(customer, request));
    }

    private void processWithdraw(Customer customer, CardlessCashRequest request) {
        try {
            Transaction transaction = transactionService.cardlessCashWithdrawal(customer.getAccountNumber(), request.getAmount(),
                    "Cardless cash code " + request.getWithdrawalCode());
            request.setStatus("USED");
            FileDataStore.saveAll();
            refreshTable();
            messageLabel.setText("Cardless cash withdrawal completed.");
            ToastManager.show(messageLabel, "Cardless cash withdrawal completed");
            ReceiptHelper.showReceipt(transaction, "Cardless Cash");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTable() {
        Customer customer = LoginSession.getLoggedInCustomer();
        String accountNumber = customer == null ? "" : customer.getAccountNumber();
        ArrayList<CardlessCashRequest> customerRequests = new ArrayList<>();
        for (int index = BankDataStore.allCardlessCashRequests.size() - 1; index >= 0; index--) {
            CardlessCashRequest request = BankDataStore.allCardlessCashRequests.get(index);
            if (accountNumber.equals(request.getAccountNumber())) {
                if ("ACTIVE".equals(request.getStatus()) && LocalDateTime.now().isAfter(request.getExpiresAt())) {
                    request.setStatus("EXPIRED");
                }
                customerRequests.add(request);
            }
        }
        requestTable.setItems(FXCollections.observableArrayList(customerRequests));
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
        AlertHelper.showError("Cardless Cash", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Cardless Cash", message);
    }
}
