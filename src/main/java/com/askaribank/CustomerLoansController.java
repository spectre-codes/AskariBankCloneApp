package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class CustomerLoansController {

    private Label accountNumberLabel;
    private TableView<LoanApplication> loanTable;
    private TableColumn<LoanApplication, String> loanIdColumn;
    private TableColumn<LoanApplication, String> loanTypeColumn;
    private TableColumn<LoanApplication, Double> loanAmountColumn;
    private TableColumn<LoanApplication, Integer> tenureColumn;
    private TableColumn<LoanApplication, String> statusColumn;
    private TableColumn<LoanApplication, Double> emiColumn;
    private TableColumn<LoanApplication, String> reasonColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("My Loans"),
                new Label("Loan applications linked with your account.")
        );

        accountNumberLabel = new Label("-");
        GridPane detailsGrid = UiFactory.grid(18, 10);
        detailsGrid.add(new Label("Account Number"), 0, 0);
        detailsGrid.add(accountNumberLabel, 1, 0);

        loanIdColumn = UiFactory.column("Loan ID", 110);
        loanTypeColumn = UiFactory.column("Loan Type", 150);
        loanAmountColumn = UiFactory.column("Amount", 150);
        tenureColumn = UiFactory.column("Tenure Years", 120);
        statusColumn = UiFactory.column("Status", 120);
        emiColumn = UiFactory.column("Monthly EMI", 150);
        reasonColumn = UiFactory.column("Rejection Reason", 320);
        loanTable = UiFactory.table(360, loanIdColumn, loanTypeColumn, loanAmountColumn,
                tenureColumn, statusColumn, emiColumn, reasonColumn);

        page.getChildren().addAll(detailsGrid, loanTable);

        initialize();
        return page;
    }

    private void initialize() {
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
        loanAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        tenureColumn.setCellValueFactory(new PropertyValueFactory<>("tenureYears"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        emiColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyInstallment"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("rejectionReason"));

        TableColumnFormatters.currency(loanAmountColumn);
        TableColumnFormatters.currency(emiColumn);
        loadCustomerLoans();
    }

    private void loadCustomerLoans() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            accountNumberLabel.setText("-");
            loanTable.setItems(FXCollections.observableArrayList());
            return;
        }

        accountNumberLabel.setText(customer.getAccountNumber());
        ArrayList<LoanApplication> customerLoans = new ArrayList<>();
        for (LoanApplication application : BankDataStore.allLoanApplications) {
            if (customer.getAccountNumber().equals(application.getLinkedAccountNumber())) {
                customerLoans.add(application);
            }
        }
        loanTable.setItems(FXCollections.observableArrayList(customerLoans));
    }

}
