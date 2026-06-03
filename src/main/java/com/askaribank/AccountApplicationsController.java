package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountApplicationsController {

    private final AccountApplicationService applicationService = new AccountApplicationService();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TableView<AccountApplication> applicationsTable;
    private Label messageLabel;
    private TextField remarksField;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Account Applications"),
                new Label("Review online account application requests. Approval here does not create an active account automatically.")
        );

        TableColumn<AccountApplication, String> idColumn = UiFactory.column("Application ID", 120);
        TableColumn<AccountApplication, String> nameColumn = UiFactory.column("Full Name", 160);
        TableColumn<AccountApplication, String> cnicColumn = UiFactory.column("CNIC", 130);
        TableColumn<AccountApplication, String> mobileColumn = UiFactory.column("Mobile", 125);
        TableColumn<AccountApplication, String> emailColumn = UiFactory.column("Email", 190);
        TableColumn<AccountApplication, String> cityColumn = UiFactory.column("City", 110);
        TableColumn<AccountApplication, String> accountTypeColumn = UiFactory.column("Account Type", 120);
        TableColumn<AccountApplication, String> statusColumn = UiFactory.column("Status", 105);
        TableColumn<AccountApplication, LocalDateTime> submittedColumn = UiFactory.column("Submitted", 145);
        TableColumn<AccountApplication, String> remarksColumn = UiFactory.column("Remarks", 220);
        applicationsTable = UiFactory.table(380, idColumn, nameColumn, cnicColumn, mobileColumn,
                emailColumn, cityColumn, accountTypeColumn, statusColumn, submittedColumn, remarksColumn);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        cnicColumn.setCellValueFactory(new PropertyValueFactory<>("cnic"));
        mobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        submittedColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        remarksColumn.setCellValueFactory(new PropertyValueFactory<>("staffRemarks"));
        TableColumnFormatters.dateTime(submittedColumn, dateTimeFormatter);

        remarksField = UiFactory.textField("Optional staff remarks");
        remarksField.setPrefWidth(360);
        Button approveButton = UiFactory.button("Approve Request");
        approveButton.setOnAction(event -> handleApprove());
        Button rejectButton = UiFactory.button("Reject Request");
        rejectButton.setOnAction(event -> handleReject());
        Button refreshButton = UiFactory.button("Refresh");
        refreshButton.setOnAction(event -> refreshTable());

        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(new Label("Remarks"), remarksField, approveButton, rejectButton, refreshButton);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(
                actions,
                messageLabel,
                applicationsTable,
                new Label("After approval, create the real demo account from Manage Customers when staff verification is complete.")
        );

        refreshTable();
        return UiFactory.scroll(page);
    }

    private void handleApprove() {
        AccountApplication application = getSelectedApplication();
        if (application == null) {
            return;
        }

        try {
            applicationService.approve(application, remarksField.getText());
            refreshTable();
            remarksField.clear();
            showInfo("Application approved for staff verification. No active bank account was created.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void handleReject() {
        AccountApplication application = getSelectedApplication();
        if (application == null) {
            return;
        }

        try {
            applicationService.reject(application, remarksField.getText());
            refreshTable();
            remarksField.clear();
            showInfo("Application rejected.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private AccountApplication getSelectedApplication() {
        AccountApplication application = applicationsTable.getSelectionModel().getSelectedItem();
        if (application == null) {
            showError("Please select an application request.");
        }
        return application;
    }

    private void refreshTable() {
        applicationsTable.setItems(FXCollections.observableArrayList(BankDataStore.allAccountApplications));
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Account Applications", message);
    }

    private void showInfo(String message) {
        messageLabel.setText(message);
        AlertHelper.showInfo("Account Applications", message);
    }
}
