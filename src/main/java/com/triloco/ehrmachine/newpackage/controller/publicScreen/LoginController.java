package com.triloco.ehrmachine.newpackage.controller.publicScreen;

import javafx.fxml.FXML;
import javafx.scene.control.*;
public class LoginController {
     @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> login());
        forgotPasswordLink.setOnAction(event -> showForgotPassword());
    }

    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Dummy check (replace with actual logic)
        if (email.equals("doctor@example.com") && password.equals("123456")) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials");
        }
    }

    private void showForgotPassword() {
        System.out.println("Redirect to password recovery");
    }
}
