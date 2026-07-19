package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.AppointmentUiUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TodayAppointmentsController implements NavigationAware {

    private final AppointmentDesktopService appointmentService;

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
    private Button refreshButton;

    @FXML
    private Button checkInButton;

    @FXML
    private Button todayButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label emptyLabel;

    public TodayAppointmentsController(AppointmentDesktopService appointmentService) {
        this.appointmentService = appointmentService;
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
    private void onCheckIn() {
        AppointmentResponse selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Check-in", "Please select an appointment first.");
            return;
        }
        if (!selected.isCheckInAllowed()) {
            AlertUtil.showWarning("Check-in", "Only BOOKED appointments can be checked in.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Check-in");
        confirm.setHeaderText(null);
        confirm.setContentText("Check in " + selected.getPatientName() + " for appointment "
                + selected.getAppointmentCode() + "?");
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                performCheckIn(selected);
            }
        });
    }

    private void configureFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL",
                "BOOKED",
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

    private void performCheckIn(AppointmentResponse appointment) {
        setLoading(true);
        appointmentService.checkIn(appointment.getId())
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
        todayButton.setDisable(loading);
        datePicker.setDisable(loading);
        statusFilterCombo.setDisable(loading);
        keywordField.setDisable(loading);
        appointmentTable.setDisable(loading);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
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
