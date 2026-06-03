package com.askaribank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class StatementExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private StatementExporter() {
    }

    public static void exportStatement(File file, Customer customer, List<Transaction> transactions,
                                       String title) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(BankDataStore.appName);
            writer.newLine();
            writer.write(title);
            writer.newLine();
            writer.write("Exported: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.newLine();

            if (customer != null) {
                writer.write("Customer Name: " + customer.getCustomerFullName());
                writer.newLine();
                writer.write("Account Number: " + customer.getAccountNumber());
                writer.newLine();
                writer.write("Account Type: " + customer.getAccountType());
                writer.newLine();
                writer.write("Current Balance: PKR " + String.format("%.2f", customer.getAccountBalance()));
                writer.newLine();
                writer.newLine();
            }

            writer.write("Transaction ID,Date/Time,Type,From Account,To Account,Amount,Description,Balance After");
            writer.newLine();
            for (Transaction transaction : transactions) {
                writer.write(csv(transaction.getTransactionId()) + ","
                        + csv(transaction.getTransactionDateTime().format(FORMATTER)) + ","
                        + transaction.getTransactionType() + ","
                        + csv(transaction.getFromAccountNumber()) + ","
                        + csv(transaction.getToAccountNumber()) + ","
                        + transaction.getAmount() + ","
                        + csv(transaction.getDescription()) + ","
                        + transaction.getBalanceAfterTransaction());
                writer.newLine();
            }
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }

        String cleaned = value.replace("\"", "\"\"");
        if (cleaned.contains(",") || cleaned.contains("\n") || cleaned.contains("\r")) {
            return "\"" + cleaned + "\"";
        }
        return cleaned;
    }
}
