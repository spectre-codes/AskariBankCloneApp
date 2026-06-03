package com.askaribank;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

final class ToastManager {

    private ToastManager() {
    }

    static void show(Node owner, String message) {
        if (owner == null || owner.getScene() == null || !(owner.getScene().getRoot() instanceof Pane rootPane)) {
            return;
        }

        Label toast = new Label(message);
        toast.setTextFill(Color.WHITE);
        toast.setPadding(new Insets(10, 16, 10, 16));
        toast.setBackground(new Background(new BackgroundFill(Color.rgb(35, 45, 61, 0.94),
                new CornerRadii(18), Insets.EMPTY)));

        StackPane wrapper = new StackPane(toast);
        wrapper.setMouseTransparent(true);
        wrapper.setAlignment(Pos.BOTTOM_CENTER);
        wrapper.setPadding(new Insets(0, 0, 88, 0));
        wrapper.prefWidthProperty().bind(rootPane.widthProperty());
        wrapper.prefHeightProperty().bind(rootPane.heightProperty());

        rootPane.getChildren().add(wrapper);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), wrapper);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setDelay(Duration.seconds(1.2));
        fadeTransition.setOnFinished(event -> rootPane.getChildren().remove(wrapper));
        fadeTransition.play();
    }
}
