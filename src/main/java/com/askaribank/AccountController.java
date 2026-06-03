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

import java.time.LocalDate;
import java.util.function.Consumer;

public class AccountController {

    private final TransactionService transactionService = new TransactionService();

    private TextField customerNameField;
    private TextField cnicField;
    private TextField phoneField;
    private TextField initialDepositField;
    private TextField searchField;
    private ComboBox<String> accountTypeCombo;
    private ComboBox<String> cardTypeCombo;
    private TextField atmPinField;
    private Label messageLabel;
    private TableView<Customer> customerTable;
    private TableColumn<Customer, String> nameColumn;
    private TableColumn<Customer, String> cnicColumn;
    private TableColumn<Customer, String> phoneColumn;
    private TableColumn<Customer, String> accountColumn;
    private TableColumn<Customer, String> accountTypeColumn;
    private TableColumn<Customer, String> cardTypeColumn;
    private TableColumn<Customer, Double> balanceColumn;
    private TableColumn<Customer, String> statusColumn;
    private TableColumn<Customer, Integer> failedAttemptsColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Manage Customers"),
                new Label("Create customer accounts with account type, card type, opening deposit, and ATM PIN.")
        );

        GridPane formGrid = UiFactory.grid(14, 12);
        customerNameField = UiFactory.textField("e.g. Ali Khan");
        cnicField = UiFactory.textField("e.g. 3520212345671");
        phoneField = UiFactory.textField("e.g. 03001234567");
        initialDepositField = UiFactory.textField("e.g. 25000");
        accountTypeCombo = UiFactory.comboBox("Select account type");
        cardTypeCombo = UiFactory.comboBox("Select card type");
        atmPinField = UiFactory.textField("4 digits");

        formGrid.add(new Label("Customer Full Name"), 0, 0);
        UiFactory.addGrowing(formGrid, customerNameField, 1, 0);
        formGrid.add(new Label("CNIC Number"), 0, 1);
        UiFactory.addGrowing(formGrid, cnicField, 1, 1);
        formGrid.add(new Label("Phone Number"), 0, 2);
        UiFactory.addGrowing(formGrid, phoneField, 1, 2);
        formGrid.add(new Label("Initial Deposit"), 0, 3);
        UiFactory.addGrowing(formGrid, initialDepositField, 1, 3);
        formGrid.add(new Label("Account Type"), 2, 0);
        UiFactory.addGrowing(formGrid, accountTypeCombo, 3, 0);
        formGrid.add(new Label("Card Type"), 2, 1);
        UiFactory.addGrowing(formGrid, cardTypeCombo, 3, 1);
        formGrid.add(new Label("ATM PIN"), 2, 2);
        UiFactory.addGrowing(formGrid, atmPinField, 3, 2);

        Button createButton = UiFactory.button("Create Account");
        createButton.setOnAction(event -> handleCreateAccount());
        Button clearButton = UiFactory.button("Clear");
        clearButton.setOnAction(event -> handleClearForm());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(createButton, clearButton);
        formGrid.add(actions, 3, 3);

        messageLabel = UiFactory.messageLabel();

        searchField = UiFactory.textField("Search name, CNIC, account, or phone");
        searchField.setPrefWidth(340);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshCustomerTable());

        Button freezeButton = UiFactory.button("Freeze Account");
        freezeButton.setOnAction(event -> handleFreezeSelectedCustomer());
        Button unfreezeButton = UiFactory.button("Unfreeze Account");
        unfreezeButton.setOnAction(event -> handleUnfreezeSelectedCustomer());
        Button unlockButton = UiFactory.button("Unlock Account");
        unlockButton.setOnAction(event -> handleUnlockSelectedCustomer());
        Button resetAttemptsButton = UiFactory.button("Reset Failed Attempts");
        resetAttemptsButton.setOnAction(event -> handleResetFailedAttempts());

        HBox customerActions = UiFactory.hbox(10);
        customerActions.getChildren().addAll(
                new Label("Search"),
                searchField,
                freezeButton,
                unfreezeButton,
                unlockButton,
                resetAttemptsButton
        );

        nameColumn = UiFactory.column("Customer Name", 170);
        cnicColumn = UiFactory.column("CNIC", 130);
        phoneColumn = UiFactory.column("Phone", 125);
        accountColumn = UiFactory.column("Account No.", 150);
        accountTypeColumn = UiFactory.column("Account Type", 110);
        cardTypeColumn = UiFactory.column("Card", 110);
        balanceColumn = UiFactory.column("Balance", 130);
        statusColumn = UiFactory.column("Status", 120);
        failedAttemptsColumn = UiFactory.column("Failed Attempts", 115);
        customerTable = UiFactory.table(320, nameColumn, cnicColumn, phoneColumn, accountColumn,
                accountTypeColumn, cardTypeColumn, balanceColumn, statusColumn, failedAttemptsColumn);

        page.getChildren().addAll(
                formGrid,
                messageLabel,
                UiFactory.sectionTitle("All Customers"),
                customerActions,
                customerTable
        );

        initialize();
        return UiFactory.scroll(page);
    }

    private void initialize() {
        accountTypeCombo.setItems(FXCollections.observableArrayList("Current", "Savings"));
        cardTypeCombo.setItems(FXCollections.observableArrayList("Visa", "Mastercard"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerFullName"));
        cnicColumn.setCellValueFactory(new PropertyValueFactory<>("cnicNumber"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        cardTypeColumn.setCellValueFactory(new PropertyValueFactory<>("cardType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("accountBalance"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        failedAttemptsColumn.setCellValueFactory(new PropertyValueFactory<>("failedLoginAttempts"));
        TableColumnFormatters.currency(balanceColumn);

        refreshCustomerTable();
    }

    private void handleCreateAccount() {
        String fullName = customerNameField.getText().trim();
        String cnic = cnicField.getText().trim();
        String phone = phoneField.getText().trim();
        String depositText = initialDepositField.getText().trim();
        String accountType = accountTypeCombo.getValue();
        String cardType = cardTypeCombo.getValue();
        String atmPin = atmPinField.getText().trim();

        if (fullName.isEmpty() || cnic.isEmpty() || phone.isEmpty() || depositText.isEmpty()
                || accountType == null || cardType == null || atmPin.isEmpty()) {
            showMessage("All fields are required.", true);
            return;
        }

        if (cnic.length() < 13) {
            showMessage("CNIC number is too short. Please enter a valid CNIC.", true);
            return;
        }

        if (!phone.matches("\\d+")) {
            showMessage("Phone number must contain numeric digits only.", true);
            return;
        }

        if (!atmPin.matches("\\d{4}")) {
            showMessage("ATM PIN must be exactly 4 numeric digits.", true);
            return;
        }

        if (BankDataStore.findCustomerByCnic(cnic) != null) {
            showMessage("A customer with this CNIC already exists.", true);
            return;
        }

        if (BankDataStore.findCustomerByPhoneNumber(phone) != null) {
            showMessage("A customer with this phone number already exists.", true);
            return;
        }

        double initialDeposit;
        try {
            initialDeposit = Double.parseDouble(depositText);
        } catch (NumberFormatException exception) {
            showMessage("Initial deposit must be a valid number.", true);
            return;
        }

        if (initialDeposit <= 0) {
            showMessage("Initial deposit must be greater than zero.", true);
            return;
        }

        String accountNumber = BankDataStore.generateAccountNumber();
        Customer customer = new Customer(fullName, cnic, phone, accountNumber, 0,
                accountType, cardType, atmPin, LocalDate.now());

        BankDataStore.allCustomers.add(customer);
        BankCard card = BankDataStore.createDefaultCardForCustomer(customer);

        try {
            transactionService.deposit(accountNumber, initialDeposit, "Initial account opening deposit");
        } catch (AccountNotFoundException | IllegalArgumentException exception) {
            BankDataStore.allCustomers.remove(customer);
            BankDataStore.allCards.remove(card);
            showMessage(exception.getMessage(), true);
            return;
        }

        AuditLogger.log("ACCOUNT_CREATED", "Account created for " + fullName + " - " + accountNumber);
        FileDataStore.saveAll();
        refreshCustomerTable();
        clearForm();
        showMessage("Account created successfully. Account number: " + accountNumber, false);
    }

    private void handleClearForm() {
        clearForm();
        showMessage("", false);
    }

    private void refreshCustomerTable() {
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            customerTable.setItems(FXCollections.observableArrayList(BankDataStore.allCustomers));
            return;
        }

        customerTable.setItems(FXCollections.observableArrayList(BankDataStore.allCustomers.stream()
                .filter(customer -> matchesSearch(customer, query))
                .toList()));
    }

    private void clearForm() {
        customerNameField.clear();
        cnicField.clear();
        phoneField.clear();
        initialDepositField.clear();
        accountTypeCombo.getSelectionModel().clearSelection();
        cardTypeCombo.getSelectionModel().clearSelection();
        atmPinField.clear();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
    }

    private boolean matchesSearch(Customer customer, String query) {
        return contains(customer.getCustomerFullName(), query)
                || contains(customer.getCnicNumber(), query)
                || contains(customer.getAccountNumber(), query)
                || contains(customer.getPhoneNumber(), query);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private Customer getSelectedCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showMessage("Please select a customer from the table.", true);
        }
        return selectedCustomer;
    }

    private void handleFreezeSelectedCustomer() {
        updateSelectedCustomer(customer -> customer.setFrozen(true),
                "ACCOUNT_FROZEN",
                "Your account is temporarily restricted. Please contact branch staff.",
                "Account frozen: ");
    }

    private void handleUnfreezeSelectedCustomer() {
        updateSelectedCustomer(customer -> customer.setFrozen(false),
                "ACCOUNT_UNFROZEN",
                "Your account restriction has been removed.",
                "Account unfrozen: ");
    }

    private void handleUnlockSelectedCustomer() {
        updateSelectedCustomer(customer -> {
            customer.setLocked(false);
            customer.setFailedLoginAttempts(0);
        }, "ACCOUNT_UNLOCKED",
                "Your account has been unlocked by branch staff.",
                "Account unlocked: ");
    }

    private void handleResetFailedAttempts() {
        updateSelectedCustomer(customer -> customer.setFailedLoginAttempts(0),
                "FAILED_ATTEMPTS_RESET",
                null,
                "Failed login attempts reset for ");
    }

    private void updateSelectedCustomer(Consumer<Customer> update, String auditAction,
                                        String notificationMessage, String successMessagePrefix) {
        Customer selectedCustomer = getSelectedCustomer();
        if (selectedCustomer == null) {
            return;
        }

        update.accept(selectedCustomer);
        FileDataStore.saveAll();
        AuditLogger.log(auditAction, selectedCustomer.getAccountNumber());
        if (notificationMessage != null) {
            NotificationService.addNotification(selectedCustomer.getAccountNumber(), notificationMessage);
        }
        refreshCustomerTable();
        showMessage(successMessagePrefix + selectedCustomer.getAccountNumber(), false);
    }

}
