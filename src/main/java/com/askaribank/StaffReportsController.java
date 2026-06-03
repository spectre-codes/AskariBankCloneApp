package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class StaffReportsController {

    private Label customersLabel;
    private Label balanceLabel;
    private Label transactionsLabel;
    private Label approvedLoansLabel;
    private Label pendingLoansLabel;
    private Label blockedCardsLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Reports"),
                new Label("Simple banking summary for staff review.")
        );

        customersLabel = new Label("0");
        balanceLabel = new Label("PKR 0.00");
        transactionsLabel = new Label("0");
        approvedLoansLabel = new Label("0");
        pendingLoansLabel = new Label("0");
        blockedCardsLabel = new Label("0");

        GridPane gridPane = UiFactory.grid(24, 14);
        gridPane.add(new Label("Total Customers"), 0, 0);
        gridPane.add(customersLabel, 1, 0);
        gridPane.add(new Label("Total Bank Balance"), 0, 1);
        gridPane.add(balanceLabel, 1, 1);
        gridPane.add(new Label("Total Transactions"), 0, 2);
        gridPane.add(transactionsLabel, 1, 2);
        gridPane.add(new Label("Approved Loans"), 0, 3);
        gridPane.add(approvedLoansLabel, 1, 3);
        gridPane.add(new Label("Pending Loans"), 0, 4);
        gridPane.add(pendingLoansLabel, 1, 4);
        gridPane.add(new Label("Blocked Cards"), 0, 5);
        gridPane.add(blockedCardsLabel, 1, 5);

        page.getChildren().add(gridPane);

        initialize();
        return page;
    }

    private void initialize() {
        customersLabel.setText(String.valueOf(BankDataStore.allCustomers.size()));
        balanceLabel.setText(String.format("PKR %,.2f", BankDataStore.getTotalBankBalance()));
        transactionsLabel.setText(String.valueOf(BankDataStore.getTotalTransactions()));
        approvedLoansLabel.setText(String.valueOf(BankDataStore.getApprovedLoanCount()));
        pendingLoansLabel.setText(String.valueOf(BankDataStore.getPendingLoanCount()));
        blockedCardsLabel.setText(String.valueOf(BankDataStore.getBlockedCardCount()));
    }
}
