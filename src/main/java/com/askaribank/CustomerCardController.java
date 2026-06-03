package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerCardController {

    private BankCard card;

    private Label cardNumberLabel;
    private Label cardTypeLabel;
    private Label cardStatusLabel;
    private TextField newPinField;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("My Cards"),
                new Label("View card status, block or unblock card, and change ATM PIN.")
        );

        cardNumberLabel = new Label("-");
        cardTypeLabel = new Label("-");
        cardStatusLabel = new Label("-");
        GridPane detailsGrid = UiFactory.grid(18, 12);
        detailsGrid.add(new Label("Card Number"), 0, 0);
        detailsGrid.add(cardNumberLabel, 1, 0);
        detailsGrid.add(new Label("Card Type"), 0, 1);
        detailsGrid.add(cardTypeLabel, 1, 1);
        detailsGrid.add(new Label("Card Status"), 0, 2);
        detailsGrid.add(cardStatusLabel, 1, 2);

        Button blockButton = UiFactory.button("Block Card");
        blockButton.setOnAction(event -> handleBlockCard());
        Button unblockButton = UiFactory.button("Unblock Card");
        unblockButton.setOnAction(event -> handleUnblockCard());
        HBox cardActions = UiFactory.hbox(10);
        cardActions.getChildren().addAll(blockButton, unblockButton);

        newPinField = UiFactory.textField("4 digits");
        GridPane pinGrid = UiFactory.grid(14, 12);
        pinGrid.setMaxWidth(420);
        pinGrid.add(new Label("New PIN"), 0, 0);
        UiFactory.addGrowing(pinGrid, newPinField, 1, 0);
        Button changePinButton = UiFactory.button("Change PIN");
        changePinButton.setOnAction(event -> handleChangePin());
        pinGrid.add(changePinButton, 1, 1);

        messageLabel = UiFactory.messageLabel();

        page.getChildren().addAll(
                detailsGrid,
                cardActions,
                UiFactory.separator(),
                pinGrid,
                messageLabel
        );

        initialize();
        return page;
    }

    private void initialize() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            messageLabel.setText("No customer is logged in.");
            return;
        }

        card = BankDataStore.findCardByAccountNumber(customer.getAccountNumber());
        if (card == null) {
            card = BankDataStore.createDefaultCardForCustomer(customer);
            FileDataStore.saveAll();
        }
        refreshCardDetails();
    }

    private void handleBlockCard() {
        if (card == null) {
            showError("Card record was not found.");
            return;
        }

        card.setStatus(CardStatus.BLOCKED);
        saveCardUpdate("CARD_BLOCKED", "Card blocked for " + card.getAccountNumber());
        NotificationService.addNotification(card.getAccountNumber(), "Your card was blocked.");
        refreshCardDetails();
        ToastManager.show(messageLabel, "Card blocked");
        showInfo("Card blocked successfully.");
    }

    private void handleUnblockCard() {
        if (card == null) {
            showError("Card record was not found.");
            return;
        }

        card.setStatus(CardStatus.ACTIVE);
        saveCardUpdate("CARD_UNBLOCKED", "Card unblocked for " + card.getAccountNumber());
        NotificationService.addNotification(card.getAccountNumber(), "Your card was unblocked.");
        refreshCardDetails();
        ToastManager.show(messageLabel, "Card unblocked");
        showInfo("Card unblocked successfully.");
    }

    private void handleChangePin() {
        if (card == null) {
            showError("Card record was not found.");
            return;
        }

        String newPin = newPinField.getText().trim();
        if (!newPin.matches("\\d{4}")) {
            showError("PIN must be exactly 4 digits.");
            return;
        }

        card.setPin(newPin);
        Customer customer = BankDataStore.findCustomerByAccountNumber(card.getAccountNumber());
        if (customer != null) {
            customer.setAtmPin(newPin);
        }
        FileDataStore.saveAll();
        AuditLogger.log("CARD_PIN_CHANGED", card.getAccountNumber());
        NotificationService.addNotification(card.getAccountNumber(), "Your card PIN was changed.");
        newPinField.clear();
        ToastManager.show(messageLabel, "PIN changed");
        showInfo("PIN changed successfully.");
    }

    private void refreshCardDetails() {
        if (card == null) {
            return;
        }
        cardNumberLabel.setText(card.getMaskedCardNumber());
        cardTypeLabel.setText(card.getCardType());
        cardStatusLabel.setText(card.getStatus().toString());
        messageLabel.setText("");
    }

    private void saveCardUpdate(String action, String detail) {
        FileDataStore.saveAll();
        AuditLogger.log(action, detail);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Card Error", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Card Management", message);
    }
}
