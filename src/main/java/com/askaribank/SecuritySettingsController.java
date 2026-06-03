package com.askaribank;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SecuritySettingsController {

    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color SOFT_BORDER = Color.rgb(219, 226, 235);
    private static final CornerRadii CARD_RADIUS = new CornerRadii(14);

    private final Runnable backAction;

    private Label messageLabel;
    private Label cardStatusLabel;
    private ComboBox<String> balancePrivacyCombo;
    private PasswordField currentTransactionPinField;
    private PasswordField newTransactionPinField;
    private PasswordField confirmTransactionPinField;
    private PasswordField currentLoginPinField;
    private PasswordField newLoginPinField;
    private PasswordField confirmLoginPinField;

    public SecuritySettingsController(Runnable backAction) {
        this.backAction = backAction;
    }

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                createHeader(),
                buildSecurityOverviewSection(),
                buildTransactionPinSection(),
                buildLoginPinSection(),
                buildPrivacySection(),
                buildSessionSection(),
                buildSecurityActivitySection(),
                buildCardSecuritySection(),
                buildSecurityTipsSection()
        );

        messageLabel = UiFactory.messageLabel();
        page.getChildren().add(messageLabel);
        return UiFactory.scroll(page);
    }

    private HBox createHeader() {
        HBox header = UiFactory.hbox(12, Pos.CENTER_LEFT);
        Button backButton = UiFactory.button("Back");
        backButton.setOnAction(event -> backAction.run());

        VBox titleBox = new VBox(3);
        titleBox.getChildren().addAll(
                UiFactory.title("Security Settings"),
                new Label("Manage your account security")
        );
        header.getChildren().addAll(backButton, titleBox);
        return header;
    }

    private VBox buildSecurityOverviewSection() {
        Customer customer = LoginSession.getLoggedInCustomer();
        BankCard card = customer == null ? null : BankDataStore.findCardByAccountNumber(customer.getAccountNumber());

        GridPane grid = UiFactory.grid(14, 10);
        addDetail(grid, "Customer Name", customer == null ? "-" : customer.getCustomerFullName(), 0);
        addDetail(grid, "Account Number", customer == null ? "-" : maskAccountNumber(customer.getAccountNumber()), 1);
        addDetail(grid, "Account Status", customer == null ? "-" : customer.getAccountStatus(), 2);
        addDetail(grid, "Last Login", findLastLogin(customer), 3);
        addDetail(grid, "Transaction PIN", customer != null && hasFourDigitPin(customer.getTransactionPin()) ? "Set" : "Not Set", 4);
        addDetail(grid, "Card Status", card == null ? "No card" : card.getStatus().toString(), 5);

        return cardSection("Security Overview", grid);
    }

    private VBox buildTransactionPinSection() {
        currentTransactionPinField = UiFactory.passwordField("Current Transaction PIN");
        newTransactionPinField = UiFactory.passwordField("New 4-digit PIN");
        confirmTransactionPinField = UiFactory.passwordField("Confirm new PIN");

        GridPane grid = UiFactory.grid(14, 10);
        grid.add(new Label("Current PIN"), 0, 0);
        UiFactory.addGrowing(grid, currentTransactionPinField, 1, 0);
        grid.add(new Label("New PIN"), 0, 1);
        UiFactory.addGrowing(grid, newTransactionPinField, 1, 1);
        grid.add(new Label("Confirm PIN"), 0, 2);
        UiFactory.addGrowing(grid, confirmTransactionPinField, 1, 2);

        Button changeButton = UiFactory.button("Change Transaction PIN");
        changeButton.setOnAction(event -> handleChangeTransactionPin());
        grid.add(changeButton, 1, 3);
        return cardSection("Change Transaction PIN", grid);
    }

    private VBox buildLoginPinSection() {
        currentLoginPinField = UiFactory.passwordField("Current Login PIN");
        newLoginPinField = UiFactory.passwordField("New 4-digit Login PIN");
        confirmLoginPinField = UiFactory.passwordField("Confirm new Login PIN");

        GridPane grid = UiFactory.grid(14, 10);
        grid.add(new Label("Current Login PIN"), 0, 0);
        UiFactory.addGrowing(grid, currentLoginPinField, 1, 0);
        grid.add(new Label("New Login PIN"), 0, 1);
        UiFactory.addGrowing(grid, newLoginPinField, 1, 1);
        grid.add(new Label("Confirm Login PIN"), 0, 2);
        UiFactory.addGrowing(grid, confirmLoginPinField, 1, 2);

        Button changeButton = UiFactory.button("Change Login PIN");
        changeButton.setOnAction(event -> handleChangeLoginPin());
        grid.add(changeButton, 1, 3);
        return cardSection("Change Login PIN", grid);
    }

    private VBox buildPrivacySection() {
        Customer customer = LoginSession.getLoggedInCustomer();
        balancePrivacyCombo = UiFactory.comboBox("Select balance privacy");
        balancePrivacyCombo.setItems(FXCollections.observableArrayList("ON - Hide balance by default",
                "OFF - Show balance by default"));
        balancePrivacyCombo.getSelectionModel().select(customer == null || customer.isHideBalanceByDefault() ? 0 : 1);

        Button saveButton = UiFactory.button("Save Privacy Setting");
        saveButton.setOnAction(event -> handleSavePrivacySetting());

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Hide balance by default"), balancePrivacyCombo, saveButton);
        return cardSection("Balance Privacy Setting", content);
    }

    private VBox buildSessionSection() {
        VBox content = new VBox(8);
        content.getChildren().addAll(
                new Label("Auto logout is enabled for your security."),
                new Label("Current timeout: 3 minutes of inactivity."),
                new Label("Timeout changing is kept read-only here to avoid breaking the active session timer.")
        );
        return cardSection("Session Timeout Setting", content);
    }

    private VBox buildSecurityActivitySection() {
        ListView<String> activityList = new ListView<>();
        activityList.setPrefHeight(180);
        activityList.setItems(FXCollections.observableArrayList(readSecurityActivity()));
        return cardSection("Recent Security Activity", activityList);
    }

    private VBox buildCardSecuritySection() {
        Customer customer = LoginSession.getLoggedInCustomer();
        BankCard card = customer == null ? null : BankDataStore.findCardByAccountNumber(customer.getAccountNumber());

        GridPane grid = UiFactory.grid(14, 10);
        addDetail(grid, "Card Number", card == null ? "-" : card.getMaskedCardNumber(), 0);
        cardStatusLabel = new Label(card == null ? "No card" : card.getStatus().toString());
        grid.add(new Label("Card Status"), 0, 1);
        grid.add(cardStatusLabel, 1, 1);

        Button blockButton = UiFactory.button("Block Card");
        blockButton.setOnAction(event -> handleUpdateCardStatus(CardStatus.BLOCKED));
        Button unblockButton = UiFactory.button("Unblock Card");
        unblockButton.setOnAction(event -> handleUpdateCardStatus(CardStatus.ACTIVE));
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(blockButton, unblockButton);
        grid.add(actions, 1, 2);

        return cardSection("Card Security Shortcut", grid);
    }

    private VBox buildSecurityTipsSection() {
        VBox tips = new VBox(6);
        tips.getChildren().addAll(
                new Label("Never share your PIN with anyone."),
                new Label("Change your transaction PIN regularly."),
                new Label("Do not use easy PINs like 0000 or 1234."),
                new Label("Always logout after using the app."),
                new Label("Contact branch staff if you notice suspicious activity.")
        );
        return cardSection("Security Tips", tips);
    }

    private void handleChangeTransactionPin() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String currentPin = currentTransactionPinField.getText().trim();
        String newPin = newTransactionPinField.getText().trim();
        String confirmPin = confirmTransactionPinField.getText().trim();

        if (!currentPin.equals(customer.getTransactionPin())) {
            showError("Current transaction PIN is incorrect.");
            return;
        }

        if (!validateFourDigitPin(newPin)) {
            showError("New transaction PIN must be exactly 4 digits.");
            return;
        }

        if (!newPin.equals(confirmPin)) {
            showError("New PIN and confirmation do not match.");
            return;
        }

        customer.setTransactionPin(newPin);
        FileDataStore.saveAll();
        AuditLogger.log("TRANSACTION_PIN_CHANGED", customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Your transaction PIN was changed successfully");
        clearTransactionPinFields();
        showInfo("Transaction PIN changed successfully.");
    }

    private void handleChangeLoginPin() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String currentPin = currentLoginPinField.getText().trim();
        String newPin = newLoginPinField.getText().trim();
        String confirmPin = confirmLoginPinField.getText().trim();

        if (!currentPin.equals(customer.getAtmPin())) {
            showError("Current login PIN is incorrect.");
            return;
        }

        if (!validateFourDigitPin(newPin)) {
            showError("New login PIN must be exactly 4 digits.");
            return;
        }

        if (!newPin.equals(confirmPin)) {
            showError("New login PIN and confirmation do not match.");
            return;
        }

        customer.setAtmPin(newPin);
        BankCard card = BankDataStore.findCardByAccountNumber(customer.getAccountNumber());
        if (card != null) {
            card.setPin(newPin);
        }
        FileDataStore.saveAll();
        AuditLogger.log("LOGIN_PASSWORD_CHANGED", customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Your login PIN was changed successfully");
        clearLoginPinFields();
        showInfo("Login PIN changed successfully.");
    }

    private void handleSavePrivacySetting() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        customer.setHideBalanceByDefault(balancePrivacyCombo.getSelectionModel().getSelectedIndex() == 0);
        FileDataStore.saveAll();
        AuditLogger.log("BALANCE_PRIVACY_UPDATED", customer.getAccountNumber());
        showInfo("Balance privacy setting saved.");
    }

    private void handleUpdateCardStatus(CardStatus status) {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        BankCard card = BankDataStore.findCardByAccountNumber(customer.getAccountNumber());
        if (card == null) {
            showError("Card record was not found.");
            return;
        }

        card.setStatus(status);
        FileDataStore.saveAll();
        AuditLogger.log(status == CardStatus.BLOCKED ? "CARD_BLOCKED" : "CARD_UNBLOCKED",
                customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(),
                status == CardStatus.BLOCKED ? "Your card was blocked." : "Your card was unblocked.");
        cardStatusLabel.setText(card.getStatus().toString());
        showInfo("Card status updated successfully.");
    }

    private VBox cardSection(String title, javafx.scene.Node content) {
        VBox section = new VBox(12);
        section.setPadding(new Insets(16));
        section.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CARD_RADIUS, Insets.EMPTY)));
        section.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));
        section.getChildren().addAll(UiFactory.sectionTitle(title), content);
        return section;
    }

    private void addDetail(GridPane grid, String title, String value, int row) {
        Label titleLabel = new Label(title);
        Label valueLabel = new Label(value == null || value.isBlank() ? "-" : value);
        valueLabel.setWrapText(true);
        grid.add(titleLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }

    private ArrayList<String> readSecurityActivity() {
        ArrayList<String> securityEvents = new ArrayList<>();
        List<String> keywords = List.of("LOGIN_SUCCESS", "LOGIN_FAILED", "TRANSACTION_PIN_CHANGED",
                "LOGIN_PASSWORD_CHANGED", "CARD_BLOCKED", "CARD_UNBLOCKED", "ACCOUNT_LOCKED",
                "ACCOUNT_UNLOCKED", "ACCOUNT_FROZEN", "ACCOUNT_UNFROZEN");

        ArrayList<String> logs = AuditLogger.readRecentLogs();
        for (int index = logs.size() - 1; index >= 0 && securityEvents.size() < 8; index--) {
            String logLine = logs.get(index);
            for (String keyword : keywords) {
                if (logLine.contains(keyword)) {
                    securityEvents.add(logLine);
                    break;
                }
            }
        }

        if (securityEvents.isEmpty()) {
            securityEvents.add("No recent security activity found.");
        }
        return securityEvents;
    }

    private String findLastLogin(Customer customer) {
        if (customer == null) {
            return "-";
        }

        ArrayList<String> logs = AuditLogger.readRecentLogs();
        for (int index = logs.size() - 1; index >= 0; index--) {
            String logLine = logs.get(index);
            if (logLine.contains("LOGIN_SUCCESS") && logLine.contains(customer.getAccountNumber())) {
                return logLine.split("\\|")[0].trim();
            }
        }
        return "Current session";
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "XXXXXX";
        }
        return "XXXXXX" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
    }

    private boolean validateFourDigitPin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    private boolean hasFourDigitPin(String pin) {
        return validateFourDigitPin(pin);
    }

    private void clearTransactionPinFields() {
        currentTransactionPinField.clear();
        newTransactionPinField.clear();
        confirmTransactionPinField.clear();
    }

    private void clearLoginPinFields() {
        currentLoginPinField.clear();
        newLoginPinField.clear();
        confirmLoginPinField.clear();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Security Settings", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Security Settings", message);
    }
}
