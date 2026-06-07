package ui;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.AttendanceDAO;
import dao.EmployeeDAO;
import dao.LeaveDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.Employee;

public class DashboardUI {

    private BorderPane root;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final LeaveDAO leaveDAO = new LeaveDAO();

    private Button btnHome;
    private Button btnAnalytics;
    private Button btnView;
    private Button btnAdd;
    private Button btnAttendance;
    private Button btnApplyLeave;
    private Button btnLeaveApproval;

    private static final int DEMO_EMPLOYEE_ID = 1;
    private static final int ADMIN_ID = 1;

    public void start(Stage dashboardStage) {
        dashboardStage.setTitle("Employee Management System - Dashboard");
        loadAppIcon(dashboardStage);

        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        root.setLeft(createSideMenu(dashboardStage));

        displayHomeGrid();
        setActiveMenu(btnHome);

        Scene scene = new Scene(root, 1280, 700);

        try {
            String css = this.getClass().getResource("modern-theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("WARNING: modern-theme.css not found.");
        }

        dashboardStage.setScene(scene);
        dashboardStage.setMinWidth(1150);
        dashboardStage.setMinHeight(650);
        dashboardStage.show();
    }

    private void loadAppIcon(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/pro.png");

            if (iconStream == null) {
                System.err.println("Dashboard icon not found. Put pro.png in src/main/resources/pro.png");
                return;
            }

            Image icon = new Image(iconStream);
            stage.getIcons().add(icon);

        } catch (Exception e) {
            System.err.println("Dashboard icon loading failed: " + e.getMessage());
        }
    }

    private void setCenterPage(VBox page) {
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("clean-scroll-pane");

        root.setCenter(scrollPane);
    }

    private VBox createSideMenu(Stage stage) {
        VBox menu = new VBox(12);
        menu.getStyleClass().add("sidebar");
        menu.setPrefWidth(235);

        VBox profileSection = new VBox(8);
        profileSection.getStyleClass().add("sidebar-profile");

        StackPane avatarContainer = new StackPane();
        avatarContainer.setAlignment(Pos.CENTER);

        Circle avatarCircle = new Circle(43);
        avatarCircle.getStyleClass().add("avatar-placeholder");

        Text avatarText = new Text("👤");
        avatarText.getStyleClass().add("avatar-text");

        Circle statusDot = new Circle(7);
        statusDot.getStyleClass().add("online-dot");
        StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(statusDot, new Insets(0, 4, 8, 0));

        avatarContainer.getChildren().addAll(avatarCircle, avatarText, statusDot);

        Label adminName = new Label("System Admin");
        adminName.getStyleClass().add("profile-name");

        Label adminRole = new Label("Administrator");
        adminRole.getStyleClass().add("profile-role");

        profileSection.getChildren().addAll(avatarContainer, adminName, adminRole);

        btnHome = createSidebarButton("⌂", "Home Menu");
        btnAnalytics = createSidebarButton("▧", "Analytics");
        btnView = createSidebarButton("👥", "View Employees");
        btnAdd = createSidebarButton("+", "Add Employee");
        btnAttendance = createSidebarButton("◷", "Today Attendance");
        btnApplyLeave = createSidebarButton("▣", "Apply Leave");
        btnLeaveApproval = createSidebarButton("✓", "Leave Approval");

        Button logoutBtn = createSidebarButton("↪", "Logout");

        btnHome.setOnAction(e -> {
            displayHomeGrid();
            setActiveMenu(btnHome);
        });

        btnAnalytics.setOnAction(e -> {
            displayAnalytics();
            setActiveMenu(btnAnalytics);
        });

        btnView.setOnAction(e -> {
            displayEmployees();
            setActiveMenu(btnView);
        });

        btnAdd.setOnAction(e -> {
            displayAddEmployee();
            setActiveMenu(btnAdd);
        });

        btnAttendance.setOnAction(e -> {
            displayAttendance();
            setActiveMenu(btnAttendance);
        });

        btnApplyLeave.setOnAction(e -> {
            displayApplyLeave();
            setActiveMenu(btnApplyLeave);
        });

        btnLeaveApproval.setOnAction(e -> {
            displayLeaveApproval();
            setActiveMenu(btnLeaveApproval);
        });

        logoutBtn.setOnAction(e -> stage.close());

        VBox navBox = new VBox(8);
        navBox.getStyleClass().add("nav-box");
        navBox.getChildren().addAll(
                btnHome,
                btnAnalytics,
                btnView,
                btnAdd,
                btnAttendance,
                btnApplyLeave,
                btnLeaveApproval
        );

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox quoteBox = new VBox(8);
        quoteBox.getStyleClass().add("sidebar-quote");

        Label quoteIcon = new Label("✦");
        quoteIcon.getStyleClass().add("quote-icon");

        Label quoteTitle = new Label("Smart Workforce.");
        quoteTitle.getStyleClass().add("quote-title");

        Label quoteSub = new Label("Stronger Tomorrow.");
        quoteSub.getStyleClass().add("quote-subtitle");

        Label quoteLine = new Label("━━━━");
        quoteLine.getStyleClass().add("quote-line");

        quoteBox.getChildren().addAll(quoteIcon, quoteTitle, quoteSub, quoteLine);

        menu.getChildren().addAll(profileSection, navBox, spacer, logoutBtn, quoteBox);

        return menu;
    }

    private Button createSidebarButton(String icon, String text) {
        Button button = new Button(icon + "   " + text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("sidebar-button");
        return button;
    }

    private void setActiveMenu(Button activeBtn) {
        Button[] buttons = {
                btnHome,
                btnAnalytics,
                btnView,
                btnAdd,
                btnAttendance,
                btnApplyLeave,
                btnLeaveApproval
        };

        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("active-menu");
            }
        }

        if (activeBtn != null && !activeBtn.getStyleClass().contains("active-menu")) {
            activeBtn.getStyleClass().add("active-menu");
        }
    }

    private void displayHomeGrid() {
        VBox page = new VBox(14);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(14, 20, 12, 20));

        page.getChildren().add(createTopBar());
        page.getChildren().add(createHeroBanner());

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.getStyleClass().add("landing-grid");

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(33.33);
        c1.setHgrow(Priority.ALWAYS);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(33.33);
        c2.setHgrow(Priority.ALWAYS);

        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(33.33);
        c3.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().setAll(c1, c2, c3);

        VBox analyticsCard = createMenuCard(
                "▥",
                "Dashboard Analytics",
                "View workforce distribution and analytics with insights and trends.",
                "Open Dashboard  →",
                "card-icon-orange",
                () -> {
                    displayAnalytics();
                    setActiveMenu(btnAnalytics);
                }
        );

        VBox viewCard = createMenuCard(
                "👥",
                "View Employees",
                "Search, browse, and manage employee records efficiently.",
                "Open Records  →",
                "card-icon-purple",
                () -> {
                    displayEmployees();
                    setActiveMenu(btnView);
                }
        );

        VBox addCard = createMenuCard(
                "+",
                "Add Employee",
                "Quickly add new employees with a simple form.",
                "Open Form  →",
                "card-icon-green",
                () -> {
                    displayAddEmployee();
                    setActiveMenu(btnAdd);
                }
        );

        VBox attendanceCard = createMenuCard(
                "◷",
                "Today Attendance",
                "Mark daily attendance with present, half-day, or absent status.",
                "Mark Attendance  →",
                "card-icon-yellow",
                () -> {
                    displayAttendance();
                    setActiveMenu(btnAttendance);
                }
        );

        VBox leaveCard = createMenuCard(
                "▣",
                "Apply Leave",
                "Apply employee leave requests with date range and remarks.",
                "Apply Leave  →",
                "card-icon-pink",
                () -> {
                    displayApplyLeave();
                    setActiveMenu(btnApplyLeave);
                }
        );

        VBox approvalCard = createMenuCard(
                "✓",
                "Leave Approval",
                "Admin can approve or reject leave requests easily.",
                "Open Approval  →",
                "card-icon-mint",
                () -> {
                    displayLeaveApproval();
                    setActiveMenu(btnLeaveApproval);
                }
        );

        grid.add(analyticsCard, 0, 0);
        grid.add(viewCard, 1, 0);
        grid.add(addCard, 2, 0);
        grid.add(attendanceCard, 0, 1);
        grid.add(leaveCard, 1, 1);
        grid.add(approvalCard, 2, 1);

        page.getChildren().add(grid);
        page.getChildren().add(createBottomInfoBar());

        root.setCenter(page);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.getStyleClass().add("top-bar");
        topBar.setMinHeight(36);
        topBar.setPrefHeight(36);
        topBar.setMaxHeight(36);

        Label search = new Label("⌕");
        search.getStyleClass().add("top-icon-button");

        StackPane notificationStack = new StackPane();

        Label bell = new Label("●");
        bell.getStyleClass().add("top-icon-button");

        Label badge = new Label("1");
        badge.getStyleClass().add("notification-badge");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(-5, -5, 0, 0));

        notificationStack.getChildren().addAll(bell, badge);

        Label theme = new Label("☼");
        theme.getStyleClass().add("top-icon-button");

        topBar.getChildren().addAll(search, notificationStack, theme);

        return topBar;
    }

    private HBox createHeroBanner() {
        HBox hero = new HBox();
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("hero-banner");
        hero.setPadding(new Insets(0));
        hero.setMinHeight(185);
        hero.setPrefHeight(185);
        hero.setMaxHeight(185);

        Rectangle roundedClip = new Rectangle();
        roundedClip.setArcWidth(44);
        roundedClip.setArcHeight(44);
        roundedClip.widthProperty().bind(hero.widthProperty());
        roundedClip.heightProperty().bind(hero.heightProperty());
        hero.setClip(roundedClip);

        VBox left = new VBox(8);
        left.getStyleClass().add("hero-left");
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(20, 28, 20, 32));
        HBox.setHgrow(left, Priority.ALWAYS);

        Label badge = new Label("✦  WELCOME BACK, ADMIN");
        badge.getStyleClass().add("welcome-badge");

        TextFlow titleFlow = new TextFlow();
        titleFlow.setLineSpacing(1);

        Text t1 = new Text("Welcome to ");
        t1.getStyleClass().add("welcome-title-dark");

        Text t2 = new Text("Smart Employee");
        t2.getStyleClass().add("welcome-title-orange");

        Text t3 = new Text("\nManagement System");
        t3.getStyleClass().add("welcome-title-dark");

        titleFlow.getChildren().addAll(t1, t2, t3);

        Label subtitle = new Label("Simplifying Workforce, Empowering Growth");
        subtitle.getStyleClass().add("welcome-subtitle-dark");

        Label accentLine = new Label("━━━━");
        accentLine.getStyleClass().add("hero-accent-line");

        left.getChildren().addAll(badge, titleFlow, subtitle, accentLine);

        VBox right = new VBox();
        right.getStyleClass().add("hero-right-panel");
        right.setAlignment(Pos.BOTTOM_RIGHT);
        right.setPadding(new Insets(18, 22, 22, 12));
        right.setPrefWidth(455);
        right.setMinWidth(455);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox stats = new HBox(12);
        stats.setAlignment(Pos.BOTTOM_RIGHT);

        int totalEmployees = safeEmployeeCount();
        int attendancePercent = safeTodayAttendanceCount();
        int pendingLeaves = safePendingLeaveCount();

        stats.getChildren().addAll(
                createHeroStat("👥", String.valueOf(totalEmployees), "Total Employees"),
                createHeroStat("▣", attendancePercent + "%", "Attendance Today"),
                createHeroStat("◷", String.valueOf(pendingLeaves), "Pending Approvals")
        );

        right.getChildren().addAll(spacer, stats);
        hero.getChildren().addAll(left, right);

        return hero;
    }

    private VBox createHeroStat(String icon, String value, String labelText) {
        VBox stat = new VBox(4);
        stat.getStyleClass().add("hero-stat-card");
        stat.setAlignment(Pos.CENTER_LEFT);
        stat.setPrefWidth(120);
        stat.setMinWidth(120);
        stat.setMaxWidth(120);
        stat.setPrefHeight(72);
        stat.setMinHeight(72);
        stat.setMaxHeight(72);

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("hero-stat-icon");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("hero-stat-value");

        top.getChildren().addAll(iconLabel, valueLabel);

        Label label = new Label(labelText);
        label.getStyleClass().add("hero-stat-label");
        label.setWrapText(false);

        stat.getChildren().addAll(top, label);
        return stat;
    }

    private VBox createMenuCard(String icon, String titleText, String descText, String btnText,
                                String iconClass, Runnable action) {
        VBox card = new VBox(10);
        card.getStyleClass().add("landing-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(160);
        card.setPrefHeight(160);

        HBox header = new HBox(12);
        header.setAlignment(Pos.TOP_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().addAll("landing-card-icon", iconClass);

        VBox titleBox = new VBox(6);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(titleText);
        title.getStyleClass().add("landing-card-title");

        Label dots = new Label("⋮⋮");
        dots.getStyleClass().add("card-dots");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(title, spacer, dots);

        Label desc = new Label(descText);
        desc.getStyleClass().add("landing-card-desc");
        desc.setWrapText(true);
        desc.setMaxWidth(230);

        titleBox.getChildren().addAll(topRow, desc);
        header.getChildren().addAll(iconLabel, titleBox);

        Region pushDown = new Region();
        VBox.setVgrow(pushDown, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.getStyleClass().add("landing-button-primary");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setPrefHeight(38);
        actionBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(header, pushDown, actionBtn);

        return card;
    }

    private HBox createBottomInfoBar() {
        HBox bottom = new HBox(18);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.getStyleClass().add("bottom-info-bar");

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        Label dateLabel = new Label("▣  Today is " + date);
        dateLabel.getStyleClass().add("bottom-info-text");

        Label divider = new Label("|");
        divider.getStyleClass().add("bottom-divider");

        Label timeLabel = new Label("◷  " + time);
        timeLabel.getStyleClass().add("bottom-info-text");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label quote = new Label("❝  Great teams build great organizations.  ❞");
        quote.getStyleClass().add("bottom-quote");

        bottom.getChildren().addAll(dateLabel, divider, timeLabel, spacer, quote);

        return bottom;
    }

    private int safeEmployeeCount() {
        try {
            return employeeDAO.getAllEmployees().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int safeTodayAttendanceCount() {
        try {
            int total = employeeDAO.getAllEmployees().size();

            if (total == 0) {
                return 0;
            }

            int marked = attendanceDAO.getTodayAttendance().size();

            return Math.min(100, (int) Math.round((marked * 100.0) / total));
        } catch (Exception e) {
            return 0;
        }
    }

    private int safePendingLeaveCount() {
        try {
            return (int) leaveDAO.getAllLeaves()
                    .stream()
                    .filter(l -> l.getStatus() != null && l.getStatus().equalsIgnoreCase("Pending"))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private void displayAttendance() {
        AttendanceUI attendanceUI = new AttendanceUI();
        setCenterPage(attendanceUI.getAttendanceView(DEMO_EMPLOYEE_ID));
    }

    private void displayApplyLeave() {
        LeaveManagementUI leaveManagementUI = new LeaveManagementUI();
        setCenterPage(leaveManagementUI.getLeaveApplyView(DEMO_EMPLOYEE_ID));
    }

    private void displayLeaveApproval() {
        LeaveManagementUI leaveManagementUI = new LeaveManagementUI();
        setCenterPage(leaveManagementUI.getAdminLeaveApprovalView(ADMIN_ID));
    }

    private void displayAddEmployee() {
        VBox page = new VBox(16);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(14, 20, 18, 20));

        page.getChildren().add(createTopBar());
        page.getChildren().add(createAddEmployeeHeroBanner());

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("add-employee-card");
        formCard.setPadding(new Insets(18, 24, 22, 24));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(32);
        formGrid.setVgap(14);

        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(50);
        leftCol.setHgrow(Priority.ALWAYS);

        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(50);
        rightCol.setHgrow(Priority.ALWAYS);

        formGrid.getColumnConstraints().addAll(leftCol, rightCol);

        TextField empIdField = new TextField();
        empIdField.setPromptText("Enter Employee ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Full Name");

        TextField fnameField = new TextField();
        fnameField.setPromptText("Enter Father's Name");

        DatePicker dobField = new DatePicker();
        dobField.setPromptText("Select Date of Birth");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Enter Salary");

        TextField addressField = new TextField();
        addressField.setPromptText("Enter Address");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter Phone Number");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");

        TextField educationField = new TextField();
        educationField.setPromptText("Enter Education");

        TextField designationField = new TextField();
        designationField.setPromptText("Enter Designation");

        TextField aadharField = new TextField();
        aadharField.setPromptText("Enter Aadhaar Number");

        formGrid.add(createFormField("Employee ID", "♙", empIdField), 0, 0);
        formGrid.add(createFormField("Full Name", "♙", nameField), 1, 0);

        formGrid.add(createFormField("Father's Name", "♙", fnameField), 0, 1);
        formGrid.add(createDateFormField("Date of Birth", "▣", dobField), 1, 1);

        formGrid.add(createFormField("Salary", "₹", salaryField), 0, 2);
        formGrid.add(createFormField("Address", "⌖", addressField), 1, 2);

        formGrid.add(createFormField("Phone", "☎", phoneField), 0, 3);
        formGrid.add(createFormField("Email", "✉", emailField), 1, 3);

        formGrid.add(createFormField("Education", "▱", educationField), 0, 4);
        formGrid.add(createFormField("Designation", "▤", designationField), 1, 4);

        formGrid.add(createFormField("Aadhaar Number", "▦", aadharField), 0, 5);

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("form-message-label");

        Button resetBtn = new Button("↻   Reset");
        resetBtn.getStyleClass().add("add-reset-button");

        Button addBtn = new Button("⊕   Add Employee");
        addBtn.getStyleClass().add("add-submit-button");

        resetBtn.setOnAction(e -> {
            empIdField.clear();
            nameField.clear();
            fnameField.clear();
            dobField.setValue(null);
            salaryField.clear();
            addressField.clear();
            phoneField.clear();
            emailField.clear();
            educationField.clear();
            designationField.clear();
            aadharField.clear();
            messageLabel.setText("");
        });

        addBtn.setOnAction(e -> {
            try {
                if (empIdField.getText().trim().isEmpty()
                        || nameField.getText().trim().isEmpty()
                        || fnameField.getText().trim().isEmpty()
                        || dobField.getValue() == null
                        || salaryField.getText().trim().isEmpty()
                        || addressField.getText().trim().isEmpty()
                        || phoneField.getText().trim().isEmpty()
                        || emailField.getText().trim().isEmpty()
                        || educationField.getText().trim().isEmpty()
                        || designationField.getText().trim().isEmpty()
                        || aadharField.getText().trim().isEmpty()) {

                    messageLabel.setStyle("-fx-text-fill: #dc2626;");
                    messageLabel.setText("Please fill all required fields.");
                    return;
                }

                String salaryText = salaryField.getText().trim();

                if (!salaryText.matches("\\d+(\\.\\d+)?")) {
                    messageLabel.setStyle("-fx-text-fill: #dc2626;");
                    messageLabel.setText("Salary must be a valid number.");
                    return;
                }

                Employee emp = new Employee(
                        empIdField.getText().trim(),
                        nameField.getText().trim(),
                        fnameField.getText().trim(),
                        Date.valueOf(dobField.getValue()),
                        new BigDecimal(salaryText),
                        addressField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        educationField.getText().trim(),
                        designationField.getText().trim(),
                        aadharField.getText().replaceAll("\\s+", "")
                );

                if (employeeDAO.addEmployee(emp)) {
                    messageLabel.setStyle("-fx-text-fill: #16a34a;");
                    messageLabel.setText("Employee added successfully!");

                    empIdField.clear();
                    nameField.clear();
                    fnameField.clear();
                    dobField.setValue(null);
                    salaryField.clear();
                    addressField.clear();
                    phoneField.clear();
                    emailField.clear();
                    educationField.clear();
                    designationField.clear();
                    aadharField.clear();
                } else {
                    messageLabel.setStyle("-fx-text-fill: #dc2626;");
                    messageLabel.setText("Failed to add employee. Check duplicate Employee ID, Email, or Aadhaar.");
                }

            } catch (Exception ex) {
                messageLabel.setStyle("-fx-text-fill: #dc2626;");
                messageLabel.setText("System Error: " + ex.getMessage());
            }
        });

        Region line = new Region();
        line.getStyleClass().add("form-bottom-line");
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.setMaxHeight(1);

        HBox actionBox = new HBox(18);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(resetBtn, addBtn);

        formCard.getChildren().addAll(formGrid, messageLabel, line, actionBox);
        page.getChildren().add(formCard);

        Region bottomSpace = new Region();
        bottomSpace.getStyleClass().add("bottom-safe-space");
        page.getChildren().add(bottomSpace);

        setCenterPage(page);
    }

    private HBox createAddEmployeeHeroBanner() {
        HBox hero = new HBox();
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("add-hero-banner");
        hero.setPadding(new Insets(0));
        hero.setMinHeight(165);
        hero.setPrefHeight(165);
        hero.setMaxHeight(165);

        Rectangle roundedClip = new Rectangle();
        roundedClip.setArcWidth(44);
        roundedClip.setArcHeight(44);
        roundedClip.widthProperty().bind(hero.widthProperty());
        roundedClip.heightProperty().bind(hero.heightProperty());
        hero.setClip(roundedClip);

        HBox left = new HBox(18);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(20, 28, 20, 26));
        HBox.setHgrow(left, Priority.ALWAYS);

        Label iconBadge = new Label("♙+");
        iconBadge.getStyleClass().add("add-hero-icon-badge");

        VBox textBox = new VBox(10);
        textBox.setAlignment(Pos.CENTER_LEFT);

        TextFlow titleFlow = new TextFlow();

        Text t1 = new Text("Add ");
        t1.getStyleClass().add("add-hero-title-dark");

        Text t2 = new Text("New");
        t2.getStyleClass().add("add-hero-title-orange");

        Text t3 = new Text(" Employee");
        t3.getStyleClass().add("add-hero-title-dark");

        titleFlow.getChildren().addAll(t1, t2, t3);

        Label subtitle = new Label("Fill in the employee details below to add a new team member\nto the system.");
        subtitle.getStyleClass().add("add-hero-subtitle");

        Label accent = new Label("━━━━");
        accent.getStyleClass().add("hero-accent-line");

        textBox.getChildren().addAll(titleFlow, subtitle, accent);
        left.getChildren().addAll(iconBadge, textBox);

        hero.getChildren().add(left);

        return hero;
    }

    private VBox createFormField(String labelText, String iconText, TextField field) {
        VBox box = new VBox(7);
        box.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("add-form-label");

        HBox inputWrap = new HBox(10);
        inputWrap.setAlignment(Pos.CENTER_LEFT);
        inputWrap.getStyleClass().add("icon-input-wrapper");

        Label icon = new Label(iconText);
        icon.getStyleClass().add("input-icon");

        Region divider = new Region();
        divider.getStyleClass().add("input-divider");
        divider.setPrefWidth(1);
        divider.setMinWidth(1);
        divider.setMaxWidth(1);

        HBox.setHgrow(field, Priority.ALWAYS);
        field.getStyleClass().add("icon-input-field");

        inputWrap.getChildren().addAll(icon, divider, field);
        box.getChildren().addAll(label, inputWrap);

        return box;
    }

    private VBox createDateFormField(String labelText, String iconText, DatePicker picker) {
        VBox box = new VBox(7);
        box.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("add-form-label");

        HBox inputWrap = new HBox(10);
        inputWrap.setAlignment(Pos.CENTER_LEFT);
        inputWrap.getStyleClass().add("icon-input-wrapper");

        Label icon = new Label(iconText);
        icon.getStyleClass().add("input-icon");

        Region divider = new Region();
        divider.getStyleClass().add("input-divider");
        divider.setPrefWidth(1);
        divider.setMinWidth(1);
        divider.setMaxWidth(1);

        HBox.setHgrow(picker, Priority.ALWAYS);
        picker.getStyleClass().add("icon-date-picker");

        inputWrap.getChildren().addAll(icon, divider, picker);
        box.getChildren().addAll(label, inputWrap);

        return box;
    }

    private void displayEmployees() {
        VBox page = new VBox(14);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(12, 20, 16, 20));

        page.getChildren().add(createTopBar());

        TextField heroSearchField = new TextField();
        heroSearchField.setPromptText("Search by ID or Name...");
        page.getChildren().add(createEmployeeDirectoryHeroBanner(heroSearchField));

        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("employee-directory-card");
        tableCard.setPadding(new Insets(16, 20, 16, 20));

        TextField tableSearchField = new TextField();
        tableSearchField.setPromptText("Search by ID or Name...");
        tableSearchField.getStyleClass().add("employee-search-field");

        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("employee-search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label searchIcon = new Label("⌕");
        searchIcon.getStyleClass().add("employee-search-icon");

        HBox.setHgrow(tableSearchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, tableSearchField);

        Button filterButton = new Button("▽  Filter");
        filterButton.getStyleClass().add("employee-tool-button");

        Button sortButton = new Button("↕  Sort");
        sortButton.getStyleClass().add("employee-tool-button");

        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getChildren().addAll(searchBox, filterButton, sortButton);

        TableView<Employee> table = new TableView<>();
        table.getStyleClass().add("employee-premium-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setMinHeight(300);
        table.setMaxHeight(300);

        TableColumn<Employee, Boolean> selectCol = new TableColumn<>("");
        selectCol.setPrefWidth(45);
        selectCol.setMaxWidth(55);
        selectCol.setMinWidth(45);
        selectCol.setCellFactory(col -> new TableCell<Employee, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                    checkBox.setOnAction(e -> {
                        Employee employee = getTableView().getItems().get(getIndex());
                        getTableView().getSelectionModel().select(employee);
                    });
                }
            }
        });

        TableColumn<Employee, String> empIdCol = new TableColumn<>("Employee ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> designationCol = new TableColumn<>("Designation");
        designationCol.setCellValueFactory(new PropertyValueFactory<>("designation"));

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryCol.setCellFactory(col -> new TableCell<Employee, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal salary, boolean empty) {
                super.updateItem(salary, empty);

                if (empty || salary == null) {
                    setText(null);
                } else {
                    setText("₹ " + String.format("%,.2f", salary));
                }
            }
        });

        table.getColumns().setAll(selectCol, empIdCol, nameCol, phoneCol, emailCol, designationCol, salaryCol);

        List<Employee> employees = employeeDAO.getAllEmployees();
        ObservableList<Employee> data = FXCollections.observableArrayList(employees);

        FilteredList<Employee> filteredData = new FilteredList<>(data, employee -> true);

        Runnable applyFilter = () -> {
            String heroText = heroSearchField.getText() == null ? "" : heroSearchField.getText().trim().toLowerCase();
            String tableText = tableSearchField.getText() == null ? "" : tableSearchField.getText().trim().toLowerCase();

            String keyword = !tableText.isEmpty() ? tableText : heroText;

            filteredData.setPredicate(employee -> {
                if (keyword.isEmpty()) {
                    return true;
                }

                return employee.getEmpId().toLowerCase().contains(keyword)
                        || employee.getName().toLowerCase().contains(keyword)
                        || employee.getPhone().toLowerCase().contains(keyword)
                        || employee.getEmail().toLowerCase().contains(keyword)
                        || employee.getDesignation().toLowerCase().contains(keyword);
            });
        };

        heroSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter.run());
        tableSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter.run());

        SortedList<Employee> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        Label showingLabel = new Label("Showing 1 to " + Math.min(8, data.size()) + " of " + data.size() + " employees");
        showingLabel.getStyleClass().add("employee-footer-text");

        Button prevButton = new Button("‹");
        prevButton.getStyleClass().add("pagination-button");

        Button pageButton = new Button("1");
        pageButton.getStyleClass().add("pagination-active-button");

        Button nextButton = new Button("›");
        nextButton.getStyleClass().add("pagination-button");

        HBox paginationBox = new HBox(8);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.getChildren().addAll(prevButton, pageButton, nextButton);

        Button pageSizeButton = new Button("10 per page  ⌄");
        pageSizeButton.getStyleClass().add("page-size-button");

        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        footer.getChildren().addAll(showingLabel, footerSpacer, paginationBox, pageSizeButton);

        Label actionStatus = new Label();
        actionStatus.getStyleClass().add("form-message-label");

        Button updateButton = new Button("✎  Update Selected");
        updateButton.getStyleClass().add("employee-update-button");

        Button deleteButton = new Button("🗑  Delete Selected");
        deleteButton.getStyleClass().add("employee-delete-button");

        updateButton.setOnAction(e -> {
            Employee selectedEmp = table.getSelectionModel().getSelectedItem();

            if (selectedEmp != null) {
                displayUpdateEmployee(selectedEmp);
            } else {
                actionStatus.setStyle("-fx-text-fill: #dc2626;");
                actionStatus.setText("Please select an employee to update.");
            }
        });

        deleteButton.setOnAction(e -> {
            Employee selectedEmp = table.getSelectionModel().getSelectedItem();

            if (selectedEmp != null) {
                if (employeeDAO.deleteEmployee(selectedEmp.getEmpId())) {
                    data.remove(selectedEmp);
                    actionStatus.setStyle("-fx-text-fill: #16a34a;");
                    actionStatus.setText("Employee ID " + selectedEmp.getEmpId() + " deleted successfully.");
                    showingLabel.setText("Showing 1 to " + Math.min(8, data.size()) + " of " + data.size() + " employees");
                } else {
                    actionStatus.setStyle("-fx-text-fill: #dc2626;");
                    actionStatus.setText("Database error. Could not delete employee.");
                }
            } else {
                actionStatus.setStyle("-fx-text-fill: #dc2626;");
                actionStatus.setText("Please select an employee to delete.");
            }
        });

        HBox actionBox = new HBox(14);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        actionBox.getChildren().addAll(actionStatus, actionSpacer, updateButton, deleteButton);

        tableCard.getChildren().addAll(toolbar, table, actionBox, footer);
        page.getChildren().add(tableCard);

        Region bottomSpace = new Region();
        bottomSpace.getStyleClass().add("bottom-safe-space");
        page.getChildren().add(bottomSpace);

        setCenterPage(page);
    }

    private HBox createEmployeeDirectoryHeroBanner(TextField searchField) {
        HBox hero = new HBox();
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("employee-hero-banner");
        hero.setPadding(new Insets(0));
        hero.setMinHeight(150);
        hero.setPrefHeight(150);
        hero.setMaxHeight(150);

        Rectangle roundedClip = new Rectangle();
        roundedClip.setArcWidth(44);
        roundedClip.setArcHeight(44);
        roundedClip.widthProperty().bind(hero.widthProperty());
        roundedClip.heightProperty().bind(hero.heightProperty());
        hero.setClip(roundedClip);

        HBox left = new HBox(18);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(20, 28, 20, 26));
        HBox.setHgrow(left, Priority.ALWAYS);

        Label iconBadge = new Label("👥");
        iconBadge.getStyleClass().add("employee-hero-icon-badge");

        VBox textBox = new VBox(10);
        textBox.setAlignment(Pos.CENTER_LEFT);

        TextFlow titleFlow = new TextFlow();

        Text t1 = new Text("Employee ");
        t1.getStyleClass().add("add-hero-title-dark");

        Text t2 = new Text("Directory");
        t2.getStyleClass().add("add-hero-title-orange");

        titleFlow.getChildren().addAll(t1, t2);

        Label subtitle = new Label("Search, review, and manage employee records efficiently.");
        subtitle.getStyleClass().add("add-hero-subtitle");

        Label accent = new Label("━━━━");
        accent.getStyleClass().add("hero-accent-line");

        textBox.getChildren().addAll(titleFlow, subtitle, accent);
        left.getChildren().addAll(iconBadge, textBox);

        HBox searchWrapper = new HBox(10);
        searchWrapper.setAlignment(Pos.CENTER_LEFT);
        searchWrapper.getStyleClass().add("employee-hero-search-wrapper");
        searchWrapper.setMinWidth(360);
        searchWrapper.setPrefWidth(360);
        searchWrapper.setMaxWidth(360);

        Label searchIcon = new Label("⌕");
        searchIcon.getStyleClass().add("employee-search-icon");

        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.getStyleClass().add("employee-hero-search-field");

        searchWrapper.getChildren().addAll(searchIcon, searchField);

        VBox right = new VBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPadding(new Insets(0, 36, 0, 0));
        right.getChildren().add(searchWrapper);

        hero.getChildren().addAll(left, right);

        return hero;
    }

    private void displayUpdateEmployee(Employee emp) {
        VBox mainContent = new VBox(20);
        mainContent.getStyleClass().add("content-page");
        mainContent.setPadding(new Insets(30));

        Label title = new Label("Update Employee Record");
        title.getStyleClass().add("page-title");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.getStyleClass().add("card");

        TextField empIdField = new TextField(emp.getEmpId());
        empIdField.setDisable(true);

        TextField nameField = new TextField(emp.getName());
        TextField fnameField = new TextField(emp.getFname());
        DatePicker dobField = new DatePicker(emp.getDob().toLocalDate());
        TextField salaryField = new TextField(emp.getSalary().toString());
        TextField addressField = new TextField(emp.getAddress());
        TextField phoneField = new TextField(emp.getPhone());
        TextField emailField = new TextField(emp.getEmail());
        TextField educationField = new TextField(emp.getEducation());
        TextField designationField = new TextField(emp.getDesignation());
        TextField aadharField = new TextField(emp.getAadhar());

        form.add(new Label("Employee ID (Locked):"), 0, 0);
        form.add(empIdField, 1, 0);
        form.add(new Label("Full Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Father's Name:"), 0, 2);
        form.add(fnameField, 1, 2);
        form.add(new Label("Date of Birth:"), 0, 3);
        form.add(dobField, 1, 3);
        form.add(new Label("Salary:"), 0, 4);
        form.add(salaryField, 1, 4);
        form.add(new Label("Address:"), 0, 5);
        form.add(addressField, 1, 5);
        form.add(new Label("Phone:"), 0, 6);
        form.add(phoneField, 1, 6);
        form.add(new Label("Email:"), 0, 7);
        form.add(emailField, 1, 7);
        form.add(new Label("Education:"), 0, 8);
        form.add(educationField, 1, 8);
        form.add(new Label("Designation:"), 0, 9);
        form.add(designationField, 1, 9);
        form.add(new Label("Aadhar:"), 0, 10);
        form.add(aadharField, 1, 10);

        Label messageLabel = new Label();

        Button submitBtn = new Button("Save Changes");
        submitBtn.getStyleClass().add("btn-warning");

        Button cancelBtn = new Button("Cancel");

        cancelBtn.setOnAction(e -> {
            displayEmployees();
            setActiveMenu(btnView);
        });

        submitBtn.setOnAction(e -> {
            try {
                if (dobField.getValue() == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("Please select a valid Date of Birth.");
                    return;
                }

                String salaryText = salaryField.getText().trim();

                if (salaryText.isEmpty() || !salaryText.matches("\\d+(\\.\\d+)?")) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("Salary must be a valid number.");
                    return;
                }

                Employee updatedEmp = new Employee(
                        empIdField.getText().trim(),
                        nameField.getText().trim(),
                        fnameField.getText().trim(),
                        Date.valueOf(dobField.getValue()),
                        new BigDecimal(salaryText),
                        addressField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        educationField.getText().trim(),
                        designationField.getText().trim(),
                        aadharField.getText().replaceAll("\\s+", "")
                );

                if (employeeDAO.updateEmployee(updatedEmp)) {
                    displayEmployees();
                    setActiveMenu(btnView);
                } else {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("Failed to update employee.");
                }
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("System Error: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(15);
        buttonBox.getChildren().addAll(submitBtn, cancelBtn);

        form.add(buttonBox, 1, 11);
        form.add(messageLabel, 1, 12);

        mainContent.getChildren().addAll(title, form);

        setCenterPage(mainContent);
    }

    private void displayAnalytics() {
        VBox mainContent = new VBox(20);
        mainContent.getStyleClass().add("content-page");
        mainContent.setPadding(new Insets(30));

        VBox headerBox = new VBox(5);

        Label title = new Label("System Analytics Overview");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Workforce distribution & compensation insights");
        subtitle.getStyleClass().add("page-subtitle");

        headerBox.getChildren().addAll(title, subtitle);

        List<Employee> employees = employeeDAO.getAllEmployees();

        if (employees.isEmpty()) {
            mainContent.getChildren().addAll(headerBox, new Label("Insufficient data to generate analytics."));
            setCenterPage(mainContent);
            return;
        }

        int totalEmployees = employees.size();

        double avgSalary = employees.stream()
                .mapToDouble(e -> e.getSalary().doubleValue())
                .average()
                .orElse(0.0);

        double maxSalary = employees.stream()
                .mapToDouble(e -> e.getSalary().doubleValue())
                .max()
                .orElse(0.0);

        long uniqueRoles = employees.stream()
                .map(Employee::getDesignation)
                .distinct()
                .count();

        HBox kpiRow = new HBox(18);
        kpiRow.getChildren().addAll(
                createKPICard("TOTAL EMPLOYEES", String.valueOf(totalEmployees), "Across all departments", "border-orange"),
                createKPICard("AVG. SALARY", formatCurrency(avgSalary), "Per month, all roles", "border-blue"),
                createKPICard("HIGHEST PAY", formatCurrency(maxSalary), "Top earner", "border-green"),
                createKPICard("DESIGNATIONS", String.valueOf(uniqueRoles), "Unique roles active", "border-red")
        );

        GridPane chartsPane = new GridPane();
        chartsPane.setHgap(20);
        chartsPane.setVgap(20);

        PieChart designationChart = new PieChart();

        Map<String, Long> designationCounts = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDesignation, Collectors.counting()));

        for (Map.Entry<String, Long> entry : designationCounts.entrySet()) {
            designationChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Designation");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average Salary");

        BarChart<String, Number> salaryChart = new BarChart<>(xAxis, yAxis);
        salaryChart.setLegendVisible(false);

        Map<String, Double> averageSalaries = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDesignation,
                        Collectors.averagingDouble(emp -> emp.getSalary().doubleValue())
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Double> entry : averageSalaries.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        salaryChart.getData().add(series);

        VBox pieCard = createChartCard("📊 Workforce Distribution", designationChart);
        VBox barCard = createChartCard("📊 Average Compensation by Designation", salaryChart);

        pieCard.setPrefSize(400, 360);
        barCard.setPrefSize(560, 360);

        chartsPane.add(pieCard, 0, 0);
        chartsPane.add(barCard, 1, 0);

        mainContent.getChildren().addAll(headerBox, kpiRow, chartsPane);

        setCenterPage(mainContent);
    }

    private String formatCurrency(double amount) {
        if (amount >= 100000) {
            return String.format("₹%.1fL", amount / 100000.0);
        } else if (amount >= 1000) {
            return String.format("₹%.1fK", amount / 1000.0);
        } else {
            return String.format("₹%.0f", amount);
        }
    }

    private VBox createKPICard(String titleText, String valueText, String subtitleText, String borderClass) {
        VBox card = new VBox();
        card.getStyleClass().addAll("kpi-card", borderClass);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.getStyleClass().add("kpi-title");

        Label value = new Label(valueText);
        value.getStyleClass().add("kpi-value");

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("kpi-subtitle");

        card.getChildren().addAll(title, value, subtitle);

        return card;
    }

    private VBox createChartCard(String titleText, Node chartNode) {
        VBox card = new VBox();
        card.getStyleClass().add("chart-card");

        HBox header = new HBox();
        header.getStyleClass().add("chart-header");

        Label title = new Label(titleText);
        title.getStyleClass().add("chart-header-title");

        header.getChildren().add(title);

        VBox chartWrapper = new VBox(chartNode);
        chartWrapper.setPadding(new Insets(15));

        VBox.setVgrow(chartWrapper, Priority.ALWAYS);
        VBox.setVgrow(chartNode, Priority.ALWAYS);

        card.getChildren().addAll(header, chartWrapper);

        return card;
    }
}