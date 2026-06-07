package ui;

import dao.AttendanceDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AttendanceUI {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public VBox getAttendanceView(int employeeId) {
        VBox page = new VBox(16);
        page.getStyleClass().add("dashboard-page");
        page.setPadding(new Insets(14, 20, 18, 20));

        page.getChildren().add(createSimpleTopBar());
        page.getChildren().add(createPageHero("◷", "Today ", "Attendance",
                "Mark daily attendance with present, half-day, or work-from-home status."));

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("premium-form-card");
        formCard.setPadding(new Insets(24, 28, 26, 28));

        HBox contentRow = new HBox(24);
        contentRow.setAlignment(Pos.TOP_CENTER);

        VBox leftInfo = new VBox(12);
        leftInfo.getStyleClass().add("info-panel");
        leftInfo.setPrefWidth(340);

        Label infoIcon = new Label("◷");
        infoIcon.getStyleClass().add("info-panel-icon");

        Label infoTitle = new Label("Daily Attendance");
        infoTitle.getStyleClass().add("info-panel-title");

        Label infoText = new Label("Select today’s attendance status and add optional remarks. Once marked, attendance cannot be marked again for the same day.");
        infoText.getStyleClass().add("info-panel-text");
        infoText.setWrapText(true);

        Label tip = new Label("Tip: Use remarks for late check-in, WFH reason, or half-day note.");
        tip.getStyleClass().add("info-panel-tip");
        tip.setWrapText(true);

        leftInfo.getChildren().addAll(infoIcon, infoTitle, infoText, tip);

        VBox formBox = new VBox(14);
        HBox.setHgrow(formBox, Priority.ALWAYS);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Present", "Half Day", "Work From Home");
        statusBox.setPromptText("Select Attendance Status");

        TextArea remarksArea = new TextArea();
        remarksArea.setPromptText("Remarks optional");
        remarksArea.setPrefHeight(120);

        VBox statusField = createComboFormField("Attendance Status", "✓", statusBox);
        VBox remarksField = createTextAreaFormField("Remarks", "✎", remarksArea);

        Label message = new Label();
        message.getStyleClass().add("form-message-label");

        Button resetButton = new Button("↻  Reset");
        resetButton.getStyleClass().add("add-reset-button");

        Button markButton = new Button("⊕  Mark Attendance");
        markButton.getStyleClass().add("add-submit-button");

        if (attendanceDAO.hasMarkedToday(employeeId)) {
            markButton.setDisable(true);
            message.setStyle("-fx-text-fill: #16a34a;");
            message.setText("Attendance already marked for today.");
        }

        resetButton.setOnAction(e -> {
            statusBox.setValue(null);
            remarksArea.clear();
            if (!markButton.isDisabled()) {
                message.setText("");
            }
        });

        markButton.setOnAction(e -> {
            String status = statusBox.getValue();
            String remarks = remarksArea.getText();

            if (status == null || status.isEmpty()) {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Please select attendance status.");
                return;
            }

            boolean success = attendanceDAO.markAttendance(employeeId, status, remarks);

            if (success) {
                message.setStyle("-fx-text-fill: #16a34a;");
                message.setText("Attendance marked successfully.");
                markButton.setDisable(true);
            } else {
                message.setStyle("-fx-text-fill: #dc2626;");
                message.setText("Attendance already marked or failed.");
            }
        });

        HBox actionBox = new HBox(14);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(resetButton, markButton);

        formBox.getChildren().addAll(statusField, remarksField, message, actionBox);
        contentRow.getChildren().addAll(leftInfo, formBox);

        formCard.getChildren().add(contentRow);
        page.getChildren().add(formCard);

        Region bottomSpace = new Region();
        bottomSpace.getStyleClass().add("bottom-safe-space");
        page.getChildren().add(bottomSpace);

        return page;
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

    private VBox createComboFormField(String labelText, String iconText, ComboBox<String> comboBox) {
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