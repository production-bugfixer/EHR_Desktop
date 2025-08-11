package com.triloco.ehrmachine.newpackage.controller.publicScreen;


import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastController {

    public enum ToastType {
        INFO, SUCCESS, WARNING
    }

    @FXML
    private StackPane rootPane;

    @FXML
    private Label messageLabel;

    public void showToast(String message, ToastType type) {
        messageLabel.setText(message);

        // Clear previous style classes and add base + type style
        rootPane.getStyleClass().clear();
        rootPane.getStyleClass().add("toast");

        switch (type) {
    case INFO:
        rootPane.getStyleClass().add("info");
        break;
    case SUCCESS:
        rootPane.getStyleClass().add("success");
        break;
    case WARNING:
        rootPane.getStyleClass().add("warning");
        break;
}

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(700), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));

        fadeIn.play();
        fadeIn.setOnFinished(event -> fadeOut.play());
    }
}
