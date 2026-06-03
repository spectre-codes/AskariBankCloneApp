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

public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService = new BeneficiaryService();

    private TextField nameField;
    private TextField accountNumberField;
    private TextField mobileNumberField;
    private TextField bankNameField;
    private Label messageLabel;
    private TableView<Beneficiary> beneficiaryTable;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Beneficiaries"),
                new Label("Save frequently used Askari account or mobile/Raast payees.")
        );

        nameField = UiFactory.textField("e.g. Sana Khan");
        accountNumberField = UiFactory.textField("ASK-2026-00002");
        mobileNumberField = UiFactory.textField("03007654321");
        bankNameField = UiFactory.textField(BankDataStore.bankName);

        GridPane formGrid = UiFactory.grid(14, 12);
        formGrid.setMaxWidth(720);
        formGrid.add(new Label("Beneficiary Name"), 0, 0);
        UiFactory.addGrowing(formGrid, nameField, 1, 0);
        formGrid.add(new Label("Account Number"), 0, 1);
        UiFactory.addGrowing(formGrid, accountNumberField, 1, 1);
        formGrid.add(new Label("Mobile Number"), 0, 2);
        UiFactory.addGrowing(formGrid, mobileNumberField, 1, 2);
        formGrid.add(new Label("Bank Name"), 0, 3);
        UiFactory.addGrowing(formGrid, bankNameField, 1, 3);

        Button addButton = UiFactory.button("Add Beneficiary");
        addButton.setOnAction(event -> handleAddBeneficiary());
        Button clearButton = UiFactory.button("Clear");
        clearButton.setOnAction(event -> clearForm());
        HBox actions = UiFactory.hbox(10);
        actions.getChildren().addAll(addButton, clearButton);
        formGrid.add(actions, 1, 4);

        messageLabel = UiFactory.messageLabel();

        TableColumn<Beneficiary, String> idColumn = UiFactory.column("ID", 95);
        TableColumn<Beneficiary, String> nameColumn = UiFactory.column("Name", 170);
        TableColumn<Beneficiary, String> accountColumn = UiFactory.column("Account", 160);
        TableColumn<Beneficiary, String> mobileColumn = UiFactory.column("Mobile", 140);
        TableColumn<Beneficiary, String> bankColumn = UiFactory.column("Bank", 150);
        beneficiaryTable = UiFactory.table(320, idColumn, nameColumn, accountColumn, mobileColumn, bankColumn);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryName"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryAccountNumber"));
        mobileColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryMobileNumber"));
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankName"));

        Button deleteButton = UiFactory.button("Delete Selected");
        deleteButton.setOnAction(event -> handleDeleteBeneficiary());

        page.getChildren().addAll(
                formGrid,
                messageLabel,
                UiFactory.sectionTitle("Saved Payees"),
                deleteButton,
                beneficiaryTable
        );

        refreshTable();
        return UiFactory.scroll(page);
    }

    private void handleAddBeneficiary() {
        Customer owner = LoginSession.getLoggedInCustomer();
        try {
            beneficiaryService.addBeneficiary(owner, nameField.getText(), accountNumberField.getText(),
                    mobileNumberField.getText(), bankNameField.getText());
            clearForm();
            refreshTable();
            showMessage("Beneficiary saved successfully.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void handleDeleteBeneficiary() {
        try {
            beneficiaryService.deleteBeneficiary(beneficiaryTable.getSelectionModel().getSelectedItem());
            refreshTable();
            showMessage("Beneficiary deleted.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTable() {
        Customer owner = LoginSession.getLoggedInCustomer();
        String ownerAccountNumber = owner == null ? "" : owner.getAccountNumber();
        beneficiaryTable.setItems(FXCollections.observableArrayList(
                beneficiaryService.getBeneficiariesForOwner(ownerAccountNumber)));
    }

    private void clearForm() {
        nameField.clear();
        accountNumberField.clear();
        mobileNumberField.clear();
        bankNameField.setText(BankDataStore.bankName);
        messageLabel.setText("");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        AlertHelper.showError("Beneficiary Error", message);
    }
}
