package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.DoctorResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.dto.RoomResponse;
import com.smartclinic.desktop.dto.WalkInQueueRequest;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.QueueDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.PatientUiUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class WalkInController implements NavigationAware {

    private final QueueDesktopService queueService;

    private List<PatientResponse> allPatients = List.of();

    @FXML
    private TextField patientFilterField;

    @FXML
    private ComboBox<PatientResponse> patientCombo;

    @FXML
    private ComboBox<DoctorResponse> doctorCombo;

    @FXML
    private ComboBox<RoomResponse> roomCombo;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private TextField reasonField;

    @FXML
    private Button submitButton;

    @FXML
    private Button resetButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label formStatusLabel;

    @FXML
    private VBox resultPanel;

    @FXML
    private Label resultTitleLabel;

    @FXML
    private Label resultDetailLabel;

    public WalkInController(QueueDesktopService queueService) {
        this.queueService = queueService;
    }

    @FXML
    private void initialize() {
        configureCombos();
        hideResultPanel();
        patientFilterField.textProperty().addListener((obs, oldValue, newValue) -> applyPatientFilter(newValue));
    }

    @Override
    public void onNavigate() {
        loadFormData();
    }

    @FXML
    private void onSubmit() {
        PatientResponse patient = patientCombo.getValue();
        DoctorResponse doctor = doctorCombo.getValue();

        if (patient == null) {
            formStatusLabel.setText("Please select a patient.");
            AlertUtil.showWarning("Quick Walk-in", "Please select a patient.");
            return;
        }
        if (doctor == null) {
            formStatusLabel.setText("Please select a doctor.");
            AlertUtil.showWarning("Quick Walk-in", "Please select a doctor.");
            return;
        }
        if (!patient.isActive()) {
            formStatusLabel.setText("Inactive or archived patients cannot enter the queue.");
            AlertUtil.showWarning("Quick Walk-in", "Inactive or archived patients cannot enter the queue.");
            return;
        }

        WalkInQueueRequest request = new WalkInQueueRequest();
        request.setPatientId(patient.getId());
        request.setDoctorId(doctor.getId());
        RoomResponse room = roomCombo.getValue();
        request.setRoomId(room == null ? null : room.getId());
        request.setPriority(priorityCombo.getValue());
        request.setReason(normalizeReason(reasonField.getText()));

        setLoading(true);
        queueService.createWalkIn(request)
                .whenComplete((queueItem, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        formStatusLabel.setText("Unable to register walk-in.");
                        AlertUtil.showError("Walk-in Failed", throwable);
                        return;
                    }

                    showSuccess(queueItem);
                    resetFormFields(false);
                }));
    }

    @FXML
    private void onReset() {
        resetFormFields(true);
        hideResultPanel();
        formStatusLabel.setText("");
    }

    private void configureCombos() {
        patientCombo.setConverter(patientConverter());
        doctorCombo.setConverter(doctorConverter());
        roomCombo.setConverter(roomConverter());
        roomCombo.setPromptText("Use doctor default room");

        priorityCombo.setItems(FXCollections.observableArrayList("NORMAL", "URGENT"));
        priorityCombo.getSelectionModel().selectFirst();
    }

    private void loadFormData() {
        setLoading(true);
        formStatusLabel.setText("Loading patients, doctors, and rooms...");

        CompletableFuture<List<PatientResponse>> patientsFuture = queueService.getActivePatients();
        CompletableFuture<List<DoctorResponse>> doctorsFuture = queueService.getActiveDoctors();
        CompletableFuture<List<RoomResponse>> roomsFuture = queueService.getActiveRooms();

        CompletableFuture.allOf(patientsFuture, doctorsFuture, roomsFuture)
                .whenComplete((ignored, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        formStatusLabel.setText("Unable to load walk-in form data.");
                        AlertUtil.showError("Quick Walk-in", throwable);
                        return;
                    }

                    allPatients = new ArrayList<>(patientsFuture.join());
                    applyPatientFilter(patientFilterField.getText());
                    doctorCombo.setItems(FXCollections.observableArrayList(doctorsFuture.join()));
                    roomCombo.setItems(FXCollections.observableArrayList(roomsFuture.join()));
                    roomCombo.getSelectionModel().clearSelection();
                    formStatusLabel.setText("Select patient and doctor, then register walk-in.");
                }));
    }

    private void applyPatientFilter(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<PatientResponse> filtered = allPatients.stream()
                .filter(patient -> matchesPatientFilter(patient, normalized))
                .toList();
        PatientResponse selected = patientCombo.getValue();
        patientCombo.setItems(FXCollections.observableArrayList(filtered));
        if (selected != null && filtered.contains(selected)) {
            patientCombo.getSelectionModel().select(selected);
        }
    }

    private boolean matchesPatientFilter(PatientResponse patient, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return contains(patient.getFullName(), keyword)
                || contains(patient.getPatientCode(), keyword)
                || contains(patient.getPhone(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void showSuccess(QueueItemResponse queueItem) {
        resultPanel.setVisible(true);
        resultPanel.setManaged(true);
        resultTitleLabel.setText("Walk-in registered successfully");
        resultDetailLabel.setText(buildSuccessMessage(queueItem));
        formStatusLabel.setText("Patient added to today's queue.");
        AlertUtil.showInfo(
                "Walk-in Successful",
                "Queue number: " + queueItem.displayQueueNumber()
        );
    }

    private String buildSuccessMessage(QueueItemResponse queueItem) {
        return "Queue #: " + queueItem.displayQueueNumber()
                + " | Patient: " + PatientUiUtil.formatValue(queueItem.getPatientName())
                + " | Doctor: " + PatientUiUtil.formatValue(queueItem.getDoctorName())
                + " | Room: " + PatientUiUtil.formatValue(queueItem.getRoomCode())
                + " | Status: " + (queueItem.getStatus() == null ? "WAITING" : queueItem.getStatus());
    }

    private void hideResultPanel() {
        resultPanel.setVisible(false);
        resultPanel.setManaged(false);
    }

    private void resetFormFields(boolean clearFilter) {
        if (clearFilter) {
            patientFilterField.clear();
            applyPatientFilter("");
        }
        patientCombo.getSelectionModel().clearSelection();
        doctorCombo.getSelectionModel().clearSelection();
        roomCombo.getSelectionModel().clearSelection();
        priorityCombo.getSelectionModel().selectFirst();
        reasonField.clear();
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        submitButton.setDisable(loading);
        resetButton.setDisable(loading);
        patientFilterField.setDisable(loading);
        patientCombo.setDisable(loading);
        doctorCombo.setDisable(loading);
        roomCombo.setDisable(loading);
        priorityCombo.setDisable(loading);
        reasonField.setDisable(loading);
    }

    private StringConverter<PatientResponse> patientConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(PatientResponse patient) {
                return PatientUiUtil.formatPatientLabel(patient);
            }

            @Override
            public PatientResponse fromString(String string) {
                return patientCombo.getValue();
            }
        };
    }

    private StringConverter<DoctorResponse> doctorConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(DoctorResponse doctor) {
                return doctor == null ? "" : doctor.displayLabel();
            }

            @Override
            public DoctorResponse fromString(String string) {
                return doctorCombo.getValue();
            }
        };
    }

    private StringConverter<RoomResponse> roomConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(RoomResponse room) {
                return room == null ? "Use doctor default room" : room.displayLabel();
            }

            @Override
            public RoomResponse fromString(String string) {
                return roomCombo.getValue();
            }
        };
    }
}
