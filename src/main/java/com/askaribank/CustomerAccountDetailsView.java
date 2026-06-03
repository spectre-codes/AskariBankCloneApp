package com.askaribank;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

final class CustomerAccountDetailsView {

    private static final Color CARD_BACKGROUND = Color.rgb(255, 255, 255);
    private static final Color SOFT_BORDER = Color.rgb(219, 226, 235);
    private static final CornerRadii CARD_RADIUS = new CornerRadii(18);

    private CustomerAccountDetailsView() {
    }

    static Parent create() {
        Customer customer = LoginSession.getLoggedInCustomer();
        VBox page = UiFactory.page();
        page.setMaxWidth(560);
        page.getChildren().addAll(
                UiFactory.title("Account Details"),
                new Label("Your Askari Bank account profile and current account status.")
        );

        if (customer == null) {
            page.getChildren().add(new Label("No customer is logged in."));
            return UiFactory.scroll(page);
        }

        page.getChildren().add(createDetailsGrid(customer));
        return UiFactory.scroll(page);
    }

    private static GridPane createDetailsGrid(Customer customer) {
        BankCard card = BankDataStore.findCardByAccountNumber(customer.getAccountNumber());
        GridPane detailsGrid = UiFactory.grid(18, 12);
        detailsGrid.setPadding(new Insets(20));
        detailsGrid.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CARD_RADIUS, Insets.EMPTY)));
        detailsGrid.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));

        addDetail(detailsGrid, "Customer Name", customer.getCustomerFullName(), 0);
        addDetail(detailsGrid, "Account Number", maskAccountNumber(customer.getAccountNumber()), 1);
        addDetail(detailsGrid, "CNIC", maskCnic(customer.getCnicNumber()), 2);
        addDetail(detailsGrid, "Phone", customer.getPhoneNumber(), 3);
        addDetail(detailsGrid, "Account Type", customer.getAccountType(), 4);
        addDetail(detailsGrid, "Current Balance", String.format("PKR %,.2f", customer.getAccountBalance()), 5);
        addDetail(detailsGrid, "Card Status", card == null ? "No card" : card.getStatus().toString(), 6);
        addDetail(detailsGrid, "Account Status", customer.getAccountStatus(), 7);
        addDetail(detailsGrid, "Date Opened", String.valueOf(customer.getDateOpened()), 8);
        return detailsGrid;
    }

    private static void addDetail(GridPane gridPane, String title, String value, int row) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        Label valueLabel = new Label(value == null || value.isBlank() ? "-" : value);
        valueLabel.setWrapText(true);
        gridPane.add(titleLabel, 0, row);
        gridPane.add(valueLabel, 1, row);
    }

    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "XXXXXX";
        }
        return "XXXXXX" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
    }

    private static String maskCnic(String cnic) {
        if (cnic == null || cnic.length() < 4) {
            return "XXXXXXXXXXXXX";
        }
        return "XXXXXXXXX" + cnic.substring(cnic.length() - 4);
    }
}
