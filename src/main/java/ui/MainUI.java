package ui;

import dao.AdminDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainUI extends Application {
    
    public static void launchUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management System - Secure Login");

        // The main background
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        // The floating card
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(400);
        card.getStyleClass().add("card"); // Applies CSS Drop Shadow

        // Aesthetic Titles
        Label welcomeTitle = new Label("Welcome to");
        welcomeTitle.setFont(Font.font("Arial", 16));
        welcomeTitle.setTextFill(Color.web("#64748b"));

        Label mainTitle = new Label("Employee Management System");
        mainTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        mainTitle.setTextFill(Color.web("#1e293b"));
        mainTitle.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        mainTitle.setWrapText(true);

        TextField userField = new TextField();
        userField.setPromptText("Administrator Username");
        userField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(300);

        Button loginBtn = new Button("Secure Sign In");
        loginBtn.setMaxWidth(300);
        loginBtn.setPadding(new Insets(12));
        
        Label statusLabel = new Label();

        loginBtn.setOnAction(e -> {
            AdminDAO dao = new AdminDAO();
            if (dao.authenticate(userField.getText(), passField.getText())) {
                primaryStage.close();
                new DashboardUI().start(new Stage());
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Authentication failed. Check credentials.");
            }
        });

        card.getChildren().addAll(welcomeTitle, mainTitle, userField, passField, loginBtn, statusLabel);

        // --- NEW: Developer Signature ---
        Label developerSignature = new Label("Developed by Karan");
        developerSignature.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        developerSignature.setTextFill(Color.web("#94a3b8")); // Subtle grey color
        developerSignature.setPadding(new Insets(15, 0, 0, 0));

        // Add both the card and the signature to the main screen
        root.getChildren().addAll(card, developerSignature);

        Scene scene = new Scene(root, 600, 550);
        
        // Load CSS Safely
        try {
            String css = this.getClass().getResource("modern-theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Theme missing. Using default JavaFX styling.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}