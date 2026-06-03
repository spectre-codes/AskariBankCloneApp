package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoanApplicationController {

    private ComboBox<Customer> customerCombo;
    private ComboBox<String> loanTypeCombo;
    private TextField loanAmountField;
    private TextField tenureYearsField;
    private TextField monthlySalaryField;
    private TextField creditScoreField;
    private Label messageLabel;
    private TableView<LoanApplication> loanTable;
    private TableColumn<LoanApplication, String> loanIdColumn;
    private TableColumn<LoanApplication, String> applicantColumn;
    private TableColumn<LoanApplication, String> linkedAccountColumn;
    private TableColumn<LoanApplication, String> loanTypeColumn;
    private TableColumn<LoanApplication, Double> loanAmountColumn;
    private TableColumn<LoanApplication, Integer> tenureColumn;
    private TableColumn<LoanApplication, Double> salaryColumn;
    private TableColumn<LoanApplication, Integer> scoreColumn;
    private TableColumn<LoanApplication, String> statusColumn;
    private TableColumn<LoanApplication, String> reasonColumn;
    private TableColumn<LoanApplication, Double> emiColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Loan Applications"),
                new Label("Select an existing customer account, choose a loan type, and process eligibility.")
        );

        GridPane formGrid = UiFactory.grid(14, 12);
        customerCombo = UiFactory.comboBox("Select customer account");
        loanTypeCombo = UiFactory.comboBox("Select loan type");
        loanAmountField = UiFactory.textField("e.g. 500000");
        tenureYearsField = UiFactory.textField("e.g. 5");
        monthlySalaryField = UiFactory.textField("e.g. 75000");
        creditScoreField = UiFactory.textField("300 to 850");

        formGrid.add(new Label("Customer Account"), 0, 0);
        UiFactory.addGrowing(formGrid, customerCombo, 1, 0);
        formGrid.add(new Label("Loan Type"), 2, 0);
        UiFactory.addGrowing(formGrid, loanTypeCombo, 3, 0);
        formGrid.add(new Label("Loan Amount"), 0, 1);
        UiFactory.addGrowing(formGrid, loanAmountField, 1, 1);
        formGrid.add(new Label("Tenure (Years)"), 2, 1);
        UiFactory.addGrowing(formGrid, tenureYearsField, 3, 1);
        formGrid.add(new Label("Monthly Salary"), 0, 2);
        UiFactory.addGrowing(formGrid, monthlySalaryField, 1, 2);
        formGrid.add(new Label("Credit Score"), 2, 2);
        UiFactory.addGrowing(formGrid, creditScoreField, 3, 2);

        Button submitButton = UiFactory.button("Submit Application");
        submitButton.setOnAction(event -> handleSubmitApplication());
        Button clearButton = UiFactory.button("Clear");
        clearButton.setOnAction(event -> handleClearForm());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(submitButton, clearButton);
        formGrid.add(actions, 3, 3);

        VBox rules = new VBox(4);
        rules.getChildren().addAll(
                UiFactory.sectionTitle("Loan Rules"),
                new Label("Personal Loan: Max PKR 500,000 | 18% | Min Salary PKR 30,000 | Min Score 600"),
                new Label("Home Loan: Max PKR 10,000,000 | 12% | Min Salary PKR 60,000 | Min Score 700"),
                new Label("Car Loan: Max PKR 3,000,000 | 15% | Min Salary PKR 45,000 | Min Score 650")
        );

        messageLabel = UiFactory.messageLabel();

        loanIdColumn = UiFactory.column("Loan ID", 100);
        applicantColumn = UiFactory.column("Applicant", 150);
        linkedAccountColumn = UiFactory.column("Account No.", 145);
        loanTypeColumn = UiFactory.column("Loan Type", 120);
        loanAmountColumn = UiFactory.column("Amount", 125);
        tenureColumn = UiFactory.column("Years", 90);
        salaryColumn = UiFactory.column("Salary", 120);
        scoreColumn = UiFactory.column("Score", 90);
        statusColumn = UiFactory.column("Status", 105);
        emiColumn = UiFactory.column("Monthly EMI", 130);
        reasonColumn = UiFactory.column("Rejection Reason", 260);
        loanTable = UiFactory.table(330, loanIdColumn, applicantColumn, linkedAccountColumn, loanTypeColumn,
                loanAmountColumn, tenureColumn, salaryColumn, scoreColumn, statusColumn, emiColumn, reasonColumn);

        page.getChildren().addAll(formGrid, rules, messageLabel, loanTable);

        initialize();
        return UiFactory.scroll(page);
    }

    private void initialize() {
        customerCombo.setItems(FXCollections.observableArrayList(BankDataStore.allCustomers));
        loanTypeCombo.setItems(FXCollections.observableArrayList("Personal Loan", "Home Loan", "Car Loan"));

        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        applicantColumn.setCellValueFactory(new PropertyValueFactory<>("applicantName"));
        linkedAccountColumn.setCellValueFactory(new PropertyValueFactory<>("linkedAccountNumber"));
        loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
        loanAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        tenureColumn.setCellValueFactory(new PropertyValueFactory<>("tenureYears"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("monthlySalary"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("creditScore"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("rejectionReason"));
        emiColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyInstallment"));

        TableColumnFormatters.currency(loanAmountColumn);
        TableColumnFormatters.currency(salaryColumn);
        TableColumnFormatters.currency(emiColumn);
        refreshLoanTable();
    }

    private void handleSubmitApplication() {
        Customer selectedCustomer = customerCombo.getValue();
        String loanType = loanTypeCombo.getValue();
        String amountText = loanAmountField.getText().trim();
        String tenureText = tenureYearsField.getText().trim();
        String salaryText = monthlySalaryField.getText().trim();
        String scoreText = creditScoreField.getText().trim();

        if (selectedCustomer == null) {
            showMessage("Account not found. Please select an existing customer account.", true);
            return;
        }

        if (loanType == null || amountText.isEmpty() || tenureText.isEmpty()
                || salaryText.isEmpty() || scoreText.isEmpty()) {
            showMessage("All loan application fields are required.", true);
            return;
        }

        double loanAmount;
        int tenureYears;
        double monthlySalary;
        int creditScore;

        try {
            loanAmount = Double.parseDouble(amountText);
            tenureYears = Integer.parseInt(tenureText);
            monthlySalary = Double.parseDouble(salaryText);
            creditScore = Integer.parseInt(scoreText);
        } catch (NumberFormatException exception) {
            showMessage("Please enter numeric values for amount, tenure, salary, and credit score.", true);
            return;
        }

        if (tenureYears <= 0) {
            showMessage("Tenure must be greater than zero years.", true);
            return;
        }

        if (monthlySalary <= 0) {
            showMessage("Monthly salary must be greater than zero.", true);
            return;
        }

        if (creditScore < 300 || creditScore > 850) {
            showMessage("Credit score must be between 300 and 850.", true);
            return;
        }

        Loan loan = createLoanByType(loanType, loanAmount, tenureYears * 12);
        LoanApplication application = new LoanApplication(selectedCustomer.getCustomerFullName(),
                selectedCustomer.getAccountNumber(), loan, monthlySalary, creditScore);

        try {
            application.processApplication();
        } catch (InvalidLoanAmountException | InsufficientSalaryException | AccountNotFoundException exception) {
            application.reject(exception.getMessage());
        }

        BankDataStore.allLoanApplications.add(application);
        AuditLogger.log("LOAN_APPLIED", application.getLoanId() + " for " + selectedCustomer.getAccountNumber());
        if ("APPROVED".equals(application.getStatus())) {
            AuditLogger.log("LOAN_APPROVED", application.getLoanId() + " for " + selectedCustomer.getAccountNumber());
            NotificationService.addNotification(selectedCustomer.getAccountNumber(),
                    "Loan " + application.getLoanId() + " was approved. Monthly EMI: PKR "
                            + String.format("%.2f", application.calculateMonthlyInstallment()));
        } else {
            AuditLogger.log("LOAN_REJECTED", application.getLoanId() + " for " + selectedCustomer.getAccountNumber());
            NotificationService.addNotification(selectedCustomer.getAccountNumber(),
                    "Loan " + application.getLoanId() + " was rejected: " + application.getRejectionReason());
        }
        FileDataStore.saveAll();
        refreshLoanTable();
        clearForm();

        if ("APPROVED".equals(application.getStatus())) {
            showMessage("Loan approved. Monthly EMI: PKR "
                    + String.format("%.2f", application.calculateMonthlyInstallment()), false);
        } else {
            showMessage("Loan rejected: " + application.getRejectionReason(), true);
        }
    }

    private void handleClearForm() {
        clearForm();
        showMessage("", false);
    }

    private Loan createLoanByType(String loanType, double loanAmount, int tenureMonths) {
        return switch (loanType) {
            case "Home Loan" -> new HomeLoan(loanAmount, tenureMonths);
            case "Car Loan" -> new CarLoan(loanAmount, tenureMonths);
            default -> new PersonalLoan(loanAmount, tenureMonths);
        };
    }

    private void refreshLoanTable() {
        loanTable.setItems(FXCollections.observableArrayList(BankDataStore.allLoanApplications));
    }

    private void clearForm() {
        customerCombo.getSelectionModel().clearSelection();
        loanTypeCombo.getSelectionModel().clearSelection();
        loanAmountField.clear();
        tenureYearsField.clear();
        monthlySalaryField.clear();
        creditScoreField.clear();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
    }

}
