package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ChequeBookRequestController {

    private final ChequeBookRequestService requestService = new ChequeBookRequestService();

    private ComboBox<Integer> leavesCombo;
    private Label messageLabel;
    private TableView<ChequeBookRequest> requestTable;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Cheque Requests"),
                new Label("Request a cheque book and track branch processing status.")
        );

        leavesCombo = UiFactory.comboBox("Select leaves");
        leavesCombo.setItems(FXCollections.observableArrayList(25, 50, 100));

        GridPane formGrid = UiFactory.grid(14, 12);
        formGrid.setMaxWidth(420);
        formGrid.add(new Label("Cheque Leaves"), 0, 0);
        UiFactory.addGrowing(formGrid, leavesCombo, 1, 0);
        Button submitButton = UiFactory.button("Submit Request");
        submitButton.setOnAction(event -> handleSubmitRequest());
        formGrid.add(submitButton, 1, 1);

        messageLabel = UiFactory.messageLabel();

        TableColumn<ChequeBookRequest, String> idColumn = UiFactory.column("Request ID", 120);
        TableColumn<ChequeBookRequest, Integer> leavesColumn = UiFactory.column("Leaves", 90);
        TableColumn<ChequeBookRequest, LocalDate> dateColumn = UiFactory.column("Date", 130);
        TableColumn<ChequeBookRequest, String> statusColumn = UiFactory.column("Status", 120);
        requestTable = UiFactory.table(320, idColumn, leavesColumn, dateColumn, statusColumn);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        leavesColumn.setCellValueFactory(new PropertyValueFactory<>("leaves"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        page.getChildren().addAll(formGrid, messageLabel, UiFactory.sectionTitle("My Requests"), requestTable);
        refreshTable();
        return page;
    }

    private void handleSubmitRequest() {
        Customer customer = LoginSession.getLoggedInCustomer();
        Integer leaves = leavesCombo.getValue();
        if (leaves == null) {
            showError("Please select cheque leaves.");
            return;
        }

        try {
            ChequeBookRequest request = requestService.submitRequest(customer, leaves);
            leavesCombo.getSelectionModel().clearSelection();
            refreshTable();
            messageLabel.setText("Cheque book request submitted. Ref: " + request.getRequestId());
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTable() {
        Customer customer = LoginSession.getLoggedInCustomer();
        String accountNumber = customer == null ? "" : customer.getAccountNumber();
        requestTable.setItems(FXCollections.observableArrayList(requestService.getRequestsForAccount(accountNumber)));
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Cheque Request Error", message);
    }
}
