package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.InvoiceResponse;
import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.BillingDesktopService;
import com.smartclinic.desktop.service.PaymentDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.InvoiceDetailDialog;
import com.smartclinic.desktop.util.PaymentDialog;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

public class PendingInvoicesController implements NavigationAware {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final BillingDesktopService billingService;
    private final PaymentDesktopService paymentService;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private TableView<InvoiceResponse> invoiceTable;

    @FXML
    private TableColumn<InvoiceResponse, String> numberColumn;

    @FXML
    private TableColumn<InvoiceResponse, String> patientColumn;

    @FXML
    private TableColumn<InvoiceResponse, String> visitColumn;

    @FXML
    private TableColumn<InvoiceResponse, String> amountColumn;

    @FXML
    private TableColumn<InvoiceResponse, String> statusColumn;

    @FXML
    private TableColumn<InvoiceResponse, String> issuedAtColumn;

    @FXML
    private TableColumn<InvoiceResponse, InvoiceResponse> actionsColumn;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label emptyLabel;

    public PendingInvoicesController(
            BillingDesktopService billingService,
            PaymentDesktopService paymentService
    ) {
        this.billingService = billingService;
        this.paymentService = paymentService;
    }

    @FXML
    private void initialize() {
        configureFilters();
        configureTable();
        loadInvoices();
    }

    @Override
    public void onNavigate() {
        loadInvoices();
    }

    @FXML
    private void onRefresh() {
        loadInvoices();
    }

    private void configureFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "UNPAID", "PAID", "CANCELLED"));
        statusFilterCombo.getSelectionModel().select("UNPAID");
        statusFilterCombo.setOnAction(event -> loadInvoices());
    }

    private void configureTable() {
        numberColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getInvoiceNumber())
        ));
        patientColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> formatPatient(data.getValue())
        ));
        visitColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getVisitCode())
        ));
        amountColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> formatCurrency(data.getValue().getTotalAmount())
        ));
        statusColumn.setCellFactory(column -> new StatusTableCell());
        statusColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getStatus()
        ));
        issuedAtColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getIssuedAt() == null ? "-" : DATE_TIME_FORMATTER.format(data.getValue().getIssuedAt())
        ));
        actionsColumn.setCellFactory(column -> new ActionsTableCell());
        actionsColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue()));

        invoiceTable.setPlaceholder(emptyLabel);
    }

    private void loadInvoices() {
        String filterStatus = statusFilterCombo.getValue();
        String paramStatus = "ALL".equals(filterStatus) ? null : filterStatus;

        setLoading(true);
        billingService.findAll(paramStatus)
                .whenComplete((invoices, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        invoiceTable.getItems().clear();
                        summaryLabel.setText("Unable to load invoices.");
                        AlertUtil.showError("Load Invoices", throwable);
                        return;
                    }

                    List<InvoiceResponse> list = invoices == null ? List.of() : invoices;
                    invoiceTable.setItems(FXCollections.observableArrayList(list));
                    summaryLabel.setText(list.size() + " invoice(s) listed");
                }));
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }
        if (statusFilterCombo != null) {
            statusFilterCombo.setDisable(loading);
        }
        if (invoiceTable != null) {
            invoiceTable.setDisable(loading);
        }
    }

    private String formatPatient(InvoiceResponse inv) {
        String name = inv.getPatientName() == null ? "Unknown" : inv.getPatientName();
        String code = inv.getPatientCode();
        return code == null || code.isBlank() ? name : name + " (" + code + ")";
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    private String valueOrDash(String val) {
        return val == null || val.isBlank() ? "-" : val;
    }

    private Window getWindow() {
        return invoiceTable.getScene() == null ? null : invoiceTable.getScene().getWindow();
    }

    private class ActionsTableCell extends TableCell<InvoiceResponse, InvoiceResponse> {

        private final HBox actionsBox = new HBox(6);
        private final Button payBtn = actionButton("Pay Now", "btn-success");
        private final Button viewBtn = actionButton("View", "btn-secondary");

        private ActionsTableCell() {
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            payBtn.setOnAction(event -> {
                int idx = getIndex();
                if (getTableView() == null || idx < 0 || idx >= getTableView().getItems().size()) {
                    return;
                }
                InvoiceResponse inv = getTableView().getItems().get(idx);
                Window owner = payBtn.getScene() == null ? null : payBtn.getScene().getWindow();
                PaymentDialog.show(inv, paymentService, owner)
                        .ifPresent(payment -> {
                            AlertUtil.showInfo("Payment Recorded", "Invoice " + inv.getInvoiceNumber() + " paid successfully.");
                            loadInvoices();
                        });
            });
            viewBtn.setOnAction(event -> {
                int idx = getIndex();
                if (getTableView() == null || idx < 0 || idx >= getTableView().getItems().size()) {
                    return;
                }
                InvoiceResponse inv = getTableView().getItems().get(idx);
                Window owner = viewBtn.getScene() == null ? null : viewBtn.getScene().getWindow();
                InvoiceDetailDialog.show(inv, owner);
            });
        }

        @Override
        protected void updateItem(InvoiceResponse inv, boolean empty) {
            super.updateItem(inv, empty);
            if (empty || inv == null) {
                setGraphic(null);
                return;
            }

            actionsBox.getChildren().clear();
            if (inv.isPayAllowed()) {
                actionsBox.getChildren().add(payBtn);
            }
            actionsBox.getChildren().add(viewBtn);

            setGraphic(actionsBox);
        }

        private Button actionButton(String text, String styleClass) {
            Button button = new Button(text);
            button.getStyleClass().add(styleClass);
            button.setMinWidth(64);
            return button;
        }
    }

    private static class StatusTableCell extends TableCell<InvoiceResponse, String> {

        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label badge = new Label(status);
            badge.getStyleClass().add("status-badge");
            switch (status) {
                case "UNPAID" -> badge.getStyleClass().add("status-badge-no-show");
                case "PAID" -> badge.getStyleClass().add("status-badge-checked-in");
                case "CANCELLED" -> badge.getStyleClass().add("status-badge-cancelled");
                default -> badge.getStyleClass().add("status-badge-default");
            }
            setGraphic(badge);
            setText(null);
        }
    }
}
