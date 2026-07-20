package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.InvoiceItemResponse;
import com.smartclinic.desktop.dto.InvoiceResponse;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class InvoiceDetailDialogController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final InvoiceResponse invoice;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private Label statusBadge;

    @FXML
    private Label patientLabel;

    @FXML
    private Label visitLabel;

    @FXML
    private Label issuedAtLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private TableView<InvoiceItemResponse> itemsTable;

    @FXML
    private TableColumn<InvoiceItemResponse, String> descColumn;

    @FXML
    private TableColumn<InvoiceItemResponse, String> typeColumn;

    @FXML
    private TableColumn<InvoiceItemResponse, String> qtyColumn;

    @FXML
    private TableColumn<InvoiceItemResponse, String> priceColumn;

    @FXML
    private TableColumn<InvoiceItemResponse, String> totalColumn;

    public InvoiceDetailDialogController(InvoiceResponse invoice) {
        this.invoice = invoice;
    }

    @FXML
    private void initialize() {
        invoiceNumberLabel.setText(invoice.getInvoiceNumber() == null ? "-" : invoice.getInvoiceNumber());
        statusBadge.setText(invoice.getStatus() == null ? "UNKNOWN" : invoice.getStatus());

        patientLabel.setText(formatPatient(invoice));
        visitLabel.setText(invoice.getVisitCode() == null ? "-" : invoice.getVisitCode());
        issuedAtLabel.setText(invoice.getIssuedAt() == null ? "-" : DATE_TIME_FORMATTER.format(invoice.getIssuedAt()));

        BigDecimal total = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
        totalAmountLabel.setText(formatCurrency(total));

        configureTable();
        List<InvoiceItemResponse> items = invoice.getItems() == null ? List.of() : invoice.getItems();
        itemsTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) invoiceNumberLabel.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void configureTable() {
        descColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getDescription())
        ));
        typeColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> valueOrDash(data.getValue().getItemType())
        ));
        qtyColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getQuantity() == null ? "1" : String.valueOf(data.getValue().getQuantity())
        ));
        priceColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> formatCurrency(data.getValue().getUnitPrice())
        ));
        totalColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> formatCurrency(data.getValue().getLineTotal())
        ));
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
}
