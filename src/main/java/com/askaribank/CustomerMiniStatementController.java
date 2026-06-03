package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomerMiniStatementController {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Label accountNumberLabel;
    private Label balanceLabel;
    private VBox timelineBox;
    private TableView<Transaction> transactionTable;
    private TableColumn<Transaction, String> transactionIdColumn;
    private TableColumn<Transaction, LocalDateTime> dateTimeColumn;
    private TableColumn<Transaction, TransactionType> typeColumn;
    private TableColumn<Transaction, Double> amountColumn;
    private TableColumn<Transaction, String> descriptionColumn;
    private TableColumn<Transaction, Double> balanceAfterColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Mini Statement"),
                new Label("Last 10 transactions for the logged-in customer.")
        );

        accountNumberLabel = new Label("-");
        balanceLabel = new Label("-");
        GridPane detailsGrid = UiFactory.grid(18, 10);
        detailsGrid.add(new Label("Account Number"), 0, 0);
        detailsGrid.add(accountNumberLabel, 1, 0);
        detailsGrid.add(new Label("Current Balance"), 0, 1);
        detailsGrid.add(balanceLabel, 1, 1);

        transactionIdColumn = UiFactory.column("Transaction ID", 115);
        dateTimeColumn = UiFactory.column("Date/Time", 135);
        typeColumn = UiFactory.column("Type", 120);
        amountColumn = UiFactory.column("Amount", 125);
        descriptionColumn = UiFactory.column("Description", 260);
        balanceAfterColumn = UiFactory.column("Balance After", 150);
        transactionTable = UiFactory.table(380, transactionIdColumn, dateTimeColumn, typeColumn,
                amountColumn, descriptionColumn, balanceAfterColumn);

        Button exportButton = UiFactory.button("Export Mini Statement");
        exportButton.setOnAction(event -> handleExportStatement());

        timelineBox = new VBox(10);

        page.getChildren().addAll(detailsGrid, UiFactory.sectionTitle("Timeline"), timelineBox,
                exportButton, UiFactory.sectionTitle("Table View"), transactionTable);

        initialize();
        return page;
    }

    private void initialize() {
        setupTable();
        loadMiniStatement();
    }

    private void setupTable() {
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDateTime"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        balanceAfterColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfterTransaction"));

        TableColumnFormatters.dateTime(dateTimeColumn, dateTimeFormatter);
        TableColumnFormatters.transactionType(typeColumn);
        TableColumnFormatters.currency(amountColumn);
        TableColumnFormatters.currency(balanceAfterColumn);
    }

    private void loadMiniStatement() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            accountNumberLabel.setText("-");
            balanceLabel.setText("-");
            timelineBox.getChildren().clear();
            transactionTable.setItems(FXCollections.observableArrayList());
            return;
        }

        accountNumberLabel.setText(customer.getAccountNumber());
        balanceLabel.setText(String.format("PKR %,.2f", customer.getAccountBalance()));

        ArrayList<Transaction> allTransactions = BankDataStore.getTransactionsForAccount(customer.getAccountNumber());
        ArrayList<Transaction> lastTenTransactions = new ArrayList<>();
        for (int index = 0; index < allTransactions.size() && index < 10; index++) {
            lastTenTransactions.add(allTransactions.get(index));
        }
        buildTimeline(lastTenTransactions);
        transactionTable.setItems(FXCollections.observableArrayList(lastTenTransactions));
    }

    private void buildTimeline(ArrayList<Transaction> transactions) {
        timelineBox.getChildren().clear();
        LocalDate currentGroupDate = null;

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getTransactionDateTime().toLocalDate();
            if (!transactionDate.equals(currentGroupDate)) {
                currentGroupDate = transactionDate;
                timelineBox.getChildren().add(UiFactory.sectionTitle(groupTitle(transactionDate)));
            }

            timelineBox.getChildren().add(createTimelineRow(transaction));
        }

        if (transactions.isEmpty()) {
            timelineBox.getChildren().add(new Label("No recent transactions found."));
        }
    }

    private HBox createTimelineRow(Transaction transaction) {
        String direction = isCredit(transaction) ? "+" : "-";
        Label iconLabel = new Label(isCredit(transaction) ? "IN" : "OUT");
        iconLabel.setMinWidth(42);
        iconLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

        VBox detailBox = new VBox(3);
        Label descriptionLabel = new Label(transaction.getDescription());
        descriptionLabel.setWrapText(true);
        Label timeLabel = new Label(transaction.getTransactionDateTime().format(dateTimeFormatter)
                + " | Balance: PKR " + String.format("%.2f", transaction.getBalanceAfterTransaction()));
        detailBox.getChildren().addAll(descriptionLabel, timeLabel);

        Label amountLabel = new Label(direction + " PKR " + String.format("%.2f", transaction.getAmount()));
        amountLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

        HBox row = UiFactory.hbox(12, javafx.geometry.Pos.CENTER_LEFT);
        row.setPadding(new javafx.geometry.Insets(10));
        row.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                javafx.scene.paint.Color.WHITE, new javafx.scene.layout.CornerRadii(10), javafx.geometry.Insets.EMPTY)));
        row.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
                javafx.scene.paint.Color.rgb(219, 226, 235),
                javafx.scene.layout.BorderStrokeStyle.SOLID,
                new javafx.scene.layout.CornerRadii(10),
                new javafx.scene.layout.BorderWidths(1))));
        HBox.setHgrow(detailBox, javafx.scene.layout.Priority.ALWAYS);
        row.getChildren().addAll(iconLabel, detailBox, amountLabel);
        return row;
    }

    private boolean isCredit(Transaction transaction) {
        Customer customer = LoginSession.getLoggedInCustomer();
        return customer != null && customer.getAccountNumber().equals(transaction.getToAccountNumber());
    }

    private String groupTitle(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return "Today";
        }

        if (date.equals(today.minusDays(1))) {
            return "Yesterday";
        }

        return "Older - " + date;
    }

    private void handleExportStatement() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            AlertHelper.showError("Export Error", "No customer is logged in.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Mini Statement");
        fileChooser.setInitialFileName("mini_statement_" + customer.getAccountNumber() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            StatementExporter.exportStatement(file, customer, transactionTable.getItems(), "Mini Statement");
            AlertHelper.showInfo("Export Complete", "Mini statement exported successfully.");
        } catch (IOException exception) {
            AlertHelper.showError("Export Error", "Statement could not be exported.");
        }
    }

}
