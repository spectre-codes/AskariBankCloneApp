package com.askaribank;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

final class ReceiptHelper {

    private static final File RECEIPT_DIRECTORY = new File("data/receipts");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ReceiptHelper() {
    }

    static File saveReceipt(String filePrefix, String title, List<String> lines) throws IOException {
        if (!RECEIPT_DIRECTORY.exists()) {
            RECEIPT_DIRECTORY.mkdirs();
        }

        File receiptFile = new File(RECEIPT_DIRECTORY, filePrefix + "_" + System.currentTimeMillis() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile))) {
            writer.write(BankDataStore.appName);
            writer.newLine();
            writer.write(title);
            writer.newLine();
            writer.write("------------------------------");
            writer.newLine();
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return receiptFile;
    }

    static void showReceipt(Transaction transaction, String serviceName) {
        ArrayList<String> lines = buildReceiptLines(transaction, serviceName);
        VBox receiptBox = createReceiptBox(lines);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Digital Receipt");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        Button saveTextButton = UiFactory.button("Save as Text");
        saveTextButton.setOnAction(event -> saveReceiptAsText(receiptBox, lines));
        VBox content = new VBox(12, receiptBox, new javafx.scene.layout.HBox(10, saveTextButton));
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private static ArrayList<String> buildReceiptLines(Transaction transaction, String serviceName) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Askari Digital");
        lines.add("Transaction Successful");
        lines.add("Transaction ID: " + transaction.getTransactionId());
        lines.add("Date/Time: " + transaction.getTransactionDateTime().format(DATE_TIME_FORMATTER));
        lines.add("Type: " + transaction.getTransactionType().name().replace("_", " "));
        lines.add("From: " + mask(transaction.getFromAccountNumber()));
        lines.add("To/Service: " + safe(transaction.getToAccountNumber(), serviceName));
        lines.add("Amount: PKR " + String.format("%.2f", transaction.getAmount()));
        lines.add("Description: " + safe(transaction.getDescription(), "-"));
        lines.add("Status: Successful");
        return lines;
    }

    private static VBox createReceiptBox(List<String> lines) {
        VBox receiptBox = new VBox(8);
        receiptBox.setAlignment(Pos.CENTER_LEFT);
        receiptBox.setPadding(new Insets(18));
        receiptBox.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                javafx.scene.paint.Color.WHITE, new javafx.scene.layout.CornerRadii(12), Insets.EMPTY)));
        receiptBox.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
                javafx.scene.paint.Color.rgb(219, 226, 235),
                javafx.scene.layout.BorderStrokeStyle.SOLID,
                new javafx.scene.layout.CornerRadii(12),
                new javafx.scene.layout.BorderWidths(1))));

        for (int index = 0; index < lines.size(); index++) {
            Label label = new Label(lines.get(index));
            label.setWrapText(true);
            if (index < 2) {
                label.setFont(Font.font("System", FontWeight.BOLD, index == 0 ? 18 : 15));
            }
            receiptBox.getChildren().add(label);
        }
        return receiptBox;
    }

    private static void saveReceiptAsText(Node owner, List<String> lines) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt");
        fileChooser.setInitialFileName("receipt_" + System.currentTimeMillis() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(owner.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            AlertHelper.showInfo("Receipt", "Receipt saved successfully.");
        } catch (IOException exception) {
            AlertHelper.showError("Receipt", "Receipt could not be saved.");
        }
    }

    private static String mask(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return "XXXXXX" + value.substring(Math.max(0, value.length() - 4));
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
