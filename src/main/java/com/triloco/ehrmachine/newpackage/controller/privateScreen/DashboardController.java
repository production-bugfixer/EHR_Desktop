package com.triloco.ehrmachine.newpackage.controller.privateScreen;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.stage.Stage;

public class DashboardController {

    // Data Models
    public static class DashboardItem {
        private final String title;
        private final String value;
        private final String note;
        private final String color;
        
        public DashboardItem(String title, String value, String note, String color) {
            this.title = title;
            this.value = value;
            this.note = note;
            this.color = color;
        }

        public String getTitle() { return title; }
        public String getValue() { return value; }
        public String getNote() { return note; }
        public String getColor() { return color; }
    }
    
    public static class QuickAction {
        private final String text;
        private final String color;
        private final Runnable action;
        
        public QuickAction(String text, String color, Runnable action) {
            this.text = text;
            this.color = color;
            this.action = action;
        }

        public String getText() { return text; }
        public String getColor() { return color; }
        public Runnable getAction() { return action; }
    }
    
    public static class RecentPatient {
        private final String name;
        private final String lastVisit;
        private final int patientId;
        private final String avatarColor;
        
        public RecentPatient(String name, String lastVisit, int patientId, String avatarColor) {
            this.name = name;
            this.lastVisit = lastVisit;
            this.patientId = patientId;
            this.avatarColor = avatarColor;
        }

        public String getName() { return name; }
        public String getLastVisit() { return lastVisit; }
        public int getPatientId() { return patientId; }
        public String getAvatarColor() { return avatarColor; }
    }

    public static class Appointment {
        private final StringProperty time;
        private final StringProperty patient;
        private final StringProperty reason;
        private final StringProperty status;
        
        public Appointment(String time, String patient, String reason, String status) {
            this.time = new SimpleStringProperty(time);
            this.patient = new SimpleStringProperty(patient);
            this.reason = new SimpleStringProperty(reason);
            this.status = new SimpleStringProperty(status);
        }
        
        public StringProperty timeProperty() { return time; }
        public StringProperty patientProperty() { return patient; }
        public StringProperty reasonProperty() { return reason; }
        public StringProperty statusProperty() { return status; }
        
        public String getTime() { return time.get(); }
        public String getPatient() { return patient.get(); }
        public String getReason() { return reason.get(); }
        public String getStatus() { return status.get(); }
    }

    // FXML Elements
    @FXML private Label welcomeLabel;
    @FXML private FlowPane statsContainer;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, String> reasonColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private VBox quickActionsContainer;
    @FXML private FlowPane recentPatientsContainer;
    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    // Data Collections
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private List<DashboardItem> dashboardItems = Arrays.asList(
        new DashboardItem("Today's Appointments", "12", "2 from yesterday", "#1E66D0"),
        new DashboardItem("Patients Waiting", "4", "Average wait: 15min", "#E0A106"),
        new DashboardItem("Completed Today", "8", "On schedule", "#1EA061"),
        new DashboardItem("Urgent Cases", "2", "Immediate attention needed", "#D93838")
    );
    
    private List<QuickAction> quickActions = Arrays.asList(
        new QuickAction("Add New Appointment", "#1E66D0", this::handleAddAppointment),
        new QuickAction("Create Prescription", "#1EA061", this::handleCreatePrescription),
        new QuickAction("Patient Lookup", "#00BCD4", this::handlePatientLookup),
        new QuickAction("Flag Urgent Case", "#E0A106", this::handleFlagUrgentCase),
        new QuickAction("Transfer Patient", "#D93838", this::handleTransferPatient)
    );
    
    private List<RecentPatient> recentPatients = Arrays.asList(
        new RecentPatient("John Doe", "Today", 1001, "#FF9AA2"),
        new RecentPatient("Jane Smith", "Yesterday", 1002, "#FFB7B2"),
        new RecentPatient("Michael Brown", "2 days ago", 1003, "#FFDAC1"),
        new RecentPatient("Emily Davis", "3 days ago", 1004, "#E2F0CB"),
        new RecentPatient("Robert Johnson", "4 days ago", 1005, "#B5EAD7"),
        new RecentPatient("Sarah Wilson", "1 week ago", 1006, "#C7CEEA")
    );

    @FXML
    public void initialize() {
        setupWelcomeMessage();
        initializeStatsContainer();
        initializeQuickActions();
        initializeRecentPatients();
        initializeAppointmentsTable();
        loadSampleData();
        setupResponsiveLayout();
        Platform.runLater(()->{ this.setupMaximizedBehavior();this.setupResponsiveLayout();});
    }
private void setupFullScreenBehavior() {
        // Get the current stage
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        
        // Make stage full screen
        stage.setFullScreen(true);
        
        // Optional: Add exit hint
        stage.setFullScreenExitHint("Press ESC to exit full screen mode");
        
        // Handle full screen changes
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Adjust layout for full screen
                statsContainer.setHgap(30);
                statsContainer.setVgap(30);
                recentPatientsContainer.setHgap(30);
                recentPatientsContainer.setVgap(30);
            } else {
                // Adjust layout for windowed mode
                statsContainer.setHgap(15);
                statsContainer.setVgap(15);
                recentPatientsContainer.setHgap(15);
                recentPatientsContainer.setVgap(15);
            }
        });
    }
private void setupMaximizedBehavior() {
        // Get the current stage
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        
        // Maximize the window (not full screen)
        stage.setMaximized(true);
        
        // Handle window state changes
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Adjust layout for maximized window
                statsContainer.setHgap(25);
                statsContainer.setVgap(25);
                recentPatientsContainer.setHgap(25);
                recentPatientsContainer.setVgap(25);
            } else {
                // Adjust layout for normal window
                statsContainer.setHgap(15);
                statsContainer.setVgap(15);
                recentPatientsContainer.setHgap(15);
                recentPatientsContainer.setVgap(15);
            }
            // Force layout update
            mainContainer.requestLayout();
        });
    }
     private void setupResponsiveLayout() {
        if (scrollPane == null || mainContainer == null) {
            System.err.println("UI components not initialized!");
            return;
        }

        // Make containers fill available space
        mainContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(mainContainer, Priority.ALWAYS);
        
        // Initial setup
        updateLayoutSizes(scrollPane.getViewportBounds().getWidth());
        
        // Bind to viewport changes
        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateLayoutSizes(newVal.getWidth());
            }
        });
        
        // Configure table to fill width
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupWelcomeMessage() {
        welcomeLabel.setText("Welcome back, Dr. Smith");
    }

    private void initializeStatsContainer() {
        statsContainer.getChildren().clear();
        
        for (DashboardItem item : dashboardItems) {
            VBox card = createStatCard(item);
            statsContainer.getChildren().add(card);
        }
    }

    private VBox createStatCard(DashboardItem item) {
        VBox card = new VBox(8);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.getStyleClass().add("stat-card");
        card.setStyle(String.format("-fx-border-color: %s;", item.getColor()));
        
        Label titleLabel = new Label(item.getTitle());
        titleLabel.getStyleClass().add("stat-card-title");
        titleLabel.setStyle(String.format("-fx-text-fill: %s;", item.getColor()));
        
        Label valueLabel = new Label(item.getValue());
        valueLabel.getStyleClass().add("stat-card-value");
        
        Label noteLabel = new Label(item.getNote());
        noteLabel.getStyleClass().add("stat-card-note");
        
        VBox.setMargin(titleLabel, new Insets(0, 0, 5, 0));
        VBox.setMargin(valueLabel, new Insets(0, 0, 3, 0));
        
        card.getChildren().addAll(titleLabel, valueLabel, noteLabel);
        return card;
    }

    private void initializeQuickActions() {
        quickActionsContainer.getChildren().clear();
        
        for (QuickAction action : quickActions) {
            Button button = createActionButton(action);
            quickActionsContainer.getChildren().add(button);
        }
    }

    private Button createActionButton(QuickAction action) {
        Button button = new Button(action.getText());
        button.getStyleClass().add("action-button");
        button.setStyle(String.format("-fx-border-color: %s; -fx-text-fill: %s;", action.getColor(), action.getColor()));
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(e -> action.getAction().run());
        return button;
    }

    private void initializeRecentPatients() {
        recentPatientsContainer.getChildren().clear();
        
        for (RecentPatient patient : recentPatients) {
            VBox patientCard = createPatientCard(patient);
            recentPatientsContainer.getChildren().add(patientCard);
        }
    }

    private VBox createPatientCard(RecentPatient patient) {
        VBox card = new VBox(10);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.getStyleClass().add("patient-card");
        
        Circle avatar = new Circle(30, Color.web(patient.getAvatarColor()));
        
        Label nameLabel = new Label(patient.getName());
        nameLabel.getStyleClass().add("patient-name");
        
        Label lastVisitLabel = new Label("Last visit: " + patient.getLastVisit());
        lastVisitLabel.getStyleClass().add("patient-visit");
        
        Button profileButton = new Button("View Profile");
        profileButton.getStyleClass().add("patient-button");
        profileButton.setOnAction(e -> viewPatientProfile(patient.getPatientId()));
        
        VBox.setMargin(avatar, new Insets(0, 0, 5, 0));
        VBox.setMargin(profileButton, new Insets(10, 0, 0, 0));
        
        card.getChildren().addAll(avatar, nameLabel, lastVisitLabel, profileButton);
        return card;
    }

    private void initializeAppointmentsTable() {
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        patientColumn.setCellValueFactory(cellData -> cellData.getValue().patientProperty());
        reasonColumn.setCellValueFactory(cellData -> cellData.getValue().reasonProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        appointmentsTable.setItems(appointments);
        timeColumn.getStyleClass().add("table-column");
        patientColumn.getStyleClass().add("table-column");
        reasonColumn.getStyleClass().add("table-column");
        statusColumn.getStyleClass().add("table-column");
    }

    private void loadSampleData() {
        appointments.addAll(
            new Appointment("09:00 AM", "John Doe", "Annual Checkup", "Confirmed"),
            new Appointment("10:30 AM", "Jane Smith", "Follow-up", "Confirmed"),
            new Appointment("12:00 PM", "Michael Brown", "Vaccination", "Pending"),
            new Appointment("02:15 PM", "Emily Davis", "Consultation", "Confirmed"),
            new Appointment("03:45 PM", "Robert Johnson", "Blood Test", "Completed"),
            new Appointment("04:30 PM", "Sarah Wilson", "Physical Exam", "Scheduled")
        );
    }
private void updateLayoutSizes(double width) {
        // Calculate available width minus padding
        double availableWidth = width - 40;
        
        // Update containers
        statsContainer.setPrefWrapLength(availableWidth);
        recentPatientsContainer.setPrefWrapLength(availableWidth);
        appointmentsTable.setPrefWidth(availableWidth);
        
        // Ensure minimum sizes
        statsContainer.setMinWidth(availableWidth);
        recentPatientsContainer.setMinWidth(availableWidth);
        appointmentsTable.setMinWidth(availableWidth);
        
        // Adjust scroll pane to prevent clipping
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
    // Action Handlers
    private void handleAddAppointment() {
        System.out.println("Add New Appointment clicked");
    }

    private void handleCreatePrescription() {
        System.out.println("Create Prescription clicked");
    }

    private void handlePatientLookup() {
        System.out.println("Patient Lookup clicked");
    }

    private void handleFlagUrgentCase() {
        System.out.println("Flag Urgent Case clicked");
    }

    private void handleTransferPatient() {
        System.out.println("Transfer Patient clicked");
    }

    private void viewPatientProfile(int patientId) {
        System.out.println("Viewing profile for patient ID: " + patientId);
    }
}