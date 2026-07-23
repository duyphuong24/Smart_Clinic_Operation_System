package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.service.QueueDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.AppointmentUiUtil;
import com.smartclinic.desktop.util.BookAppointmentDialog;
import com.smartclinic.desktop.util.CheckInDialog;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;

public class TodayAppointmentsController implements NavigationAware {

    private final AppointmentDesktopService appointmentService;
    private final QueueDesktopService queueService;

    private final List<AppointmentResponse> loadedAppointments = new ArrayList<>();

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private TextField keywordField;

    @FXML
    private TableView<AppointmentResponse> appointmentTable;

    @FXML
    private TableColumn<AppointmentResponse, String> codeColumn;

    @FXML
    private TableColumn<AppointmentResponse, String> patientColumn;

    @FXML
    private TableColumn<AppointmentResponse, String> doctorColumn;

    @FXML
    private TableColumn<AppointmentResponse, String> roomColumn;

    @FXML
    private TableColumn<AppointmentResponse, String> timeColumn;

    @FXML
    private TableColumn<AppointmentResponse, String> statusColumn;

    @FXML
    private TableColumn<AppointmentResponse, AppointmentResponse> actionsColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button checkInButton;

    @FXML
    private Button bookAppointmentButton;

    @FXML
    private Button todayButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label emptyLabel;

    public TodayAppointmentsController(
            AppointmentDesktopService appointmentService,
            QueueDesktopService queueService
    ) {
        this.appointmentService = appointmentService;
        this.queueService = queueService;
    }

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        configureActions();
        datePicker.setValue(LocalDate.now());
    }

    @Override
    public void onNavigate() {
        if (datePicker.getValue() == null) {
            datePicker.setValue(LocalDate.now());
        }
        loadAppointments();
    }

    @FXML
    private void onRefresh() {
        loadAppointments();
    }

    @FXML
    private void onToday() {
        datePicker.setValue(LocalDate.now());
        loadAppointments();
    }

    @FXML
    private void onApplyFilters() {
        applyFilters();
    }

    @FXML
    private void onBookAppointment() {
        var owner = bookAppointmentButton.getScene() == null ? null : bookAppointmentButton.getScene().getWindow();
        BookAppointmentDialog.show(queueService, appointmentService, owner)
                .ifPresent(created -> {
                    AlertUtil.showInfo("Book Appointment", "Appointment booked successfully: " + created.getAppointmentCode());
                    loadAppointments();
                });
    }

    @FXML
    private void onCheckIn() {
        AppointmentResponse selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Check-in", "Please select an appointment first.");
            return;
        }
        if (!selected.isCheckInAllowed()) {
            AlertUtil.showWarning("Check-in", "Only PENDING/CONFIRMED appointments can be checked in.");
            return;
        }

        CheckInDialog.show(selected, checkInButton.getScene().getWindow())
                .ifPresent(result -> performCheckIn(selected, result.priority()));
    }

    private void configureFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL",
                "PENDING",
                "CONFIRMED",
                "CHECKED_IN",
                "IN_CONSULTATION",
                "COMPLETED",
                "CANCELLED",
                "NO_SHOW"
        ));
        statusFilterCombo.getSelectionModel().selectFirst();
        statusFilterCombo.setOnAction(event -> applyFilters());
        keywordField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureTable() {
        codeColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getAppointmentCode())
        ));
        patientColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> AppointmentUiUtil.formatPatient(data.getValue())
        ));
        doctorColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getDoctorName())
        ));
        roomColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> AppointmentUiUtil.formatRoom(data.getValue())
        ));
        timeColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> AppointmentUiUtil.formatTimeSlot(data.getValue())
        ));
        statusColumn.setCellFactory(column -> new StatusTableCell());
        statusColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getStatus()
        ));
        actionsColumn.setCellFactory(column -> new ActionsTableCell());
        actionsColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue()));

        appointmentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateCheckInButton(newSelection)
        );
        appointmentTable.setPlaceholder(emptyLabel);
    }

    private void configureActions() {
        datePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadAppointments();
            }
        });
        updateCheckInButton(null);
    }

    private void loadAppointments() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            AlertUtil.showWarning("Today Appointments", "Please select a date.");
            return;
        }

        setLoading(true);
        appointmentService.findByDate(selectedDate)
                .whenComplete((appointments, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        loadedAppointments.clear();
                        appointmentTable.getItems().clear();
                        summaryLabel.setText("Unable to load appointments.");
                        AlertUtil.showError("Load Appointments", throwable);
                        return;
                    }

                    loadedAppointments.clear();
                    loadedAppointments.addAll(appointments == null ? List.of() : appointments);
                    applyFilters();
                }));
    }

    private void performCheckIn(AppointmentResponse appointment, String priority) {
        setLoading(true);
        appointmentService.checkIn(appointment.getId(), priority)
                .whenComplete((queueItem, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        AlertUtil.showError("Check-in Failed", throwable);
                        return;
                    }

                    String queueNumber = queueItem == null ? "-" : queueItem.displayQueueNumber();
                    AlertUtil.showInfo(
                            "Check-in Successful",
                            "Patient checked in. Queue number: " + queueNumber
                    );
                    loadAppointments();
                }));
    }

    private void performCancel(AppointmentResponse appointment) {
        TextInputDialog dialog = new TextInputDialog("Cancelled by receptionist request");
        dialog.setTitle("Cancel Booking");
        dialog.setHeaderText("Cancel Appointment " + appointment.getAppointmentCode());
        dialog.setContentText("Reason for cancellation:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String reason = result.get().trim();
        if (reason.isBlank()) {
            reason = "Cancelled by receptionist request";
        }

        setLoading(true);
        appointmentService.cancel(appointment.getId(), reason)
                .whenComplete((cancelled, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        AlertUtil.showError("Cancel Failed", throwable);
                        return;
                    }
                    AlertUtil.showInfo("Cancel Successful", "Appointment " + appointment.getAppointmentCode() + " has been cancelled.");
                    loadAppointments();
                }));
    }

    private void applyFilters() {
        String statusFilter = statusFilterCombo.getSelectionModel().getSelectedItem();
        List<AppointmentResponse> filtered = AppointmentUiUtil.filter(
                loadedAppointments,
                statusFilter,
                keywordField.getText()
        );
        appointmentTable.setItems(FXCollections.observableArrayList(filtered));
        summaryLabel.setText(filtered.size() + " appointment(s) shown");
        emptyLabel.setText("No appointments scheduled for this date.");
        updateCheckInButton(appointmentTable.getSelectionModel().getSelectedItem());
    }

    private void updateCheckInButton(AppointmentResponse selected) {
        boolean canCheckIn = selected != null && selected.isCheckInAllowed();
        checkInButton.setDisable(!canCheckIn);
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        refreshButton.setDisable(loading);
        checkInButton.setDisable(loading || appointmentTable.getSelectionModel().getSelectedItem() == null
                || !appointmentTable.getSelectionModel().getSelectedItem().isCheckInAllowed());
        if (bookAppointmentButton != null) {
            bookAppointmentButton.setDisable(loading);
        }
        todayButton.setDisable(loading);
        datePicker.setDisable(loading);
        statusFilterCombo.setDisable(loading);
        keywordField.setDisable(loading);
        appointmentTable.setDisable(loading);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private class ActionsTableCell extends TableCell<AppointmentResponse, AppointmentResponse> {

        private final HBox actionsBox = new HBox(6);
        private final Button checkInBtn = actionButton("Check-in", "btn-success");
        private final Button cancelBtn = actionButton("Cancel", "btn-danger-outline");
        private final Button reasonBtn = actionButton("Reason", "btn-warning-outline");

        private ActionsTableCell() {
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            checkInBtn.setOnAction(event -> {
                int idx = getIndex();
                if (getTableView() == null || idx < 0 || idx >= getTableView().getItems().size()) {
                    return;
                }
                AppointmentResponse appt = getTableView().getItems().get(idx);
                var window = checkInBtn.getScene() != null ? checkInBtn.getScene().getWindow() : null;
                CheckInDialog.show(appt, window)
                        .ifPresent(result -> performCheckIn(appt, result.priority()));
            });
            cancelBtn.setOnAction(event -> {
                int idx = getIndex();
                if (getTableView() == null || idx < 0 || idx >= getTableView().getItems().size()) {
                    return;
                }
                AppointmentResponse appt = getTableView().getItems().get(idx);
                performCancel(appt);
            });
            reasonBtn.setOnAction(event -> {
                int idx = getIndex();
                if (getTableView() == null || idx < 0 || idx >= getTableView().getItems().size()) {
                    return;
                }
                AppointmentResponse appt = getTableView().getItems().get(idx);
                String reason = appt.getCancelledReason() != null && !appt.getCancelledReason().isBlank()
                        ? appt.getCancelledReason()
                        : (appt.getReason() != null && !appt.getReason().isBlank() ? appt.getReason() : "No cancellation reason recorded.");
                AlertUtil.showInfo("Cancellation Reason Log", "Appointment: " + appt.getAppointmentCode() + "\nPatient: " + AppointmentUiUtil.formatPatient(appt) + "\n\nReason: " + reason);
            });
        }

        @Override
        protected void updateItem(AppointmentResponse appt, boolean empty) {
            super.updateItem(appt, empty);
            if (empty || appt == null) {
                setGraphic(null);
                return;
            }

            actionsBox.getChildren().clear();
            if (appt.isCheckInAllowed()) {
                actionsBox.getChildren().add(checkInBtn);
            }
            if (appt.isCancelAllowed()) {
                actionsBox.getChildren().add(cancelBtn);
            }
            if ("CANCELLED".equals(appt.getStatus())) {
                actionsBox.getChildren().add(reasonBtn);
            }

            setGraphic(actionsBox.getChildren().isEmpty() ? null : actionsBox);
        }

        private Button actionButton(String text, String styleClass) {
            Button button = new Button(text);
            button.getStyleClass().add(styleClass);
            button.setMinWidth(60);
            return button;
        }
    }

    private static class StatusTableCell extends TableCell<AppointmentResponse, String> {

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label badge = new Label(AppointmentUiUtil.displayStatus(status));
            badge.getStyleClass().add("status-badge");
            badge.getStyleClass().add(AppointmentUiUtil.statusStyleClass(status));
            setGraphic(badge);
            setText(null);
        }
    }
}
