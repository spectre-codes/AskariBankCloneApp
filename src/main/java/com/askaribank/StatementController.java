package com.askaribank;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatementController {

    private final TransactionService transactionService = new TransactionService();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Customer currentStatementCustomer;
    private ComboBox<Customer> customerCombo;
    private TextField accountSearchField;
    private Label messageLabel;
    private Label nameLabel;
    private Label cnicLabel;
    private Label phoneLabel;
    private Label accountNumberLabel;
    private Label accountTypeLabel;
    private Label cardTypeLabel;
    private Label balanceLabel;
    private Label dateOpenedLabel;
    private TableView<Transaction> transactionTable;
    private TableColumn<Transaction, String> transactionIdColumn;
    private TableColumn<Transaction, LocalDateTime> dateTimeColumn;
    private TableColumn<Transaction, TransactionType> transactionTypeColumn;
    private TableColumn<Transaction, String> fromAccountColumn;
    private TableColumn<Transaction, String> toAccountColumn;
    private TableColumn<Transaction, Double> transactionAmountColumn;
    private TableColumn<Transaction, Double> balanceAfterColumn;
    private TableColumn<Transaction, String> descriptionColumn;
    private TableView<LoanApplication> relatedLoansTable;
    private TableColumn<LoanApplication, String> loanTypeColumn;
    private TableColumn<LoanApplication, Double> loanAmountColumn;
    private TableColumn<LoanApplication, Integer> tenureColumn;
    private TableColumn<LoanApplication, String> statusColumn;
    private TableColumn<LoanApplication, String> reasonColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Statements"),
                new Label("Search an account and view customer details with full transaction history.")
        );

        accountSearchField = UiFactory.textField("ASK-2026-00001");
        accountSearchField.setPrefWidth(190);
        customerCombo = UiFactory.comboBox("Select customer account");
        customerCombo.setPrefWidth(330);
        Button loadButton = UiFactory.button("Load Statement");
        loadButton.setOnAction(event -> handleLoadStatement());
        Button exportButton = UiFactory.button("Export Statement");
        exportButton.setOnAction(event -> handleExportStatement());

        HBox lookupBox = UiFactory.hbox(10, Pos.CENTER_LEFT);
        lookupBox.getChildren().addAll(
                new Label("Account Number"),
                accountSearchField,
                new Label("or Select Customer"),
                customerCombo,
                loadButton,
                exportButton
        );

        messageLabel = UiFactory.messageLabel();

        nameLabel = new Label("-");
        cnicLabel = new Label("-");
        phoneLabel = new Label("-");
        accountNumberLabel = new Label("-");
        accountTypeLabel = new Label("-");
        cardTypeLabel = new Label("-");
        balanceLabel = new Label("-");
        dateOpenedLabel = new Label("-");
        GridPane detailsGrid = UiFactory.grid(18, 12);
        detailsGrid.add(new Label("Customer Name"), 0, 0);
        detailsGrid.add(nameLabel, 1, 0);
        detailsGrid.add(new Label("CNIC"), 2, 0);
        detailsGrid.add(cnicLabel, 3, 0);
        detailsGrid.add(new Label("Phone"), 0, 1);
        detailsGrid.add(phoneLabel, 1, 1);
        detailsGrid.add(new Label("Account Number"), 2, 1);
        detailsGrid.add(accountNumberLabel, 3, 1);
        detailsGrid.add(new Label("Account Type"), 0, 2);
        detailsGrid.add(accountTypeLabel, 1, 2);
        detailsGrid.add(new Label("Card Type"), 2, 2);
        detailsGrid.add(cardTypeLabel, 3, 2);
        detailsGrid.add(new Label("Current Balance"), 0, 3);
        detailsGrid.add(balanceLabel, 1, 3);
        detailsGrid.add(new Label("Date Opened"), 2, 3);
        detailsGrid.add(dateOpenedLabel, 3, 3);

        transactionIdColumn = UiFactory.column("Transaction ID", 115);
        dateTimeColumn = UiFactory.column("Date/Time", 135);
        transactionTypeColumn = UiFactory.column("Type", 115);
        fromAccountColumn = UiFactory.column("From Account", 145);
        toAccountColumn = UiFactory.column("To Account", 145);
        transactionAmountColumn = UiFactory.column("Amount", 125);
        descriptionColumn = UiFactory.column("Description", 240);
        balanceAfterColumn = UiFactory.column("Balance After", 150);
        transactionTable = UiFactory.table(300, transactionIdColumn, dateTimeColumn, transactionTypeColumn,
                fromAccountColumn, toAccountColumn, transactionAmountColumn, descriptionColumn, balanceAfterColumn);

        loanTypeColumn = UiFactory.column("Loan Type", 150);
        loanAmountColumn = UiFactory.column("Loan Amount", 160);
        tenureColumn = UiFactory.column("Tenure Years", 120);
        statusColumn = UiFactory.column("Status", 120);
        reasonColumn = UiFactory.column("Rejection Reason", 350);
        relatedLoansTable = UiFactory.table(240, loanTypeColumn, loanAmountColumn, tenureColumn,
                statusColumn, reasonColumn);

        page.getChildren().addAll(
                lookupBox,
                messageLabel,
                detailsGrid,
                UiFactory.separator(),
                UiFactory.sectionTitle("Bank Transaction History"),
                transactionTable,
                UiFactory.sectionTitle("Loan Applications for This Account"),
                relatedLoansTable
        );

        initialize();
        return UiFactory.scroll(page);
    }

    private void initialize() {
        customerCombo.setItems(FXCollections.observableArrayList(BankDataStore.allCustomers));
        customerCombo.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, selectedCustomer) -> showStatement(selectedCustomer));

        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDateTime"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        fromAccountColumn.setCellValueFactory(new PropertyValueFactory<>("fromAccountNumber"));
        toAccountColumn.setCellValueFactory(new PropertyValueFactory<>("toAccountNumber"));
        transactionAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceAfterColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfterTransaction"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
        loanAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        tenureColumn.setCellValueFactory(new PropertyValueFactory<>("tenureYears"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("rejectionReason"));

        TableColumnFormatters.dateTime(dateTimeColumn, dateTimeFormatter);
        TableColumnFormatters.transactionType(transactionTypeColumn);
        TableColumnFormatters.accountNumberOrDash(fromAccountColumn);
        TableColumnFormatters.accountNumberOrDash(toAccountColumn);
        TableColumnFormatters.currency(transactionAmountColumn);
        TableColumnFormatters.currency(balanceAfterColumn);
        TableColumnFormatters.currency(loanAmountColumn);
        clearStatement();
    }

    private void handleLoadStatement() {
        Customer selectedCustomer = null;
        String accountNumber = accountSearchField.getText().trim();
        if (!accountNumber.isEmpty()) {
            selectedCustomer = BankDataStore.findCustomerByAccountNumber(accountNumber);
        }

        if (selectedCustomer == null) {
            selectedCustomer = customerCombo.getValue();
        }

        if (selectedCustomer == null) {
            clearStatement();
            showMessage("Please enter a valid account number or select a customer account.", true);
            return;
        }

        showStatement(selectedCustomer);
    }

    private void showStatement(Customer customer) {
        if (customer == null) {
            clearStatement();
            return;
        }

        currentStatementCustomer = customer;
        nameLabel.setText(customer.getCustomerFullName());
        cnicLabel.setText(customer.getCnicNumber());
        phoneLabel.setText(customer.getPhoneNumber());
        accountNumberLabel.setText(customer.getAccountNumber());
        accountTypeLabel.setText(customer.getAccountType());
        cardTypeLabel.setText(customer.getCardType());
        balanceLabel.setText("PKR " + String.format("%.2f", customer.getAccountBalance()));
        dateOpenedLabel.setText(String.valueOf(customer.getDateOpened()));
        transactionTable.setItems(FXCollections.observableArrayList(
                transactionService.getTransactionsForAccount(customer.getAccountNumber())));
        relatedLoansTable.setItems(FXCollections.observableArrayList(findLoansForCustomer(customer)));
        showMessage("Statement loaded successfully.", false);
    }

    private void handleExportStatement() {
        if (currentStatementCustomer == null) {
            AlertHelper.showError("Export Error", "Please load a statement before exporting.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Full Statement");
        fileChooser.setInitialFileName("statement_" + currentStatementCustomer.getAccountNumber() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            StatementExporter.exportStatement(file, currentStatementCustomer, transactionTable.getItems(),
                    "Full Account Statement");
            AlertHelper.showInfo("Export Complete", "Statement exported successfully.");
        } catch (IOException exception) {
            AlertHelper.showError("Export Error", "Statement could not be exported.");
        }
    }

    private ArrayList<LoanApplication> findLoansForCustomer(Customer customer) {
        ArrayList<LoanApplication> relatedLoans = new ArrayList<>();
        for (LoanApplication application : BankDataStore.allLoanApplications) {
            if (application.getLinkedAccountNumber().equals(customer.getAccountNumber())) {
                relatedLoans.add(application);
            }
        }
        return relatedLoans;
    }

    private void clearStatement() {
        currentStatementCustomer = null;
        nameLabel.setText("-");
        cnicLabel.setText("-");
        phoneLabel.setText("-");
        accountNumberLabel.setText("-");
        accountTypeLabel.setText("-");
        cardTypeLabel.setText("-");
        balanceLabel.setText("-");
        dateOpenedLabel.setText("-");
        transactionTable.setItems(FXCollections.observableArrayList());
        relatedLoansTable.setItems(FXCollections.observableArrayList());
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
    }

}
