package com.askaribank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

final class SupportTicketService {

    private static final File SUPPORT_FILE = new File("data/support_tickets.csv");

    private SupportTicketService() {
    }

    static String submitTicket(Customer customer, String category, String subject, String message) {
        if (customer == null) {
            throw new IllegalArgumentException("No customer is logged in.");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Please select a category.");
        }

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a subject.");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a message.");
        }

        if (!SUPPORT_FILE.getParentFile().exists()) {
            SUPPORT_FILE.getParentFile().mkdirs();
        }

        boolean writeHeader = !SUPPORT_FILE.exists() || SUPPORT_FILE.length() == 0;
        String ticketId = "SUP-" + System.currentTimeMillis();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPORT_FILE, true))) {
            if (writeHeader) {
                writer.write("ticketId,accountNumber,category,subject,message,timestamp,status");
                writer.newLine();
            }

            writer.write(csv(ticketId) + ","
                    + csv(customer.getAccountNumber()) + ","
                    + csv(category) + ","
                    + csv(subject) + ","
                    + csv(message) + ","
                    + LocalDateTime.now() + ","
                    + "OPEN");
            writer.newLine();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Support ticket could not be saved.");
        }

        AuditLogger.log("SUPPORT_TICKET_CREATED", ticketId + " for " + customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(), "Support ticket " + ticketId + " was submitted.");
        return ticketId;
    }

    private static String csv(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
