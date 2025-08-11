package com.triloco.ehrmachine.newpackage.controller.publicScreen;

import com.triloco.ehrmachine.newpackage.service.LoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class LoginController {

    @FXML private Pane rootPane;  // Main container to add toast into
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;

    private LoginService loginService = LoginService.getInstance();

    @FXML
    private void initialize() {
        loginButton.setDisable(true);
        setButtonStyle(false);

        emailField.textProperty().addListener((obs, oldText, newText) -> validateInput());
        passwordField.textProperty().addListener((obs, oldText, newText) -> validateInput());

        loginButton.setOnAction(event -> login());
        forgotPasswordLink.setOnAction(event -> showForgotPassword());
    }

    private void validateInput() {
        boolean isEmailFilled = !emailField.getText().trim().isEmpty();
        boolean isPasswordFilled = !passwordField.getText().trim().isEmpty();

        boolean enableButton = isEmailFilled && isPasswordFilled;

        loginButton.setDisable(!enableButton);
        setButtonStyle(enableButton);
    }

    private void setButtonStyle(boolean enabled) {
        if (enabled) {
            // Dark parrot green when enabled
            loginButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        } else {
            // Light parrot green (dull) when disabled
            loginButton.setStyle("-fx-background-color: #a9dfbf; -fx-text-fill: white;");
        }
    }

    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        boolean success = loginService.login(email, password);
        if (success) {
            showToast("Login successful!", ToastController.ToastType.SUCCESS);
        } else {
            showToast("Login failed! Please check your credentials.", ToastController.ToastType.WARNING);
        }
    }

    private void showForgotPassword() {
        showToast("Redirecting to password recovery...", ToastController.ToastType.INFO);
        // Add actual redirect logic here
    }

    private void showToast(String message, ToastController.ToastType type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/triloco/ehrmachine/publicView/Toaster.fxml"));
            Parent toast = loader.load();
            ToastController toastController = loader.getController();

            toastController.showToast(message, type);

            rootPane.getChildren().add(toast);

            // Remove toast after fade out
            toast.opacityProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 0) {
                    rootPane.getChildren().remove(toast);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
