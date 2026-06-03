package com.askaribank;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class AuditLogController {

    private TextArea auditTextArea;

    public Parent createView() {
        VBox page = UiFactory.page();
        page.setSpacing(14);

        Button refreshButton = UiFactory.button("Refresh");
        refreshButton.setOnAction(event -> handleRefreshLog());

        auditTextArea = UiFactory.textArea(460);
        page.getChildren().addAll(
                UiFactory.title("Audit Log"),
                new Label("Recent simple banking actions saved in audit_log.txt."),
                refreshButton,
                auditTextArea
        );

        initialize();
        return page;
    }

    private void initialize() {
        handleRefreshLog();
    }

    private void handleRefreshLog() {
        ArrayList<String> logs = AuditLogger.readRecentLogs();
        StringBuilder builder = new StringBuilder();
        int startIndex = Math.max(0, logs.size() - 150);
        for (int index = startIndex; index < logs.size(); index++) {
            builder.append(logs.get(index)).append(System.lineSeparator());
        }
        auditTextArea.setText(builder.toString());
    }
}
