package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.PatientFormRequest;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.service.PatientDesktopService;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PatientFormDialogController {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9,10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private final PatientDesktopService patientService;
    private final PatientResponse patientToEdit;
    private PatientResponse savedPatient;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField fullNameField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> genderCombo;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField identityNumberField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField emergencyNameField;

    @FXML
    private TextField emergencyPhoneField;

    @FXML
    private TextArea allergyNoteArea;

    @FXML
    private Button submitButton;

    public PatientFormDialogController(PatientDesktopService patientService) {
        this(patientService, null);
    }

    public PatientFormDialogController(PatientDesktopService patientService, PatientResponse patientToEdit) {
        this.patientService = patientService;
        this.patientToEdit = patientToEdit;
    }

    @FXML
    private void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList("MALE", "FEMALE", "OTHER"));

        if (patientToEdit != null) {
            titleLabel.setText("Edit Patient Profile");
            subtitleLabel.setText("Update the information for patient " + patientToEdit.getPatientCode() + ".");
            populateForm(patientToEdit);
        } else {
            titleLabel.setText("Register New Patient");
            subtitleLabel.setText("Please provide correct information to register the patient's record.");
        }
    }

    @FXML
    private void onSubmit() {
        clearError();
        String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String gender = genderCombo.getValue();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String identity = identityNumberField.getText() == null ? "" : identityNumberField.getText().trim();
        String address = addressField.getText() == null ? "" : addressField.getText().trim();
        String emergencyName = emergencyNameField.getText() == null ? "" : emergencyNameField.getText().trim();
        String emergencyPhone = emergencyPhoneField.getText() == null ? "" : emergencyPhoneField.getText().trim();
        String allergyNote = allergyNoteArea.getText() == null ? "" : allergyNoteArea.getText().trim();

        // Validation
        if (fullName.isBlank()) {
            showError("Full name is required.");
            return;
        }

        if (fullName.length() > 150) {
            showError("Full name must be at most 150 characters.");
            return;
        }

        if (dob != null && dob.isAfter(LocalDate.now())) {
            showError("Date of birth cannot be in the future.");
            return;
        }

        if (!phone.isBlank() && !PHONE_PATTERN.matcher(phone).matches()) {
            showError("Phone number must be 10 or 11 digits and start with 0.");
            return;
        }

        if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            showError("Email format is invalid.");
            return;
        }

        if (!identity.isBlank() && identity.length() > 50) {
            showError("Identity number must be at most 50 characters.");
            return;
        }

        if (!emergencyPhone.isBlank() && !PHONE_PATTERN.matcher(emergencyPhone).matches()) {
            showError("Emergency phone number must be 10 or 11 digits and start with 0.");
            return;
        }

        PatientFormRequest request = new PatientFormRequest();
        request.setFullName(fullName);
        request.setDateOfBirth(dob);
        request.setGender(gender == null || gender.isBlank() ? null : gender);
        request.setPhone(phone.isBlank() ? null : phone);
        request.setEmail(email.isBlank() ? null : email);
        request.setAddress(address.isBlank() ? null : address);
        request.setIdentityNumber(identity.isBlank() ? null : identity);
        request.setEmergencyContactName(emergencyName.isBlank() ? null : emergencyName);
        request.setEmergencyContactPhone(emergencyPhone.isBlank() ? null : emergencyPhone);
        request.setAllergyNote(allergyNote.isBlank() ? null : allergyNote);

        setLoading(true);

        var future = (patientToEdit == null)
                ? patientService.create(request)
                : patientService.update(patientToEdit.getId(), request);

        future.whenComplete((patient, throwable) -> Platform.runLater(() -> {
            setLoading(false);
            if (throwable != null) {
                String msg = throwable.getMessage();
                if (msg == null || msg.isBlank()) {
                    msg = "Failed to save patient record.";
                }
                showError(msg);
                return;
            }
            savedPatient = patient;
            closeStage();
        }));
    }

    @FXML
    private void onCancel() {
        savedPatient = null;
        closeStage();
    }

    public Optional<PatientResponse> getResult() {
        return Optional.ofNullable(savedPatient);
    }

    private void populateForm(PatientResponse patient) {
        fullNameField.setText(patient.getFullName());
        dobPicker.setValue(patient.getDateOfBirth());
        if (patient.getGender() != null) {
            genderCombo.getSelectionModel().select(patient.getGender());
        }
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
        identityNumberField.setText(patient.getIdentityNumber());
        addressField.setText(patient.getAddress());
        emergencyNameField.setText(patient.getEmergencyContactName());
        emergencyPhoneField.setText(patient.getEmergencyContactPhone());
        allergyNoteArea.setText(patient.getAllergyNote());
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
