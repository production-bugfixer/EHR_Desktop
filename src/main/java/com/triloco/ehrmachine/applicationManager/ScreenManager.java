package com.triloco.ehrmachine.applicationManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {

    // Singleton instance (thread-safe)
    private static final ScreenManager instance = new ScreenManager();

    private Stage primaryStage;
    private String currentFxmlPath;
    private Parent currentRoot;
    private Object currentController;

    // Private constructor
    private ScreenManager() {}

    // Public accessor
    public static ScreenManager getInstance() {
        return instance;
    }

    /**
     * Initialize the screen manager with the primary stage.
     * This method should be called from your App.java class in start().
     */
    public void initialize(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("EHR Machine");

        // Load session from file
        SessionManager.restoreSession();

        // Decide initial screen
        String initialView = SessionManager.isLoggedIn()
                ? "/com/triloco/ehrmachine/privateView/Dashboard.fxml"
                : "/com/triloco/ehrmachine/publicView/Login.fxml";

        switchScene(initialView);
        stage.show();
    }

    /**
     * Switches to the specified scene via its FXML path.
     * @param fxmlPath Full path to the FXML (e.g., "/com/example/view/MyView.fxml")
     */
    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }

            // Track scene info
            this.currentFxmlPath = fxmlPath;
            this.currentRoot = root;
            this.currentController = loader.getController();

        } catch (IOException e) {
            System.err.println("❌ Failed to load FXML: " + fxmlPath);
            e.printStackTrace(); // Consider replacing with proper logging
        }
    }

    // Optional setter (for manual setup/testing)
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Scene info accessors
    public String getCurrentFxmlPath() {
        return currentFxmlPath;
    }

    public Parent getCurrentRoot() {
        return currentRoot;
    }

    public Object getCurrentController() {
        return currentController;
    }

    public Scene getCurrentScene() {
        return primaryStage != null ? primaryStage.getScene() : null;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
