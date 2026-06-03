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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class EmiCalculatorController {

    private TextField principalField;
    private TextField interestRateField;
    private TextField tenureMonthsField;
    private Label messageLabel;
    private Label monthlyEmiLabel;
    private Label totalPaymentLabel;
    private Label totalInterestLabel;
    private TableView<AmortizationRow> amortizationTable;
    private TableColumn<AmortizationRow, Integer> monthColumn;
    private TableColumn<AmortizationRow, Double> principalPaidColumn;
    private TableColumn<AmortizationRow, Double> interestPaidColumn;
    private TableColumn<AmortizationRow, Double> remainingBalanceColumn;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("EMI Calculator"),
                new Label("Calculate monthly installment, total payment, total interest, and amortization schedule.")
        );

        GridPane formGrid = UiFactory.grid(14, 12);
        principalField = UiFactory.textField("e.g. 1000000");
        interestRateField = UiFactory.textField("e.g. 12");
        tenureMonthsField = UiFactory.textField("e.g. 60");

        formGrid.add(new Label("Principal Amount"), 0, 0);
        UiFactory.addGrowing(formGrid, principalField, 1, 0);
        formGrid.add(new Label("Annual Interest Rate (%)"), 2, 0);
        UiFactory.addGrowing(formGrid, interestRateField, 3, 0);
        formGrid.add(new Label("Tenure (Months)"), 0, 1);
        UiFactory.addGrowing(formGrid, tenureMonthsField, 1, 1);

        Button calculateButton = UiFactory.button("Calculate EMI");
        calculateButton.setOnAction(event -> handleCalculateEmi());
        Button clearButton = UiFactory.button("Clear");
        clearButton.setOnAction(event -> handleClearCalculator());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(calculateButton, clearButton);
        formGrid.add(actions, 3, 1);

        messageLabel = UiFactory.messageLabel();

        monthlyEmiLabel = UiFactory.value("PKR 0.00", 18);
        totalPaymentLabel = UiFactory.value("PKR 0.00", 18);
        totalInterestLabel = UiFactory.value("PKR 0.00", 18);

        TilePane summaryPane = new TilePane();
        summaryPane.setHgap(14);
        summaryPane.setVgap(14);
        summaryPane.setPrefColumns(3);
        summaryPane.setPrefTileWidth(220);
        summaryPane.setPrefTileHeight(88);
        summaryPane.getChildren().addAll(
                summaryBox("Monthly EMI", monthlyEmiLabel),
                summaryBox("Total Payment", totalPaymentLabel),
                summaryBox("Total Interest", totalInterestLabel)
        );

        monthColumn = UiFactory.column("Month", 120);
        principalPaidColumn = UiFactory.column("Principal Paid", 190);
        interestPaidColumn = UiFactory.column("Interest Paid", 190);
        remainingBalanceColumn = UiFactory.column("Remaining Balance", 220);
        amortizationTable = UiFactory.table(360, monthColumn, principalPaidColumn,
                interestPaidColumn, remainingBalanceColumn);

        page.getChildren().addAll(formGrid, messageLabel, summaryPane, amortizationTable);

        initialize();
        return UiFactory.scroll(page);
    }

    private VBox summaryBox(String title, Label valueLabel) {
        VBox box = new VBox(6);
        box.getChildren().addAll(new Label(title), valueLabel);
        return box;
    }

    private void initialize() {
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("monthNumber"));
        principalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("principalPaid"));
        interestPaidColumn.setCellValueFactory(new PropertyValueFactory<>("interestPaid"));
        remainingBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("remainingBalance"));

        TableColumnFormatters.currency(principalPaidColumn);
        TableColumnFormatters.currency(interestPaidColumn);
        TableColumnFormatters.currency(remainingBalanceColumn);
    }

    private void handleCalculateEmi() {
        String principalText = principalField.getText().trim();
        String rateText = interestRateField.getText().trim();
        String tenureText = tenureMonthsField.getText().trim();

        if (principalText.isEmpty() || rateText.isEmpty() || tenureText.isEmpty()) {
            showMessage("Please fill all EMI calculator fields.", true);
            return;
        }

        double principalAmount;
        double annualInterestRate;
        int tenureMonths;

        try {
            principalAmount = Double.parseDouble(principalText);
            annualInterestRate = Double.parseDouble(rateText);
            tenureMonths = Integer.parseInt(tenureText);
        } catch (NumberFormatException exception) {
            showMessage("Principal, interest rate, and tenure must be numeric.", true);
            return;
        }

        if (principalAmount <= 0 || annualInterestRate < 0 || tenureMonths <= 0) {
            showMessage("Principal and tenure must be positive. Interest rate cannot be negative.", true);
            return;
        }

        double monthlyEmi = EmiCalculationUtil.calculateMonthlyInstallment(principalAmount, annualInterestRate, tenureMonths);
        double totalPayment = monthlyEmi * tenureMonths;
        double totalInterest = totalPayment - principalAmount;
        ArrayList<AmortizationRow> schedule = EmiCalculationUtil.generateAmortizationSchedule(principalAmount,
                annualInterestRate, tenureMonths);

        monthlyEmiLabel.setText("PKR " + String.format("%.2f", monthlyEmi));
        totalPaymentLabel.setText("PKR " + String.format("%.2f", totalPayment));
        totalInterestLabel.setText("PKR " + String.format("%.2f", totalInterest));
        amortizationTable.setItems(FXCollections.observableArrayList(schedule));
        showMessage("EMI calculated successfully.", false);
    }

    private void handleClearCalculator() {
        principalField.clear();
        interestRateField.clear();
        tenureMonthsField.clear();
        monthlyEmiLabel.setText("PKR 0.00");
        totalPaymentLabel.setText("PKR 0.00");
        totalInterestLabel.setText("PKR 0.00");
        amortizationTable.getItems().clear();
        showMessage("", false);
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
    }

}
