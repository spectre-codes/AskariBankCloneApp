package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class StaffChequeRequestsController {

    private final ChequeBookRequestService requestService = new ChequeBookRequestService();

    private TableView<ChequeBookRequest> requestTable;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Cheque Requests"),
                new Label("Review cheque book requests and update branch processing status.")
        );

        TableColumn<ChequeBookRequest, String> idColumn = UiFactory.column("Request ID", 120);
        TableColumn<ChequeBookRequest, String> accountColumn = UiFactory.column("Account", 150);
        TableColumn<ChequeBookRequest, String> nameColumn = UiFactory.column("Customer", 170);
        TableColumn<ChequeBookRequest, Integer> leavesColumn = UiFactory.column("Leaves", 90);
        TableColumn<ChequeBookRequest, LocalDate> dateColumn = UiFactory.column("Date", 130);
        TableColumn<ChequeBookRequest, String> statusColumn = UiFactory.column("Status", 120);
        requestTable = UiFactory.table(420, idColumn, accountColumn, nameColumn, leavesColumn, dateColumn, statusColumn);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        leavesColumn.setCellValueFactory(new PropertyValueFactory<>("leaves"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        Button dispatchButton = UiFactory.button("Mark Dispatched");
        dispatchButton.setOnAction(event -> handleDispatch());
        Button rejectButton = UiFactory.button("Reject");
        rejectButton.setOnAction(event -> handleReject());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(dispatchButton, rejectButton);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(actions, messageLabel, requestTable);
        refreshTable();
        return page;
    }

    private void handleDispatch() {
        try {
            requestService.markDispatched(requestTable.getSelectionModel().getSelectedItem());
            refreshTable();
            messageLabel.setText("Cheque request marked as dispatched.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void handleReject() {
        try {
            requestService.reject(requestTable.getSelectionModel().getSelectedItem());
            refreshTable();
            messageLabel.setText("Cheque request rejected.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTable() {
        requestTable.setItems(FXCollections.observableArrayList(BankDataStore.allChequeBookRequests));
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Cheque Requests", message);
    }
}
