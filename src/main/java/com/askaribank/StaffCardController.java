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

public class StaffCardController {

    private BankCard selectedCard;

    private TextField accountNumberField;
    private Label customerNameLabel;
    private Label cardNumberLabel;
    private Label cardTypeLabel;
    private Label cardStatusLabel;
    private Label messageLabel;
    private TableView<BankCard> cardTable;
    private TableColumn<BankCard, String> accountColumn;
    private TableColumn<BankCard, String> cardNumberColumn;
    private TableColumn<BankCard, String> cardTypeColumn;
    private TableColumn<BankCard, CardStatus> statusColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Card Management"),
                new Label("Search a customer card by account number and block or unblock it.")
        );

        accountNumberField = UiFactory.textField("ASK-2026-00001");
        accountNumberField.setPrefWidth(220);
        Button searchButton = UiFactory.button("Search Card");
        searchButton.setOnAction(event -> handleSearchCard());
        Button blockButton = UiFactory.button("Block");
        blockButton.setOnAction(event -> handleBlockCard());
        Button unblockButton = UiFactory.button("Unblock");
        unblockButton.setOnAction(event -> handleUnblockCard());

        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(accountNumberField, searchButton, blockButton, unblockButton);

        customerNameLabel = new Label("-");
        cardNumberLabel = new Label("-");
        cardTypeLabel = new Label("-");
        cardStatusLabel = new Label("-");
        GridPane detailsGrid = UiFactory.grid(18, 12);
        detailsGrid.add(new Label("Customer Name"), 0, 0);
        detailsGrid.add(customerNameLabel, 1, 0);
        detailsGrid.add(new Label("Card Number"), 0, 1);
        detailsGrid.add(cardNumberLabel, 1, 1);
        detailsGrid.add(new Label("Card Type"), 2, 1);
        detailsGrid.add(cardTypeLabel, 3, 1);
        detailsGrid.add(new Label("Status"), 0, 2);
        detailsGrid.add(cardStatusLabel, 1, 2);

        messageLabel = UiFactory.messageLabel();

        accountColumn = UiFactory.column("Account Number", 170);
        cardNumberColumn = UiFactory.column("Masked Card", 180);
        cardTypeColumn = UiFactory.column("Card Type", 140);
        statusColumn = UiFactory.column("Status", 120);
        cardTable = UiFactory.table(300, accountColumn, cardNumberColumn, cardTypeColumn, statusColumn);

        page.getChildren().addAll(
                actions,
                detailsGrid,
                messageLabel,
                UiFactory.separator(),
                UiFactory.sectionTitle("All Cards"),
                cardTable
        );

        initialize();
        return page;
    }

    private void initialize() {
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        cardNumberColumn.setCellValueFactory(new PropertyValueFactory<>("maskedCardNumber"));
        cardTypeColumn.setCellValueFactory(new PropertyValueFactory<>("cardType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refreshTable();
        clearDetails();
    }

    private void handleSearchCard() {
        String accountNumber = accountNumberField.getText().trim();
        Customer customer = BankDataStore.findCustomerByAccountNumber(accountNumber);
        selectedCard = BankDataStore.findCardByAccountNumber(accountNumber);

        if (customer == null || selectedCard == null) {
            clearDetails();
            showError("Card was not found for this account number.");
            return;
        }

        customerNameLabel.setText(customer.getCustomerFullName());
        cardNumberLabel.setText(selectedCard.getMaskedCardNumber());
        cardTypeLabel.setText(selectedCard.getCardType());
        cardStatusLabel.setText(selectedCard.getStatus().toString());
        messageLabel.setText("Card loaded successfully.");
    }

    private void handleBlockCard() {
        updateCardStatus(CardStatus.BLOCKED);
    }

    private void handleUnblockCard() {
        updateCardStatus(CardStatus.ACTIVE);
    }

    private void updateCardStatus(CardStatus status) {
        if (selectedCard == null) {
            showError("Please search and load a card first.");
            return;
        }

        selectedCard.setStatus(status);
        FileDataStore.saveAll();
        AuditLogger.log(status == CardStatus.BLOCKED ? "CARD_BLOCKED" : "CARD_UNBLOCKED",
                selectedCard.getAccountNumber());
        NotificationService.addNotification(selectedCard.getAccountNumber(),
                status == CardStatus.BLOCKED ? "Your card was blocked by branch staff."
                        : "Your card was unblocked by branch staff.");
        refreshTable();
        handleSearchCard();
        showInfo("Card status updated successfully.");
    }

    private void refreshTable() {
        cardTable.setItems(FXCollections.observableArrayList(BankDataStore.allCards));
    }

    private void clearDetails() {
        customerNameLabel.setText("-");
        cardNumberLabel.setText("-");
        cardTypeLabel.setText("-");
        cardStatusLabel.setText("-");
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
