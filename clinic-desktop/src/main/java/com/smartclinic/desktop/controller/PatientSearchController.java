package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.PageResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.PatientDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.PatientUiUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PatientSearchController implements NavigationAware {

    private final PatientDesktopService patientService;

    private String currentKeyword = "";
    private int currentPage;
    private int totalPages;
    private long totalItems;

    @FXML
    private TextField keywordField;

    @FXML
    private Button searchButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private TableView<PatientResponse> patientTable;

    @FXML
    private TableColumn<PatientResponse, String> codeColumn;

    @FXML
    private TableColumn<PatientResponse, String> nameColumn;

    @FXML
    private TableColumn<PatientResponse, String> genderColumn;

    @FXML
    private TableColumn<PatientResponse, String> dobColumn;

    @FXML
    private TableColumn<PatientResponse, String> phoneColumn;

    @FXML
    private TableColumn<PatientResponse, String> statusColumn;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label pageLabel;

    @FXML
    private Label emptyLabel;

    @FXML
    private VBox detailPanel;

    @FXML
    private Label detailPlaceholder;

    @FXML
    private Label detailNameLabel;

    @FXML
    private Label detailCodeLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Label detailGenderLabel;

    @FXML
    private Label detailDobLabel;

    @FXML
    private Label detailPhoneLabel;

    @FXML
    private Label detailEmailLabel;

    @FXML
    private Label detailIdentityLabel;

    @FXML
    private Label detailAddressLabel;

    @FXML
    private Label detailEmergencyLabel;

    @FXML
    private Label detailAllergyLabel;

    @FXML
    private Label archivedWarningLabel;

    public PatientSearchController(PatientDesktopService patientService) {
        this.patientService = patientService;
    }

    @FXML
    private void initialize() {
        configureTable();
        keywordField.setOnAction(event -> onSearch());
        patientTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> showPatientDetail(newValue)
        );
        clearDetailPanel();
    }

    @Override
    public void onNavigate() {
        if (patientTable.getItems().isEmpty()) {
            search(0);
        }
    }

    @FXML
    private void onSearch() {
        currentKeyword = keywordField.getText() == null ? "" : keywordField.getText().trim();
        search(0);
    }

    @FXML
    private void onReset() {
        keywordField.clear();
        currentKeyword = "";
        search(0);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage > 0) {
            search(currentPage - 1);
        }
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 < totalPages) {
            search(currentPage + 1);
        }
    }

    private void configureTable() {
        codeColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getPatientCode())
        ));
        nameColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getFullName())
        ));
        genderColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> PatientUiUtil.formatGender(data.getValue().getGender())
        ));
        dobColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> PatientUiUtil.formatDateOfBirth(data.getValue().getDateOfBirth())
        ));
        phoneColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> PatientUiUtil.formatValue(data.getValue().getPhone())
        ));
        statusColumn.setCellFactory(column -> new StatusTableCell());
        statusColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getStatus()
        ));
        patientTable.setPlaceholder(emptyLabel);
    }

    private void search(int page) {
        setLoading(true);
        patientService.search(currentKeyword, page)
                .whenComplete((response, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        patientTable.getItems().clear();
                        summaryLabel.setText("Unable to load patients.");
                        pageLabel.setText("");
                        updatePaginationButtons();
                        clearDetailPanel();
                        AlertUtil.showError("Patient Search", throwable);
                        return;
                    }

                    applySearchResult(response, page);
                }));
    }

    private void applySearchResult(PageResponse<PatientResponse> response, int page) {
        currentPage = page;
        totalPages = response == null ? 0 : Math.max(response.getTotalPages(), 0);
        totalItems = response == null ? 0 : response.getTotalItems();

        var items = response == null || response.getItems() == null
                ? FXCollections.<PatientResponse>emptyObservableList()
                : FXCollections.observableArrayList(response.getItems());
        patientTable.setItems(items);

        if (items.isEmpty()) {
            summaryLabel.setText("No patients found.");
            pageLabel.setText("");
            clearDetailPanel();
        } else {
            summaryLabel.setText(totalItems + " patient(s) found");
            pageLabel.setText(buildPageLabel());
            patientTable.getSelectionModel().selectFirst();
        }

        updatePaginationButtons();
    }

    private String buildPageLabel() {
        if (totalPages <= 0) {
            return "";
        }
        return "Page " + (currentPage + 1) + " of " + totalPages;
    }

    private void updatePaginationButtons() {
        previousButton.setDisable(currentPage <= 0 || totalPages <= 1);
        nextButton.setDisable(totalPages <= 1 || currentPage + 1 >= totalPages);
    }

    private void showPatientDetail(PatientResponse patient) {
        if (patient == null) {
            clearDetailPanel();
            return;
        }

        detailPlaceholder.setVisible(false);
        detailPlaceholder.setManaged(false);
        detailNameLabel.setText(patient.getFullName());
        detailCodeLabel.setText(patient.getPatientCode());
        detailGenderLabel.setText(PatientUiUtil.formatGender(patient.getGender()));
        detailDobLabel.setText(PatientUiUtil.formatDateOfBirth(patient.getDateOfBirth()));
        detailPhoneLabel.setText(PatientUiUtil.formatValue(patient.getPhone()));
        detailEmailLabel.setText(PatientUiUtil.formatValue(patient.getEmail()));
        detailIdentityLabel.setText(PatientUiUtil.formatValue(patient.getIdentityNumber()));
        detailAddressLabel.setText(PatientUiUtil.formatValue(patient.getAddress()));
        detailEmergencyLabel.setText(formatEmergencyContact(patient));
        detailAllergyLabel.setText(PatientUiUtil.formatValue(patient.getAllergyNote()));

        detailStatusLabel.setText(PatientUiUtil.displayStatus(patient.getStatus()));
        detailStatusLabel.getStyleClass().removeIf(style -> style.startsWith("patient-status-"));
        detailStatusLabel.getStyleClass().add("status-badge");
        detailStatusLabel.getStyleClass().add(PatientUiUtil.statusStyleClass(patient.getStatus()));

        boolean archived = "ARCHIVED".equals(patient.getStatus());
        archivedWarningLabel.setVisible(archived);
        archivedWarningLabel.setManaged(archived);
    }

    private void clearDetailPanel() {
        detailPlaceholder.setVisible(true);
        detailPlaceholder.setManaged(true);
        detailNameLabel.setText("-");
        detailCodeLabel.setText("-");
        detailGenderLabel.setText("-");
        detailDobLabel.setText("-");
        detailPhoneLabel.setText("-");
        detailEmailLabel.setText("-");
        detailIdentityLabel.setText("-");
        detailAddressLabel.setText("-");
        detailEmergencyLabel.setText("-");
        detailAllergyLabel.setText("-");
        detailStatusLabel.setText("-");
        detailStatusLabel.getStyleClass().removeIf(style -> style.startsWith("patient-status-") || "status-badge".equals(style));
        archivedWarningLabel.setVisible(false);
        archivedWarningLabel.setManaged(false);
    }

    private String formatEmergencyContact(PatientResponse patient) {
        String name = PatientUiUtil.formatValue(patient.getEmergencyContactName());
        String phone = PatientUiUtil.formatValue(patient.getEmergencyContactPhone());
        if ("-".equals(name) && "-".equals(phone)) {
            return "-";
        }
        if ("-".equals(phone)) {
            return name;
        }
        if ("-".equals(name)) {
            return phone;
        }
        return name + " (" + phone + ")";
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        searchButton.setDisable(loading);
        resetButton.setDisable(loading);
        previousButton.setDisable(loading || currentPage <= 0 || totalPages <= 1);
        nextButton.setDisable(loading || totalPages <= 1 || currentPage + 1 >= totalPages);
        keywordField.setDisable(loading);
        patientTable.setDisable(loading);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static class StatusTableCell extends TableCell<PatientResponse, String> {

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label badge = new Label(PatientUiUtil.displayStatus(status));
            badge.getStyleClass().add("status-badge");
            badge.getStyleClass().add(PatientUiUtil.statusStyleClass(status));
            setGraphic(badge);
            setText(null);
        }
    }
}
