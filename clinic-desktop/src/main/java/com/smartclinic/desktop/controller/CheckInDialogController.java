package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.util.AppointmentUiUtil;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class CheckInDialogController {

    public record Result(String priority) {
    }

    private final AppointmentResponse appointment;
    private Result result;

    @FXML
    private Label appointmentCodeLabel;

    @FXML
    private Label patientLabel;

    @FXML
    private Label doctorLabel;

    @FXML
    private Label roomLabel;

    @FXML
    private Label timeSlotLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private Button confirmButton;

    public CheckInDialogController(AppointmentResponse appointment) {
        this.appointment = appointment;
    }

    @FXML
    private void initialize() {
        appointmentCodeLabel.setText(valueOrDash(appointment.getAppointmentCode()));
        patientLabel.setText(AppointmentUiUtil.formatPatient(appointment));
        doctorLabel.setText(valueOrDash(appointment.getDoctorName()));
        roomLabel.setText(AppointmentUiUtil.formatRoom(appointment));
        timeSlotLabel.setText(AppointmentUiUtil.formatTimeSlot(appointment));
        statusLabel.setText(AppointmentUiUtil.displayStatus(appointment.getStatus()));

        priorityCombo.setItems(FXCollections.observableArrayList("NORMAL", "URGENT"));
        priorityCombo.getSelectionModel().selectFirst();

        confirmButton.setDisable(!appointment.isCheckInAllowed());
    }

    @FXML
    private void onConfirm() {
        if (!appointment.isCheckInAllowed()) {
            return;
        }
        result = new Result(priorityCombo.getValue());
        closeDialog();
    }

    @FXML
    private void onCancel() {
        result = null;
        closeDialog();
    }

    public Optional<Result> getResult() {
        return Optional.ofNullable(result);
    }

    private void closeDialog() {
        confirmButton.getScene().getWindow().hide();
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
