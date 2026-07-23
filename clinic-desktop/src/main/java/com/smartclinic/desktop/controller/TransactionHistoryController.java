package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.PaymentResponse;
import com.smartclinic.desktop.service.PaymentDesktopService;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransactionHistoryController {

    private PaymentDesktopService paymentService;
    private final ObservableList<PaymentResponse> masterData = FXCollections.observableArrayList();
    private final ObservableList<PaymentResponse> filteredData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> methodFilterCombo;

    @FXML
    private TableView<PaymentResponse> transactionTable;

    @FXML
    private TableColumn<PaymentResponse, String> paidAtCol;

    @FXML
    private TableColumn<PaymentResponse, String> invoiceIdCol;

    @FXML
    private TableColumn<PaymentResponse, String> methodCol;

    @FXML
    private TableColumn<PaymentResponse, String> statusCol;

    @FXML
    private TableColumn<PaymentResponse, String> amountCol;

    @FXML
    private TableColumn<PaymentResponse, String> transactionRefCol;

    @FXML
    private TableColumn<PaymentResponse, String> noteCol;

    public void setPaymentService(PaymentDesktopService paymentService) {
        this.paymentService = paymentService;
        loadData();
    }

    @FXML
    private void initialize() {
        paidAtCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getPaidAt() != null ? cell.getValue().getPaidAt().format(DATE_FORMATTER) : "-"));
        invoiceIdCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getInvoiceId() != null ? "#" + cell.getValue().getInvoiceId() : "-"));
        methodCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getMethod() != null ? cell.getValue().getMethod() : "-"));
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getStatus() != null ? cell.getValue().getStatus() : "-"));
        amountCol.setCellValueFactory(cell -> new SimpleStringProperty(
                formatCurrency(cell.getValue().getAmount())));
        transactionRefCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTransactionRef() != null ? cell.getValue().getTransactionRef() : "-"));
        noteCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getNote() != null ? cell.getValue().getNote() : "-"));

        methodFilterCombo.setItems(FXCollections.observableArrayList("ALL", "CASH", "PAYOS_QR", "CREDIT_CARD", "BANK_TRANSFER"));
        methodFilterCombo.getSelectionModel().selectFirst();
        methodFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterData());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());

        transactionTable.setItems(filteredData);
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    private void loadData() {
        if (paymentService == null) {
            return;
        }
        paymentService.findAll(null, null)
                .whenComplete((list, throwable) -> Platform.runLater(() -> {
                    if (throwable == null && list != null) {
                        masterData.setAll(list);
                        filterData();
                    }
                }));
    }

    private void filterData() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedMethod = methodFilterCombo.getValue();

        List<PaymentResponse> filtered = masterData.stream()
                .filter(p -> {
                    if (selectedMethod == null || "ALL".equals(selectedMethod)) {
                        return true;
                    }
                    return selectedMethod.equalsIgnoreCase(p.getMethod());
                })
                .filter(p -> {
                    if (query.isBlank()) {
                        return true;
                    }
                    String invId = p.getInvoiceId() == null ? "" : p.getInvoiceId().toString();
                    String ref = p.getTransactionRef() == null ? "" : p.getTransactionRef().toLowerCase();
                    String note = p.getNote() == null ? "" : p.getNote().toLowerCase();
                    return invId.contains(query) || ref.contains(query) || note.contains(query);
                })
                .toList();

        filteredData.setAll(filtered);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
