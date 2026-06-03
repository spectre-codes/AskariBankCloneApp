package com.askaribank;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SupportTicketController {

    private ComboBox<String> categoryCombo;
    private TextField subjectField;
    private TextArea messageArea;
    private Label messageLabel;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.getChildren().addAll(
                UiFactory.title("Support / Complaints"),
                new Label("Submit a simple support ticket for branch review.")
        );

        categoryCombo = UiFactory.comboBox("Select category");
        categoryCombo.setItems(FXCollections.observableArrayList(
                "Transaction Issue", "Card Issue", "Login Issue", "Other"));
        subjectField = UiFactory.textField("Subject");
        messageArea = new TextArea();
        messageArea.setPromptText("Write your complaint or support request");
        messageArea.setPrefHeight(140);
        messageArea.setWrapText(true);

        GridPane grid = UiFactory.grid(14, 12);
        grid.setMaxWidth(620);
        grid.add(new Label("Category"), 0, 0);
        UiFactory.addGrowing(grid, categoryCombo, 1, 0);
        grid.add(new Label("Subject"), 0, 1);
        UiFactory.addGrowing(grid, subjectField, 1, 1);
        grid.add(new Label("Message"), 0, 2);
        UiFactory.addGrowing(grid, messageArea, 1, 2);

        Button submitButton = UiFactory.button("Submit Ticket");
        submitButton.setOnAction(event -> handleSubmitTicket());
        grid.add(submitButton, 1, 3);

        messageLabel = UiFactory.messageLabel();
        page.getChildren().addAll(grid, messageLabel);
        return page;
    }

    private void handleSubmitTicket() {
        try {
            String ticketId = SupportTicketService.submitTicket(LoginSession.getLoggedInCustomer(),
                    categoryCombo.getValue(), subjectField.getText(), messageArea.getText());
            clearForm();
            messageLabel.setText("Support ticket submitted. Ref: " + ticketId);
            AlertHelper.showInfo("Support", "Support ticket submitted. Ref: " + ticketId);
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
            AlertHelper.showError("Support", exception.getMessage());
        }
    }

    private void clearForm() {
        categoryCombo.getSelectionModel().clearSelection();
        subjectField.clear();
        messageArea.clear();
    }
}
