package ui;

import dao.AdminDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class MainUI extends Application {

    public static void launchUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management System - Secure Login");

        java.io.InputStream iconStream = findIconStream();
        if (iconStream != null) {
            try {
                primaryStage.getIcons().add(new Image(iconStream));
            } catch (Exception e) {
                System.err.println("Icon load failed: " + e.getMessage());
            }
        }

        StackPane root = new StackPane();
        root.getStyleClass().add("login-root-premium");

        VBox mainLayout = new VBox(14);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(24, 40, 18, 40));

        VBox card = new VBox(14);
        card.getStyleClass().add("login-glass-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26, 42, 26, 42));
        card.setMaxWidth(590);
        card.setMinWidth(590);

        Rectangle cardClip = new Rectangle();
        cardClip.setArcWidth(34);
        cardClip.setArcHeight(34);
        cardClip.widthProperty().bind(card.widthProperty());
        cardClip.heightProperty().bind(card.heightProperty());
        card.setClip(cardClip);

        StackPane logoCircle = new StackPane();
        logoCircle.getStyleClass().add("login-logo-circle");
        logoCircle.setMinSize(86, 86);
        logoCircle.setPrefSize(86, 86);
        logoCircle.setMaxSize(86, 86);

        logoCircle.getChildren().add(createLogoFallback());

        HBox welcomeRow = new HBox(12);
        welcomeRow.setAlignment(Pos.CENTER);

        Label lineLeft = new Label("━━━━");
        lineLeft.getStyleClass().add("login-small-line");

        Label welcomeBack = new Label("Welcome Back");
        welcomeBack.getStyleClass().add("login-welcome-text");

        Label dot = new Label("•");
        dot.getStyleClass().add("login-dot");

        Label lineRight = new Label("━━━━");
        lineRight.getStyleClass().add("login-small-line");

        welcomeRow.getChildren().addAll(lineLeft, welcomeBack, dot, lineRight);

        TextFlow titleFlow = new TextFlow();
        titleFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        titleFlow.getStyleClass().add("login-title-flow");

        Text titleOne = new Text("Employee ");
        titleOne.getStyleClass().add("login-title-dark");

        Text titleTwo = new Text("Management");
        titleTwo.getStyleClass().add("login-title-orange");

        Text titleThree = new Text(" System");
        titleThree.getStyleClass().add("login-title-dark");

        titleFlow.getChildren().addAll(titleOne, titleTwo, titleThree);

        Label subtitle = new Label("Sign in securely to manage your workforce");
        subtitle.getStyleClass().add("login-subtitle");

        Label titleDivider = new Label("━━━━━━━━━━━━━━━━━━━━");
        titleDivider.getStyleClass().add("login-title-divider");

        TextField userField = new TextField();
        userField.setPromptText("Username or Email");
        userField.getStyleClass().add("login-input-field");

        HBox usernameBox = createLoginInputBox("♙", userField);
        usernameBox.getStyleClass().add("login-input-active");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("login-input-field");

        TextField visiblePassField = new TextField();
        visiblePassField.setPromptText("Password");
        visiblePassField.getStyleClass().add("login-input-field");
        visiblePassField.setVisible(false);
        visiblePassField.setManaged(false);

        Label eyeBtn = new Label("◉");
        eyeBtn.getStyleClass().add("login-eye-icon");
        eyeBtn.setCursor(Cursor.HAND);

        HBox passwordBox = createPasswordBox("▣", passField, visiblePassField, eyeBtn);

        final boolean[] showPassword = {false};

        eyeBtn.setOnMouseClicked(event -> {
            showPassword[0] = !showPassword[0];

            if (showPassword[0]) {
                visiblePassField.setText(passField.getText());
                passField.setVisible(false);
                passField.setManaged(false);
                visiblePassField.setVisible(true);
                visiblePassField.setManaged(true);

                if (!eyeBtn.getStyleClass().contains("login-eye-active")) {
                    eyeBtn.getStyleClass().add("login-eye-active");
                }
            } else {
                passField.setText(visiblePassField.getText());
                visiblePassField.setVisible(false);
                visiblePassField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);

                eyeBtn.getStyleClass().remove("login-eye-active");
            }
        });

        HBox optionRow = new HBox();
        optionRow.setAlignment(Pos.CENTER_LEFT);
        optionRow.setMaxWidth(500);

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("login-remember-check");

        Region optionSpacer = new Region();
        HBox.setHgrow(optionSpacer, Priority.ALWAYS);

        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.getStyleClass().add("login-forgot-link");

        optionRow.getChildren().addAll(rememberMe, optionSpacer, forgotPassword);

        Button loginBtn = new Button("▱   Secure Sign In");
        loginBtn.getStyleClass().add("login-primary-button");
        loginBtn.setMaxWidth(500);
        loginBtn.setPrefHeight(52);
        loginBtn.setCursor(Cursor.HAND);

        HBox orRow = new HBox(10);
        orRow.setAlignment(Pos.CENTER);
        orRow.setMaxWidth(500);

        Label orLeft = new Label("━━━━━━");
        orLeft.getStyleClass().add("login-or-line");

        Label orText = new Label("OR");
        orText.getStyleClass().add("login-or-text");

        Label orRight = new Label("━━━━━━");
        orRight.getStyleClass().add("login-or-line");

        orRow.getChildren().addAll(orLeft, orText, orRight);

        Button adminDashboardBtn = new Button("♙   Access Admin Dashboard");
        adminDashboardBtn.getStyleClass().add("login-secondary-button");
        adminDashboardBtn.setMaxWidth(500);
        adminDashboardBtn.setPrefHeight(46);
        adminDashboardBtn.setCursor(Cursor.HAND);

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("login-status-label");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(500);

        loginBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = showPassword[0] ? visiblePassField.getText() : passField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #f97316;");
                statusLabel.setText("⚠ Please enter username and password.");
                return;
            }

            AdminDAO dao = new AdminDAO();

            if (dao.authenticate(username, password)) {
                primaryStage.close();
                new DashboardUI().start(new Stage());
            } else {
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("✕ Authentication failed. Check credentials.");
            }
        });

        adminDashboardBtn.setOnAction(e -> {
            primaryStage.close();
            new DashboardUI().start(new Stage());
        });

        userField.setOnAction(e -> passField.requestFocus());
        passField.setOnAction(e -> loginBtn.fire());
        visiblePassField.setOnAction(e -> loginBtn.fire());

        card.getChildren().addAll(
                logoCircle,
                welcomeRow,
                titleFlow,
                subtitle,
                titleDivider,
                usernameBox,
                passwordBox,
                optionRow,
                loginBtn,
                orRow,
                adminDashboardBtn,
                statusLabel
        );

        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("login-footer");

        Label help = new Label("♙  Need Help? Contact Admin");
        help.getStyleClass().add("login-footer-text");

        Label footerDivider = new Label("|");
        footerDivider.getStyleClass().add("login-footer-divider");

        Label version = new Label("v1.0.0");
        version.getStyleClass().add("login-footer-text");

        footer.getChildren().addAll(help, footerDivider, version);

        mainLayout.getChildren().addAll(card, footer);
        root.getChildren().add(mainLayout);

        Scene scene = new Scene(root, 1280, 720);

        try {
            String css = getClass().getResource("modern-theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("CSS theme not found.");
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    private HBox createLoginInputBox(String iconText, TextField field) {
        HBox inputBox = new HBox(14);
        inputBox.getStyleClass().add("login-input-box");
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setMaxWidth(500);
        inputBox.setPrefHeight(48);

        Label icon = new Label(iconText);
        icon.getStyleClass().add("login-input-icon");

        HBox.setHgrow(field, Priority.ALWAYS);

        inputBox.getChildren().addAll(icon, field);
        return inputBox;
    }

    private HBox createPasswordBox(String iconText, PasswordField passField, TextField visiblePassField, Label eyeBtn) {
        HBox inputBox = new HBox(14);
        inputBox.getStyleClass().add("login-input-box");
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setMaxWidth(500);
        inputBox.setPrefHeight(48);

        Label icon = new Label(iconText);
        icon.getStyleClass().add("login-input-icon");

        HBox.setHgrow(passField, Priority.ALWAYS);
        HBox.setHgrow(visiblePassField, Priority.ALWAYS);

        inputBox.getChildren().addAll(icon, passField, visiblePassField, eyeBtn);
        return inputBox;
    }

    private Label createLogoFallback() {
        Label fallback = new Label("👥");
        fallback.getStyleClass().add("login-logo-fallback");
        return fallback;
    }

    private java.io.InputStream findIconStream() {
        java.io.InputStream s = getClass().getResourceAsStream("/pro.png");
        if (s != null) return s;

        try {
            s = new java.io.FileInputStream("target/classes/pro.png");
            if (s != null) return s;
        } catch (Exception ignored) {
        }

        try {
            s = new java.io.FileInputStream("src/main/resources/pro.png");
            if (s != null) return s;
        } catch (Exception ignored) {
        }

        try {
            s = new java.io.FileInputStream("pro.png");
            if (s != null) return s;
        } catch (Exception ignored) {
        }

        return getClass().getResourceAsStream("pro.png");
    }
}