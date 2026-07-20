package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.navigation.DesktopRoute;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.navigation.NavigationService;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.service.PatientDesktopService;
import com.smartclinic.desktop.service.QueueDesktopService;
import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.AppointmentUiUtil;
import com.smartclinic.desktop.util.BookAppointmentDialog;
import com.smartclinic.desktop.util.PatientFormDialog;
import com.smartclinic.desktop.util.RoleUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;

public class HomeController implements NavigationAware {

    private final SessionManager sessionManager;
    private final NavigationService navigationService;
    private final AppointmentDesktopService appointmentService;
    private final QueueDesktopService queueService;
    private final PatientDesktopService patientService;

    @FXML
    private Label welcomeTitle;

    @FXML
    private Label roleBadge;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label apptValueLabel;

    @FXML
    private Label apptSubLabel;

    @FXML
    private Label queueValueLabel;

    @FXML
    private Label queueSubLabel;

    @FXML
    private Label checkedInValueLabel;

    @FXML
    private Label checkedInSubLabel;

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
    private Label emptyLabel;

    public HomeController(
            SessionManager sessionManager,
            NavigationService navigationService,
            AppointmentDesktopService appointmentService,
            QueueDesktopService queueService,
            PatientDesktopService patientService
    ) {
        this.sessionManager = sessionManager;
        this.navigationService = navigationService;
        this.appointmentService = appointmentService;
        this.queueService = queueService;
        this.patientService = patientService;
    }

    @FXML
    private void initialize() {
        configureHeader();
        configureTable();
        loadDashboardData();
    }

    @Override
    public void onNavigate() {
        configureHeader();
        loadDashboardData();
    }

    @FXML
    private void onRefresh() {
        loadDashboardData();
    }

    @FXML
    private void onBookAppointment() {
        BookAppointmentDialog.show(queueService, appointmentService, getWindow())
                .ifPresent(created -> {
                    AlertUtil.showInfo("Book Appointment", "Appointment " + created.getAppointmentCode() + " booked successfully.");
                    loadDashboardData();
                });
    }

    @FXML
    private void onRegisterPatient() {
        PatientFormDialog.showCreate(patientService, getWindow())
                .ifPresent(created -> {
                    AlertUtil.showInfo("Register Patient", "Patient " + created.getFullName() + " registered successfully.");
                    loadDashboardData();
                });
    }

    @FXML
    private void onGoToWalkIn() {
        navigationService.navigateTo(DesktopRoute.WALK_IN);
    }

    @FXML
    private void onGoToQueueBoard() {
        navigationService.navigateTo(DesktopRoute.QUEUE_BOARD);
    }

    @FXML
    private void onGoToAppointments() {
        navigationService.navigateTo(DesktopRoute.TODAY_APPOINTMENTS);
    }

    private void configureHeader() {
        String fullName = sessionManager.getFullName();
        welcomeTitle.setText("Welcome, " + (fullName == null || fullName.isBlank() ? "User" : fullName));
        roleBadge.setText(RoleUtil.primaryRoleLabel(sessionManager.getRoles()));
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
        appointmentTable.setPlaceholder(emptyLabel);
    }

    private void loadDashboardData() {
        setLoading(true);
        LocalDate today = LocalDate.now();

        CompletableFuture<List<AppointmentResponse>> apptsFuture = appointmentService.getTodayAppointments();
        CompletableFuture<List<QueueItemResponse>> queueFuture = queueService.findActiveQueue(today, null);

        CompletableFuture.allOf(apptsFuture, queueFuture)
                .whenComplete((ignored, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        appointmentTable.getItems().clear();
                        apptValueLabel.setText("0");
                        apptSubLabel.setText("Unable to load stats");
                        queueValueLabel.setText("0");
                        queueSubLabel.setText("Unable to load stats");
                        checkedInValueLabel.setText("0");
                        checkedInSubLabel.setText("Unable to load stats");
                        return;
                    }

                    List<AppointmentResponse> appointments = apptsFuture.join();
                    List<QueueItemResponse> queueItems = queueFuture.join();

                    updateAppointmentStats(appointments);
                    updateQueueStats(queueItems);
                }));
    }

    private void updateAppointmentStats(List<AppointmentResponse> list) {
        if (list == null) {
            list = List.of();
        }

        int total = list.size();
        long booked = list.stream().filter(a -> "BOOKED".equals(a.getStatus())).count();
        long pending = list.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
        long checkedIn = list.stream().filter(a -> "CHECKED_IN".equals(a.getStatus())).count();

        apptValueLabel.setText(String.valueOf(total));
        apptSubLabel.setText(booked + " Booked • " + pending + " Pending");

        checkedInValueLabel.setText(String.valueOf(checkedIn));

        appointmentTable.setItems(FXCollections.observableArrayList(list));
    }

    private void updateQueueStats(List<QueueItemResponse> list) {
        if (list == null) {
            list = List.of();
        }

        int totalActive = list.size();
        long waiting = list.stream().filter(q -> "WAITING".equals(q.getStatus())).count();
        long called = list.stream().filter(q -> "CALLED".equals(q.getStatus())).count();

        queueValueLabel.setText(String.valueOf(totalActive));
        queueSubLabel.setText(waiting + " Waiting • " + called + " Called");
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }
    }

    private Window getWindow() {
        return welcomeTitle.getScene() == null ? null : welcomeTitle.getScene().getWindow();
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
