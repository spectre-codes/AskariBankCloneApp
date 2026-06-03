package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerProfileController {

    private Label nameLabel;
    private Label accountNumberLabel;
    private Label cnicLabel;
    private TextField mobileNumberField;
    private TextField emailField;
    private TextField addressField;
    private PasswordField currentPinField;
    private PasswordField newPinField;
    private PasswordField confirmPinField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Profile"),
                new Label("View profile details and update contact information or transaction PIN.")
        );

        nameLabel = new Label("-");
        accountNumberLabel = new Label("-");
        cnicLabel = new Label("-");
        mobileNumberField = UiFactory.textField("Mobile number");
        emailField = UiFactory.textField("Email address");
        addressField = UiFactory.textField("Address");

        GridPane profileGrid = UiFactory.grid(14, 12);
        profileGrid.setMaxWidth(620);
        profileGrid.add(new Label("Customer Name"), 0, 0);
        profileGrid.add(nameLabel, 1, 0);
        profileGrid.add(new Label("Account Number"), 0, 1);
        profileGrid.add(accountNumberLabel, 1, 1);
        profileGrid.add(new Label("CNIC"), 0, 2);
        profileGrid.add(cnicLabel, 1, 2);
        profileGrid.add(new Label("Mobile Number"), 0, 3);
        UiFactory.addGrowing(profileGrid, mobileNumberField, 1, 3);
        profileGrid.add(new Label("Email"), 0, 4);
        UiFactory.addGrowing(profileGrid, emailField, 1, 4);
        profileGrid.add(new Label("Address"), 0, 5);
        UiFactory.addGrowing(profileGrid, addressField, 1, 5);

        Button saveProfileButton = UiFactory.button("Save Profile");
        saveProfileButton.setOnAction(event -> handleSaveProfile());
        profileGrid.add(saveProfileButton, 1, 6);

        currentPinField = UiFactory.passwordField("Current PIN");
        newPinField = UiFactory.passwordField("New 4-digit PIN");
        confirmPinField = UiFactory.passwordField("Confirm new PIN");

        GridPane pinGrid = UiFactory.grid(14, 12);
        pinGrid.setMaxWidth(620);
        pinGrid.add(new Label("Current Transaction PIN"), 0, 0);
        UiFactory.addGrowing(pinGrid, currentPinField, 1, 0);
        pinGrid.add(new Label("New Transaction PIN"), 0, 1);
        UiFactory.addGrowing(pinGrid, newPinField, 1, 1);
        pinGrid.add(new Label("Confirm New PIN"), 0, 2);
        UiFactory.addGrowing(pinGrid, confirmPinField, 1, 2);

        Button changePinButton = UiFactory.button("Change PIN");
        changePinButton.setOnAction(event -> handleChangePin());
        pinGrid.add(changePinButton, 1, 3);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(profileGrid, UiFactory.separator(), UiFactory.sectionTitle("Transaction PIN"),
                pinGrid, messageLabel);
        loadProfile();
        return UiFactory.scroll(page);
    }

    private void loadProfile() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            return;
        }

        nameLabel.setText(customer.getCustomerFullName());
        accountNumberLabel.setText(maskAccountNumber(customer.getAccountNumber()));
        cnicLabel.setText(maskCnic(customer.getCnicNumber()));
        mobileNumberField.setText(customer.getPhoneNumber());
        emailField.setText(customer.getEmail());
        addressField.setText(customer.getAddress());
    }

    private void handleSaveProfile() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String mobileNumber = mobileNumberField.getText().trim();
        if (mobileNumber.isEmpty() || !mobileNumber.matches("\\d{10,12}")) {
            showError("Mobile number must contain 10 to 12 digits.");
            return;
        }

        Customer customerWithPhone = BankDataStore.findCustomerByPhoneNumber(mobileNumber);
        if (customerWithPhone != null && !customerWithPhone.getAccountNumber().equals(customer.getAccountNumber())) {
            showError("This mobile number is already used by another customer.");
            return;
        }

        customer.setPhoneNumber(mobileNumber);
        customer.setEmail(emailField.getText());
        customer.setAddress(addressField.getText());
        if (customer.isRaastLinked()) {
            customer.setRaastId(mobileNumber);
        }
        FileDataStore.saveAll();
        AuditLogger.log("PROFILE_UPDATED", customer.getAccountNumber());
        showInfo("Profile updated successfully.");
    }

    private void handleChangePin() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showError("No customer is logged in.");
            return;
        }

        String currentPin = currentPinField.getText().trim();
        String newPin = newPinField.getText().trim();
        String confirmPin = confirmPinField.getText().trim();

        if (!currentPin.equals(customer.getTransactionPin())) {
            showError("Current transaction PIN is incorrect.");
            return;
        }

        if (!newPin.matches("\\d{4}")) {
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
        NotificationService.addNotification(customer.getAccountNumber(), "Your transaction PIN was changed.");
        currentPinField.clear();
        newPinField.clear();
        confirmPinField.clear();
        showInfo("Transaction PIN changed successfully.");
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "XXXXXX";
        }
        return "XXXXXX" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
    }

    private String maskCnic(String cnic) {
        if (cnic == null || cnic.length() < 4) {
            return "XXXXXXXXXXXXX";
        }
        return "XXXXXXXXX" + cnic.substring(cnic.length() - 4);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Profile", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Profile", message);
    }
}
