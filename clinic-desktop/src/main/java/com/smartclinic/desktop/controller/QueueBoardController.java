package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.DoctorResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.QueueDesktopService;
import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.QueueUiUtil;
import com.smartclinic.desktop.util.RoleUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class QueueBoardController implements NavigationAware {

    private static final DoctorResponse ALL_DOCTORS = allDoctorsOption();

    private final QueueDesktopService queueService;
    private final SessionManager sessionManager;

    private List<String> userRoles = List.of();
    private boolean canManageQueue;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<DoctorResponse> doctorFilterCombo;

    @FXML
    private TableView<QueueItemResponse> queueTable;

    @FXML
    private TableColumn<QueueItemResponse, String> numberColumn;

    @FXML
    private TableColumn<QueueItemResponse, String> patientColumn;

    @FXML
    private TableColumn<QueueItemResponse, String> doctorColumn;

    @FXML
    private TableColumn<QueueItemResponse, String> roomColumn;

    @FXML
    private TableColumn<QueueItemResponse, String> priorityColumn;

    @FXML
    private TableColumn<QueueItemResponse, String> statusColumn;

    @FXML
    private TableColumn<QueueItemResponse, QueueItemResponse> actionsColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button todayButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label emptyLabel;

    public QueueBoardController(QueueDesktopService queueService, SessionManager sessionManager) {
        this.queueService = queueService;
        this.sessionManager = sessionManager;
    }

    @FXML
    private void initialize() {
        userRoles = sessionManager.getRoles();
        canManageQueue = RoleUtil.hasAnyRole(userRoles, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR");

        configureFilters();
        configureTable();
        datePicker.setValue(LocalDate.now());
    }

    @Override
    public void onNavigate() {
        if (datePicker.getValue() == null) {
            datePicker.setValue(LocalDate.now());
        }
        loadDoctors();
        loadQueue();
    }

    @FXML
    private void onRefresh() {
        loadQueue();
    }

    @FXML
    private void onToday() {
        datePicker.setValue(LocalDate.now());
        loadQueue();
    }

    private void configureFilters() {
        doctorFilterCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DoctorResponse doctor) {
                if (doctor == null) {
                    return "";
                }
                return doctor.getFullName() == null ? "Doctor" : doctor.getFullName();
            }

            @Override
            public DoctorResponse fromString(String string) {
                return null;
            }
        });
        doctorFilterCombo.setOnAction(event -> loadQueue());
        datePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadQueue();
            }
        });
    }

    private void configureTable() {
        numberColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().displayQueueNumber()
        ));
        patientColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> QueueUiUtil.formatPatient(data.getValue())
        ));
        doctorColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getDoctorName())
        ));
        roomColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> QueueUiUtil.formatRoom(data.getValue())
        ));
        priorityColumn.setCellFactory(column -> new PriorityTableCell());
        priorityColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getPriority()
        ));
        statusColumn.setCellFactory(column -> new StatusTableCell());
        statusColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getStatus()
        ));
        actionsColumn.setCellFactory(column -> new ActionsTableCell());
        actionsColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue()));

        queueTable.setPlaceholder(emptyLabel);
    }

    private void loadDoctors() {
        queueService.getActiveDoctors()
                .whenComplete((doctors, throwable) -> Platform.runLater(() -> {
                    if (throwable != null) {
                        doctorFilterCombo.setItems(FXCollections.observableArrayList(ALL_DOCTORS));
                        doctorFilterCombo.getSelectionModel().selectFirst();
                        return;
                    }

                    List<DoctorResponse> options = new ArrayList<>();
                    options.add(ALL_DOCTORS);
                    if (doctors != null) {
                        options.addAll(doctors);
                    }
                    doctorFilterCombo.setItems(FXCollections.observableArrayList(options));
                    doctorFilterCombo.getSelectionModel().selectFirst();
                }));
    }

    private void loadQueue() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            AlertUtil.showWarning("Queue Board", "Please select a date.");
            return;
        }

        Long doctorId = selectedDoctorId();
        setLoading(true);
        queueService.findActiveQueue(selectedDate, doctorId)
                .whenComplete((items, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        queueTable.getItems().clear();
                        summaryLabel.setText("Unable to load queue.");
                        AlertUtil.showError("Load Queue", throwable);
                        return;
                    }

                    List<QueueItemResponse> queueItems = items == null ? List.of() : items;
                    queueTable.setItems(FXCollections.observableArrayList(queueItems));
                    summaryLabel.setText(queueItems.size() + " patient(s) in active queue");
                    emptyLabel.setText("No patients in the active queue currently.");
                }));
    }

    private void performAction(String actionLabel, Long queueItemId, java.util.function.Supplier<java.util.concurrent.CompletableFuture<QueueItemResponse>> action) {
        if (queueItemId == null) {
            return;
        }

        setLoading(true);
        action.get()
                .whenComplete((updated, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        AlertUtil.showError(actionLabel + " Failed", throwable);
                        return;
                    }
                    loadQueue();
                }));
    }

    private Long selectedDoctorId() {
        DoctorResponse selected = doctorFilterCombo.getSelectionModel().getSelectedItem();
        return selected == null ? null : selected.getId();
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        refreshButton.setDisable(loading);
        todayButton.setDisable(loading);
        datePicker.setDisable(loading);
        doctorFilterCombo.setDisable(loading);
        queueTable.setDisable(loading);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static DoctorResponse allDoctorsOption() {
        DoctorResponse option = new DoctorResponse();
        option.setFullName("All Active Doctors");
        return option;
    }

    private class ActionsTableCell extends TableCell<QueueItemResponse, QueueItemResponse> {

        private final HBox actionsBox = new HBox(6);
        private final Button callButton = actionButton("Call", "btn-info");
        private final Button startButton = actionButton("Start", "btn-primary");
        private final Button doneButton = actionButton("Done", "btn-success");
        private final Button skipButton = actionButton("Skip", "btn-danger-outline");

        private ActionsTableCell() {
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            callButton.setOnAction(event -> {
                QueueItemResponse item = getTableView().getItems().get(getIndex());
                performAction("Call", item.getId(), () -> queueService.call(item.getId()));
            });
            startButton.setOnAction(event -> {
                QueueItemResponse item = getTableView().getItems().get(getIndex());
                performAction("Start", item.getId(), () -> queueService.startService(item.getId()));
            });
            doneButton.setOnAction(event -> {
                QueueItemResponse item = getTableView().getItems().get(getIndex());
                performAction("Done", item.getId(), () -> queueService.done(item.getId()));
            });
            skipButton.setOnAction(event -> {
                QueueItemResponse item = getTableView().getItems().get(getIndex());
                performAction("Skip", item.getId(), () -> queueService.skip(item.getId()));
            });
        }

        @Override
        protected void updateItem(QueueItemResponse item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || !canManageQueue) {
                setGraphic(null);
                return;
            }

            String status = item.getStatus();
            actionsBox.getChildren().clear();

            if (QueueUiUtil.canCall(userRoles, status)) {
                actionsBox.getChildren().add(callButton);
            }
            if (QueueUiUtil.canStart(userRoles, status)) {
                actionsBox.getChildren().add(startButton);
            }
            if (QueueUiUtil.canDone(userRoles, status)) {
                actionsBox.getChildren().add(doneButton);
            }
            if (QueueUiUtil.canSkip(userRoles, status)) {
                actionsBox.getChildren().add(skipButton);
            }

            setGraphic(actionsBox.getChildren().isEmpty() ? null : actionsBox);
        }

        private Button actionButton(String text, String styleClass) {
            Button button = new Button(text);
            button.getStyleClass().add(styleClass);
            button.setMinWidth(56);
            return button;
        }
    }

    private static class StatusTableCell extends TableCell<QueueItemResponse, String> {

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label badge = new Label(QueueUiUtil.displayStatus(status));
            badge.getStyleClass().add("status-badge");
            badge.getStyleClass().add(QueueUiUtil.statusStyleClass(status));
            setGraphic(badge);
            setText(null);
        }
    }

    private static class PriorityTableCell extends TableCell<QueueItemResponse, String> {

        @Override
        protected void updateItem(String priority, boolean empty) {
            super.updateItem(priority, empty);
            if (empty || priority == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label badge = new Label(QueueUiUtil.displayPriority(priority));
            badge.getStyleClass().add("status-badge");
            badge.getStyleClass().add(QueueUiUtil.priorityStyleClass(priority));
            setGraphic(badge);
            setText(null);
        }
    }
}
