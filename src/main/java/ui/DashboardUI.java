package ui;

import dao.EmployeeDAO;
import model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardUI {

    private BorderPane root;
    private EmployeeDAO employeeDAO = new EmployeeDAO();

    private Button btnHome;
    private Button btnAnalytics;
    private Button btnView;
    private Button btnAdd;

    public void start(Stage dashboardStage) {
        dashboardStage.setTitle("Employee Management System - Dashboard");
        root = new BorderPane();
        VBox sideMenu = createSideMenu(dashboardStage);
        root.setLeft(sideMenu);

        displayHomeGrid();
        setActiveMenu(btnHome);

        Scene scene = new Scene(root, 1100, 650);

        try {
            String css = this.getClass().getResource("modern-theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("WARNING: modern-theme.css not found.");
        }

        dashboardStage.setScene(scene);
        dashboardStage.show();
    }

    private VBox createSideMenu(Stage stage) {
        VBox menu = new VBox();
        menu.getStyleClass().add("sidebar");
        menu.setPrefWidth(220);

        VBox profileSection = new VBox(8);
        profileSection.getStyleClass().add("sidebar-profile");

        StackPane avatarContainer = new StackPane();
        avatarContainer.setAlignment(Pos.CENTER);
        Circle avatarCircle = new Circle(35);
        avatarCircle.getStyleClass().add("avatar-placeholder");
        Text avatarText = new Text("A");
        avatarText.getStyleClass().add("avatar-text");
        avatarContainer.getChildren().addAll(avatarCircle, avatarText);

        Label adminName = new Label("System Admin");
        adminName.getStyleClass().add("profile-name");
        Label adminRole = new Label("Administrator");
        adminRole.getStyleClass().add("profile-role");

        profileSection.getChildren().addAll(avatarContainer, adminName, adminRole);

        btnHome = new Button("🏠 Home Menu");
        btnHome.setMaxWidth(Double.MAX_VALUE);
        btnHome.setOnAction(e -> {
            displayHomeGrid();
            setActiveMenu(btnHome);
        });

        btnAnalytics = new Button("Dashboard Analytics");
        btnView = new Button("View Employees");
        btnAdd = new Button("Add Employee");

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> stage.close());

        menu.getChildren().addAll(profileSection, btnHome, logoutBtn);
        return menu;
    }

    private void setActiveMenu(Button activeBtn) {
        btnHome.getStyleClass().remove("active-menu");
        btnAnalytics.getStyleClass().remove("active-menu");
        btnView.getStyleClass().remove("active-menu");
        btnAdd.getStyleClass().remove("active-menu");

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active-menu");
        }
    }

    private void displayHomeGrid() {
        VBox homeLayout = new VBox(20);
        homeLayout.setPadding(new Insets(25, 40, 20, 40));
        homeLayout.setAlignment(Pos.TOP_CENTER);

        VBox banner = new VBox(8);
        banner.getStyleClass().add("welcome-banner");
        banner.setPadding(new Insets(20, 30, 20, 30));
        banner.setMaxWidth(800);

        Label badge = new Label("✦ WELCOME BACK, ADMIN");
        badge.getStyleClass().add("welcome-badge");

        TextFlow titleFlow = new TextFlow();
        Text t1 = new Text("Welcome to ");
        t1.getStyleClass().add("welcome-title");
        Text t2 = new Text("Smart Employee\n");
        t2.getStyleClass().add("welcome-title-highlight");
        Text t3 = new Text("Management System");
        t3.getStyleClass().add("welcome-title");
        titleFlow.getChildren().addAll(t1, t2, t3);

        Label subtitle = new Label("Simplifying Workforce, Empowering Growth");
        subtitle.getStyleClass().add("welcome-subtitle");

        banner.getChildren().addAll(badge, titleFlow, subtitle);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(25);
        grid.setVgap(20);
        grid.getStyleClass().add("landing-grid");

        VBox analyticsCard = createMenuCard("📈 Dashboard Analytics",
                "View workforce distribution and compensation analytics with insightful visualizations.",
                "Open Dashboard →", true, () -> {
                    displayAnalytics();
                    setActiveMenu(btnAnalytics);
                });
        VBox viewCard = createMenuCard("👥 View Employees", "Search, browse, and manage employee records efficiently.",
                "Open Records →", true, () -> {
                    displayEmployees();
                    setActiveMenu(btnView);
                });
        VBox addCard = createMenuCard("👤 Add Employee", "Quickly add new employees to the system with a simple form.",
                "Open Form →", true, () -> {
                    displayAddEmployee();
                    setActiveMenu(btnAdd);
                });
        VBox logoutCard = createMenuCard("🚪 Logout", "Sign out of the system securely.", "Logout", true,
                () -> System.exit(0));

        grid.add(analyticsCard, 0, 0);
        grid.add(viewCard, 1, 0);
        grid.add(addCard, 0, 1);
        grid.add(logoutCard, 1, 1);

        homeLayout.getChildren().addAll(banner, grid);
        root.setCenter(homeLayout);
    }

    private VBox createMenuCard(String titleText, String descText, String btnText, boolean isPrimary, Runnable action) {
        VBox card = new VBox();
        card.getStyleClass().add("landing-card");
        card.setPrefSize(380, 175);

        Label title = new Label(titleText);
        title.getStyleClass().add("landing-card-title");

        Label desc = new Label(descText);
        desc.getStyleClass().add("landing-card-desc");
        desc.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(desc, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.getStyleClass().add(isPrimary ? "landing-button-primary" : "landing-button-secondary");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(title, desc, actionBtn);
        return card;
    }

    private void displayAnalytics() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20, 30, 20, 30)); // Slightly tightened top padding to fit KPIs

        // 1. Page Header
        VBox headerBox = new VBox(5);
        Label title = new Label("System Analytics Overview");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Workforce distribution & compensation insights");
        subtitle.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(title, subtitle);

        List<Employee> employees = employeeDAO.getAllEmployees();

        if (employees.isEmpty()) {
            mainContent.getChildren().addAll(headerBox, new Label("Insufficient data to generate analytics."));
            root.setCenter(mainContent);
            return;
        }

        // 2. Data Calculation for KPIs using Streams
        int totalEmployees = employees.size();

        double avgSalary = employees.stream()
                .mapToDouble(e -> e.getSalary().doubleValue())
                .average().orElse(0.0);

        double maxSalary = employees.stream()
                .mapToDouble(e -> e.getSalary().doubleValue())
                .max().orElse(0.0);

        long uniqueRoles = employees.stream()
                .map(Employee::getDesignation)
                .distinct()
                .count();

        // 3. Build KPI Row
        HBox kpiRow = new HBox(20);
        kpiRow.getChildren().addAll(
                createKPICard("TOTAL EMPLOYEES", String.valueOf(totalEmployees), "Across all departments",
                        "border-orange"),
                createKPICard("AVG. SALARY", formatCurrency(avgSalary), "Per month, all roles", "border-blue"),
                createKPICard("HIGHEST PAY", formatCurrency(maxSalary), "Top earner", "border-green"),
                createKPICard("DESIGNATIONS", String.valueOf(uniqueRoles), "Unique roles active", "border-red"));

        // 4. Build Charts
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
                        Collectors.averagingDouble(emp -> emp.getSalary().doubleValue())));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : averageSalaries.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        salaryChart.getData().add(series);

        VBox pieCard = createChartCard("📊 Workforce Distribution", designationChart);
        VBox barCard = createChartCard("📊 Average Compensation by Designation", salaryChart);

        pieCard.setPrefSize(400, 360); // Adjusted height to make room for KPIs
        barCard.setPrefSize(500, 360);

        chartsPane.add(pieCard, 0, 0);
        chartsPane.add(barCard, 1, 0);

        mainContent.getChildren().addAll(headerBox, kpiRow, chartsPane);
        root.setCenter(mainContent);
    }

    // NEW: Helper method to format large currency numbers (K and L)
    private String formatCurrency(double amount) {
        if (amount >= 100000) {
            return String.format("₹%.1fL", amount / 100000.0);
        } else if (amount >= 1000) {
            return String.format("₹%.1fK", amount / 1000.0);
        } else {
            return String.format("₹%.0f", amount);
        }
    }

    // NEW: Helper method to construct individual KPI cards
    private VBox createKPICard(String titleText, String valueText, String subtitleText, String borderClass) {
        VBox card = new VBox();
        card.getStyleClass().addAll("kpi-card", borderClass);
        HBox.setHgrow(card, Priority.ALWAYS); // Ensure cards stretch evenly

        Label title = new Label(titleText);
        title.getStyleClass().add("kpi-title");

        Label value = new Label(valueText);
        value.getStyleClass().add("kpi-value");

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("kpi-subtitle");

        card.getChildren().addAll(title, value, subtitle);
        return card;
    }

    private VBox createChartCard(String titleText, javafx.scene.Node chartNode) {
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

    private void displayEmployees() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(25));

        HBox topHeader = new HBox(20);
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Employee Directory");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by ID or Name...");
        searchField.setPrefWidth(300);

        topHeader.getChildren().addAll(title, spacer, searchField);

        TableView<Employee> table = new TableView<>();

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

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        table.getColumns().setAll(empIdCol, nameCol, phoneCol, emailCol, designationCol, salaryCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<Employee> employees = employeeDAO.getAllEmployees();
        ObservableList<Employee> data = FXCollections.observableArrayList(employees);

        FilteredList<Employee> filteredData = new FilteredList<>(data, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (employee.getEmpId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Employee> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_RIGHT);

        Button btnUpdate = new Button("Update Selected");
        btnUpdate.getStyleClass().add("btn-warning");

        Button btnDelete = new Button("Delete Selected");
        btnDelete.getStyleClass().add("btn-danger");

        Label actionStatus = new Label();
        actionStatus.setPadding(new Insets(0, 15, 0, 0));

        controls.getChildren().addAll(actionStatus, btnUpdate, btnDelete);

        btnDelete.setOnAction(e -> {
            Employee selectedEmp = table.getSelectionModel().getSelectedItem();
            if (selectedEmp != null) {
                if (employeeDAO.deleteEmployee(selectedEmp.getEmpId())) {
                    data.remove(selectedEmp);
                    actionStatus.setTextFill(Color.GREEN);
                    actionStatus.setText("Employee ID " + selectedEmp.getEmpId() + " deleted.");
                } else {
                    actionStatus.setTextFill(Color.RED);
                    actionStatus.setText("Database error. Could not delete.");
                }
            } else {
                actionStatus.setTextFill(Color.RED);
                actionStatus.setText("Please select an employee to delete.");
            }
        });

        btnUpdate.setOnAction(e -> {
            Employee selectedEmp = table.getSelectionModel().getSelectedItem();
            if (selectedEmp != null) {
                displayUpdateEmployee(selectedEmp);
            } else {
                actionStatus.setTextFill(Color.RED);
                actionStatus.setText("Please select an employee to update.");
            }
        });

        mainContent.getChildren().addAll(topHeader, table, controls);
        root.setCenter(mainContent);
    }

    private void displayUpdateEmployee(Employee emp) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        Label title = new Label("Update Employee Record");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

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
                        empIdField.getText().trim(), nameField.getText().trim(), fnameField.getText().trim(),
                        Date.valueOf(dobField.getValue()), new BigDecimal(salaryText), addressField.getText().trim(),
                        phoneField.getText().trim(), emailField.getText().trim(), educationField.getText().trim(),
                        designationField.getText().trim(), aadharField.getText().replaceAll("\\s+", ""));

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
        root.setCenter(mainContent);
    }

    private void displayAddEmployee() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));

        Label title = new Label("Add New Employee");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.getStyleClass().add("card");

        TextField empIdField = new TextField();
        empIdField.setPromptText("Employee ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField fnameField = new TextField();
        fnameField.setPromptText("Father's Name");
        DatePicker dobField = new DatePicker();
        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField educationField = new TextField();
        educationField.setPromptText("Education");
        TextField designationField = new TextField();
        designationField.setPromptText("Designation");
        TextField aadharField = new TextField();
        aadharField.setPromptText("Aadhar Number");

        form.add(new Label("Employee ID:"), 0, 0);
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

        Button submitBtn = new Button("Add Employee");
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

                Employee emp = new Employee(
                        empIdField.getText().trim(), nameField.getText().trim(), fnameField.getText().trim(),
                        Date.valueOf(dobField.getValue()), new BigDecimal(salaryText), addressField.getText().trim(),
                        phoneField.getText().trim(), emailField.getText().trim(), educationField.getText().trim(),
                        designationField.getText().trim(), aadharField.getText().replaceAll("\\s+", ""));

                if (employeeDAO.addEmployee(emp)) {
                    messageLabel.setTextFill(Color.GREEN);
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
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("Failed to add employee. Check duplicate ID.");
                }
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("System Error: " + ex.getMessage());
            }
        });

        form.add(submitBtn, 1, 11);
        form.add(messageLabel, 1, 12);

        mainContent.getChildren().addAll(title, form);
        root.setCenter(mainContent);
    }
}