package com.askaribank;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class ProcessingOverlay {

    private ProcessingOverlay() {
    }

    static void run(Node owner, String message, Runnable action) {
        if (owner == null || owner.getScene() == null || !(owner.getScene().getRoot() instanceof Pane rootPane)) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.3));
            delay.setOnFinished(event -> action.run());
            delay.play();
            return;
        }

        StackPane overlay = new StackPane();
        overlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.45),
                CornerRadii.EMPTY, Insets.EMPTY)));
        overlay.setPickOnBounds(true);

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(22));
        card.setMaxWidth(300);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.rgb(219, 226, 235), BorderStrokeStyle.SOLID,
                new CornerRadii(14), new BorderWidths(1))));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        card.getChildren().addAll(progressIndicator, messageLabel);
        overlay.getChildren().add(card);
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        rootPane.getChildren().add(overlay);
        PauseTransition delay = new PauseTransition(Duration.seconds(1.4));
        delay.setOnFinished(event -> {
            rootPane.getChildren().remove(overlay);
            action.run();
        });
        delay.play();
    }
}
