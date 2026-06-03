package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationsController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TableView<Notification> notificationTable;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Notifications"),
                new Label("Recent account alerts and banking updates.")
        );

        TableColumn<Notification, String> idColumn = UiFactory.column("ID", 105);
        TableColumn<Notification, LocalDateTime> timeColumn = UiFactory.column("Date/Time", 150);
        TableColumn<Notification, String> messageColumn = UiFactory.column("Message", 520);
        TableColumn<Notification, Boolean> readColumn = UiFactory.column("Read", 80);
        notificationTable = UiFactory.table(420, idColumn, timeColumn, messageColumn, readColumn);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("notificationId"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        readColumn.setCellValueFactory(new PropertyValueFactory<>("read"));
        TableColumnFormatters.dateTime(timeColumn, formatter);

        Button markReadButton = UiFactory.button("Mark All Read");
        markReadButton.setOnAction(event -> handleMarkAllRead());
        messageLabel = UiFactory.messageLabel();

        page.getChildren().addAll(markReadButton, messageLabel, notificationTable);
        refreshTable();
        return page;
    }

    private void handleMarkAllRead() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            messageLabel.setText("No customer is logged in.");
            return;
        }

        NotificationService.markAllRead(customer.getAccountNumber());
        refreshTable();
        messageLabel.setText("Notifications marked as read.");
    }

    private void refreshTable() {
        Customer customer = LoginSession.getLoggedInCustomer();
        String accountNumber = customer == null ? "" : customer.getAccountNumber();
        notificationTable.setItems(FXCollections.observableArrayList(
                NotificationService.getNotificationsForAccount(accountNumber)));
    }
}
