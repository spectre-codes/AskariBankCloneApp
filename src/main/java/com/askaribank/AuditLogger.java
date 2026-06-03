package com.askaribank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AuditLogger {

    private static final File DATA_DIRECTORY = new File("data");
    private static final File AUDIT_FILE = new File(DATA_DIRECTORY, "audit_log.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditLogger() {
    }

    public static void log(String action, String detail) {
        try {
            if (!DATA_DIRECTORY.exists()) {
                DATA_DIRECTORY.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUDIT_FILE, true))) {
                writer.write(LocalDateTime.now().format(FORMATTER));
                writer.write(" | ");
                writer.write(action);
                writer.write(" | ");
                writer.write(detail == null ? "" : detail);
                writer.newLine();
            }
        } catch (IOException exception) {
            System.err.println("Audit log could not be written: " + exception.getMessage());
        }
    }

    public static ArrayList<String> readRecentLogs() {
        ArrayList<String> logs = new ArrayList<>();
        if (!AUDIT_FILE.exists()) {
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(AUDIT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException exception) {
            logs.add("Audit log could not be loaded: " + exception.getMessage());
        }

        return logs;
    }
}
