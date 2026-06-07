package ui;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.LeaveDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.LeaveRequest;
import utils.DBConnection;

public class LeaveManagementUI {

    private final LeaveDAO leaveDAO = new LeaveDAO();

    public VBox getLeaveApplyView(int defaultEmployeeId) {
        VBox page = new VBox(16);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(14, 20, 18, 20));

        page.getChildren().add(createSimpleTopBar());
        page.getChildren().add(createPageHero(
                "▣",
                "Apply ",
                "Leave",
                "Submit leave request for any employee after employee request to admin."
        ));

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("premium-form-card");
        formCard.setPadding(new Insets(24, 28, 26, 28));

        HBox contentRow = new HBox(24);
        contentRow.setAlignment(Pos.TOP_CENTER);

        VBox leftInfo = createInfoPanel(
                "▣",
                "Leave Request",
                "Select employee, leave type, date range, and reason. Request will be submitted as Pending until admin approval.",
                "Tip: Admin can apply leave on behalf of an employee if the employee requested it."
        );

        VBox formBox = new VBox(14);
        HBox.setHgrow(formBox, Priority.ALWAYS);

        ComboBox<EmployeeOption> employeeBox = new ComboBox<>();
        employeeBox.setPromptText("Select Employee");
        employeeBox.getItems().setAll(loadEmployeeOptions());

        for (EmployeeOption option : employeeBox.getItems()) {
            if (option.employeeId == defaultEmployeeId) {
                employeeBox.setValue(option);
                break;
            }
        }

        ComboBox<String> leaveTypeBox = new ComboBox<>();
        leaveTypeBox.getItems().addAll("Casual Leave", "Sick Leave", "Emergency Leave", "Paid Leave");
        leaveTypeBox.setPromptText("Select Leave Type");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for leave");
        reasonArea.setPrefHeight(110);

        VBox employeeField = createComboFormField("Employee", "👤", employeeBox);
        VBox leaveTypeField = createComboFormField("Leave Type", "✓", leaveTypeBox);
        VBox startDateField = createDateFormField("Start Date", "🗓", startDatePicker);
        VBox endDateField = createDateFormField("End Date", "🗓", endDatePicker);
        VBox reasonField = createTextAreaFormField("Reason", "✎", reasonArea);

        Label message = new Label();
        message.getStyleClass().add("form-message-label");

        Button resetButton = new Button("↻  Reset");
        resetButton.getStyleClass().add("add-reset-button");

        Button applyButton = new Button("⊕  Apply Leave");
        applyButton.getStyleClass().add("add-submit-button");

        if (employeeBox.getItems().isEmpty()) {
            message.setStyle("-fx-text-fill: #dc2626;");
            message.setText("No employees found. Please add employees first.");
            applyButton.setDisable(true);
        }

        resetButton.setOnAction(e -> {
            employeeBox.setValue(null);
            leaveTypeBox.setValue(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            reasonArea.clear();
            message.setText("");
            message.setStyle("");
        });

        applyButton.setOnAction(e -> {
            EmployeeOption selectedEmployee = employeeBox.getValue();

            if (selectedEmployee == null ||
                    leaveTypeBox.getValue() == null ||
                    startDatePicker.getValue() == null ||
                    endDatePicker.getValue() == null ||
                    reasonArea.getText().trim().isEmpty()) {

                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Please select employee and fill all leave details.");
                return;
            }

            if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("End date cannot be before start date.");
                return;
            }

            boolean success = leaveDAO.applyLeave(
                    selectedEmployee.employeeId,
                    leaveTypeBox.getValue(),
                    Date.valueOf(startDatePicker.getValue()),
                    Date.valueOf(endDatePicker.getValue()),
                    reasonArea.getText().trim()
            );

            if (success) {
                message.setStyle("-fx-text-fill: #16a34a;");
                message.setText("Leave applied successfully for " + selectedEmployee.name + ". Status: Pending.");

                leaveTypeBox.setValue(null);
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                reasonArea.clear();
            } else {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Failed to apply leave. Please check database connection.");
            }
        });

        HBox actionBox = new HBox(14);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(resetButton, applyButton);

        formBox.getChildren().addAll(
                employeeField,
                leaveTypeField,
                startDateField,
                endDateField,
                reasonField,
                message,
                actionBox
        );

        contentRow.getChildren().addAll(leftInfo, formBox);
        formCard.getChildren().add(contentRow);
        page.getChildren().add(formCard);

        Region bottomSpace = new Region();
        bottomSpace.getStyleClass().add("bottom-safe-space");
        page.getChildren().add(bottomSpace);

        return page;
    }

    public VBox getAdminLeaveApprovalView(int adminId) {
        VBox page = new VBox(16);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(14, 20, 18, 20));

        page.getChildren().add(createSimpleTopBar());
        page.getChildren().add(createPageHero(
                "✓",
                "Leave ",
                "Approval",
                "Review pending leave requests and approve or reject them with admin comments."
        ));

        VBox tableCard = new VBox(14);
        tableCard.getStyleClass().add("premium-form-card");
        tableCard.setPadding(new Insets(22, 24, 24, 24));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Pending / Submitted Leave Requests");
        title.getStyleClass().add("section-title");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button refreshButton = new Button("↻  Refresh");
        refreshButton.getStyleClass().add("add-reset-button");

        header.getChildren().addAll(title, headerSpacer, refreshButton);

        TableView<LeaveRequest> table = new TableView<>();
        table.getStyleClass().add("premium-table");

        TableColumn<LeaveRequest, Integer> idCol = new TableColumn<>("Leave ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLeaveId()).asObject());

        TableColumn<LeaveRequest, Integer> empCol = new TableColumn<>("Employee ID");
        empCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEmployeeId()).asObject());

        TableColumn<LeaveRequest, String> typeCol = new TableColumn<>("Leave Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLeaveType()));

        TableColumn<LeaveRequest, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStartDate())));

        TableColumn<LeaveRequest, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEndDate())));

        TableColumn<LeaveRequest, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReason()));

        TableColumn<LeaveRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        table.getColumns().setAll(idCol, empCol, typeCol, startCol, endCol, reasonCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Admin comment optional");
        commentArea.setPrefHeight(78);
        commentArea.getStyleClass().add("premium-comment-area");

        Label message = new Label();
        message.getStyleClass().add("form-message-label");

        Button approveButton = new Button("✓  Approve");
        approveButton.getStyleClass().add("approve-button");

        Button rejectButton = new Button("✕  Reject");
        rejectButton.getStyleClass().add("reject-button");

        HBox actions = new HBox(14);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getChildren().addAll(approveButton, rejectButton);

        Runnable loadLeaves = () -> {
            try {
                List<LeaveRequest> leaves = leaveDAO.getAllLeaves();
                table.getItems().setAll(leaves);
            } catch (Exception ex) {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Could not load leaves: " + ex.getMessage());
            }
        };

        refreshButton.setOnAction(e -> loadLeaves.run());

        approveButton.setOnAction(e -> {
            LeaveRequest selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Please select a leave request.");
                return;
            }

            boolean ok = leaveDAO.updateLeaveStatus(
                    selected.getLeaveId(),
                    "Approved",
                    adminId,
                    commentArea.getText().trim()
            );

            if (ok) {
                message.setStyle("-fx-text-fill: #16a34a;");
                message.setText("Leave approved successfully.");
                commentArea.clear();
                loadLeaves.run();
            } else {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Failed to approve leave.");
            }
        });

        rejectButton.setOnAction(e -> {
            LeaveRequest selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Please select a leave request.");
                return;
            }

            boolean ok = leaveDAO.updateLeaveStatus(
                    selected.getLeaveId(),
                    "Rejected",
                    adminId,
                    commentArea.getText().trim()
            );

            if (ok) {
                message.setStyle("-fx-text-fill: #16a34a;");
                message.setText("Leave rejected successfully.");
                commentArea.clear();
                loadLeaves.run();
            } else {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Failed to reject leave.");
            }
        });

        loadLeaves.run();

        tableCard.getChildren().addAll(header, table, commentArea, message, actions);
        page.getChildren().add(tableCard);

        Region bottomSpace = new Region();
        bottomSpace.getStyleClass().add("bottom-safe-space");
        page.getChildren().add(bottomSpace);

        return page;
    }

    private List<EmployeeOption> loadEmployeeOptions() {
        List<EmployeeOption> employees = new ArrayList<>();

        String sql = """
                SELECT employee_id, emp_id, name
                FROM employees
                ORDER BY employee_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(new EmployeeOption(
                        rs.getInt("employee_id"),
                        rs.getString("emp_id"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            System.err.println("Could not load employees for leave apply: " + e.getMessage());
        }

        return employees;
    }

    private static class EmployeeOption {
        private final int employeeId;
        private final String empCode;
        private final String name;

        private EmployeeOption(int employeeId, String empCode, String name) {
            this.employeeId = employeeId;
            this.empCode = empCode;
            this.name = name;
        }

        @Override
        public String toString() {
            return empCode + " - " + name;
        }
    }

    private VBox createInfoPanel(String icon, String title, String body, String tipText) {
        VBox leftInfo = new VBox(12);
        leftInfo.getStyleClass().add("info-panel");
        leftInfo.setPrefWidth(340);

        Label infoIcon = new Label(icon);
        infoIcon.getStyleClass().add("info-panel-icon");

        Label infoTitle = new Label(title);
        infoTitle.getStyleClass().add("info-panel-title");

        Label infoText = new Label(body);
        infoText.getStyleClass().add("info-panel-text");
        infoText.setWrapText(true);

        Label tip = new Label(tipText);
        tip.getStyleClass().add("info-panel-tip");
        tip.setWrapText(true);

        leftInfo.getChildren().addAll(infoIcon, infoTitle, infoText, tip);
        return leftInfo;
    }

    private HBox createSimpleTopBar() {
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.getStyleClass().add("top-bar");
        topBar.setMinHeight(36);
        topBar.setPrefHeight(36);
        topBar.setMaxHeight(36);

        Label search = new Label("⌕");
        search.getStyleClass().add("top-icon-button");

        Label bell = new Label("●");
        bell.getStyleClass().add("top-icon-button");

        Label theme = new Label("☼");
        theme.getStyleClass().add("top-icon-button");

        topBar.getChildren().addAll(search, bell, theme);
        return topBar;
    }

    private HBox createPageHero(String icon, String titleDark, String titleOrange, String subtitleText) {
        HBox hero = new HBox();
        hero.getStyleClass().add("add-hero-banner");
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setMinHeight(150);
        hero.setPrefHeight(150);
        hero.setMaxHeight(150);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        clip.widthProperty().bind(hero.widthProperty());
        clip.heightProperty().bind(hero.heightProperty());
        hero.setClip(clip);

        HBox left = new HBox(18);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(20, 28, 20, 26));

        Label iconBadge = new Label(icon);
        iconBadge.getStyleClass().add("add-hero-icon-badge");

        VBox textBox = new VBox(10);
        textBox.setAlignment(Pos.CENTER_LEFT);

        TextFlow titleFlow = new TextFlow();

        Text t1 = new Text(titleDark);
        t1.getStyleClass().add("add-hero-title-dark");

        Text t2 = new Text(titleOrange);
        t2.getStyleClass().add("add-hero-title-orange");

        titleFlow.getChildren().addAll(t1, t2);

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("add-hero-subtitle");

        Label accent = new Label("━━━━");
        accent.getStyleClass().add("hero-accent-line");

        textBox.getChildren().addAll(titleFlow, subtitle, accent);
        left.getChildren().addAll(iconBadge, textBox);
        hero.getChildren().add(left);

        return hero;
    }

    private VBox createComboFormField(String labelText, String iconText, ComboBox<?> comboBox) {
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

        HBox.setHgrow(comboBox, Priority.ALWAYS);
        comboBox.getStyleClass().add("icon-combo-box");

        inputWrap.getChildren().addAll(icon, divider, comboBox);
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

    private VBox createTextAreaFormField(String labelText, String iconText, TextArea area) {
        VBox box = new VBox(7);
        box.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("add-form-label");

        HBox inputWrap = new HBox(10);
        inputWrap.setAlignment(Pos.TOP_LEFT);
        inputWrap.getStyleClass().add("icon-textarea-wrapper");

        Label icon = new Label(iconText);
        icon.getStyleClass().add("input-icon");

        Region divider = new Region();
        divider.getStyleClass().add("input-divider");
        divider.setPrefWidth(1);
        divider.setMinWidth(1);
        divider.setMaxWidth(1);

        HBox.setHgrow(area, Priority.ALWAYS);
        area.getStyleClass().add("icon-text-area");

        inputWrap.getChildren().addAll(icon, divider, area);
        box.getChildren().addAll(label, inputWrap);

        return box;
    }
}