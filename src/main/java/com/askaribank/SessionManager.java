package com.askaribank;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.util.Duration;

final class SessionManager {

    private SessionManager() {
    }

    static PauseTransition startSessionTimeout(Scene scene, Runnable logoutAction) {
        PauseTransition timeout = new PauseTransition(Duration.minutes(3));
        timeout.setOnFinished(event -> {
            AlertHelper.showInfo("Session Expired", "Session expired for security reasons.");
            logoutAction.run();
        });

        scene.addEventFilter(javafx.scene.input.MouseEvent.ANY, event -> timeout.playFromStart());
        scene.addEventFilter(javafx.scene.input.KeyEvent.ANY, event -> timeout.playFromStart());
        timeout.playFromStart();
        return timeout;
    }
}
