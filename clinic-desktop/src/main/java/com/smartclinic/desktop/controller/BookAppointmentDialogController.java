package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.dto.BookAppointmentRequest;
import com.smartclinic.desktop.dto.DoctorResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.dto.RoomResponse;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.service.QueueDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.PatientUiUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BookAppointmentDialogController {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final QueueDesktopService queueService;
    private final AppointmentDesktopService appointmentService;

    private List<PatientResponse> allPatients = List.of();
    private AppointmentResponse createdAppointment;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField patientFilterField;

    @FXML
    private ComboBox<PatientResponse> patientCombo;

    @FXML
    private ComboBox<DoctorResponse> doctorCombo;

    @FXML
    private ComboBox<RoomResponse> roomCombo;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField endTimeField;

    @FXML
    private ComboBox<String> sourceCombo;

    @FXML
    private TextField reasonField;

    @FXML
    private Button submitButton;

    public BookAppointmentDialogController(
            QueueDesktopService queueService,
            AppointmentDesktopService appointmentService
    ) {
        this.queueService = queueService;
        this.appointmentService = appointmentService;
    }

    @FXML
    private void initialize() {
        configureCombos();
        configureDateTimeAutoFill();
        loadFormData();
    }

    @FXML
    private void onSubmit() {
        clearError();
        PatientResponse patient = patientCombo.getValue();
        DoctorResponse doctor = doctorCombo.getValue();
        RoomResponse room = roomCombo.getValue();
        String source = sourceCombo.getValue();
        String reason = reasonField.getText() == null ? "" : reasonField.getText().trim();

        if (patient == null) {
            showError("Please select a patient.");
            return;
        }
        if (doctor == null) {
            showError("Please select a doctor.");
            return;
        }
        if (source == null || source.isBlank()) {
            showError("Please select a booking source.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        if (startDate == null) {
            showError("Please select a start date.");
            return;
        }

        LocalTime startTime = parseTime(startTimeField.getText());
        if (startTime == null) {
            showError("Start time must be in HH:mm format (e.g., 09:00).");
            return;
        }

        LocalDate endDate = endDatePicker.getValue();
        if (endDate == null) {
            showError("Please select an end date.");
            return;
        }

        LocalTime endTime = parseTime(endTimeField.getText());
        if (endTime == null) {
            showError("End time must be in HH:mm format (e.g., 09:20).");
            return;
        }

        LocalDateTime scheduledStart = LocalDateTime.of(startDate, startTime);
        LocalDateTime scheduledEnd = LocalDateTime.of(endDate, endTime);

        if (scheduledStart.isBefore(LocalDateTime.now().minusMinutes(5))) {
            showError("Scheduled start time cannot be in the past.");
            return;
        }

        if (!scheduledEnd.isAfter(scheduledStart)) {
            showError("Scheduled end time must be after scheduled start time.");
            return;
        }

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        request.setRoomId(room == null ? null : room.getId());
        request.setScheduledStart(scheduledStart);
        request.setScheduledEnd(scheduledEnd);
        request.setSource(source);
        request.setReason(reason.isBlank() ? null : reason);

        setLoading(true);
        appointmentService.create(request)
                .whenComplete((appointment, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        String msg = cause.getMessage();
                        if (msg == null || msg.isBlank()) {
                            msg = "Failed to book appointment. Please check schedule availability.";
                        }
                        showError(msg);
                        return;
                    }
                    createdAppointment = appointment;
                    closeStage();
                }));
    }

    @FXML
    private void onCancel() {
        createdAppointment = null;
        closeStage();
    }

    public Optional<AppointmentResponse> getResult() {
        return Optional.ofNullable(createdAppointment);
    }

    private void configureCombos() {
        patientCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(PatientResponse patient) {
                return PatientUiUtil.formatPatientLabel(patient);
            }

            @Override
            public PatientResponse fromString(String string) {
                return patientCombo.getValue();
            }
        });

        doctorCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DoctorResponse doctor) {
                return doctor == null ? "" : doctor.displayLabel();
            }

            @Override
            public DoctorResponse fromString(String string) {
                return doctorCombo.getValue();
            }
        });

        roomCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(RoomResponse room) {
                return room == null ? "No Room (Uses Doctor's Default)" : room.displayLabel();
            }

            @Override
            public RoomResponse fromString(String string) {
                return roomCombo.getValue();
            }
        });

        sourceCombo.setItems(FXCollections.observableArrayList("WALK_IN", "PHONE", "ONLINE"));
        sourceCombo.getSelectionModel().select("WALK_IN");

        patientFilterField.textProperty().addListener((obs, oldVal, newVal) -> applyPatientFilter(newVal));
    }

    private void configureDateTimeAutoFill() {
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        startTimeField.setText("09:00");
        endTimeField.setText("09:20");

        startTimeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.trim().length() == 5) {
                LocalTime start = parseTime(newVal.trim());
                if (start != null) {
                    LocalTime end = start.plusMinutes(20);
                    endTimeField.setText(TIME_FORMATTER.format(end));
                    if (startDatePicker.getValue() != null) {
                        endDatePicker.setValue(startDatePicker.getValue());
                    }
                }
            }
        });

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                endDatePicker.setValue(newVal);
            }
        });
    }

    private void loadFormData() {
        setLoading(true);
        CompletableFuture<List<PatientResponse>> patientsFuture = queueService.getActivePatients();
        CompletableFuture<List<DoctorResponse>> doctorsFuture = queueService.getActiveDoctors();
        CompletableFuture<List<RoomResponse>> roomsFuture = queueService.getActiveRooms();

        CompletableFuture.allOf(patientsFuture, doctorsFuture, roomsFuture)
                .whenComplete((ignored, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        showError("Failed to load form data: " + cause.getMessage());
                        return;
                    }
                    allPatients = new ArrayList<>(patientsFuture.join());
                    applyPatientFilter(patientFilterField.getText());
                    doctorCombo.setItems(FXCollections.observableArrayList(doctorsFuture.join()));
                    roomCombo.setItems(FXCollections.observableArrayList(roomsFuture.join()));
                }));
    }

    private void applyPatientFilter(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<PatientResponse> filtered = allPatients.stream()
                .filter(p -> matchesPatient(p, normalized))
                .toList();
        PatientResponse selected = patientCombo.getValue();
        patientCombo.setItems(FXCollections.observableArrayList(filtered));
        if (selected != null && filtered.contains(selected)) {
            patientCombo.getSelectionModel().select(selected);
        }
    }

    private boolean matchesPatient(PatientResponse p, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return (p.getFullName() != null && p.getFullName().toLowerCase(Locale.ROOT).contains(keyword))
                || (p.getPatientCode() != null && p.getPatientCode().toLowerCase(Locale.ROOT).contains(keyword))
                || (p.getPhone() != null && p.getPhone().toLowerCase(Locale.ROOT).contains(keyword));
    }

    private LocalTime parseTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(text.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        submitButton.setDisable(loading);
    }

    private void closeStage() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
