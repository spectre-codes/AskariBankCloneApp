package com.askaribank;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class TableColumnFormatters {

    private TableColumnFormatters() {
    }

    static <S> void currency(TableColumn<S, Double> column) {
        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : String.format("PKR %.2f", amount));
            }
        });
    }

    static <S> void dateTime(TableColumn<S, LocalDateTime> column, DateTimeFormatter formatter) {
        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                setText(empty || dateTime == null ? null : dateTime.format(formatter));
            }
        });
    }

    static <S> void transactionType(TableColumn<S, TransactionType> column) {
        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(TransactionType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.name().replace("_", " "));
            }
        });
    }

    static <S> void accountNumberOrDash(TableColumn<S, String> column) {
        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(String accountNumber, boolean empty) {
                super.updateItem(accountNumber, empty);
                setText(empty || accountNumber == null || accountNumber.isEmpty() ? "-" : accountNumber);
            }
        });
    }
}
