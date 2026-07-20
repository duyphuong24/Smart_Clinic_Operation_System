package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.InvoiceResponse;
import com.smartclinic.desktop.dto.PaymentRequest;
import com.smartclinic.desktop.dto.PaymentResponse;
import com.smartclinic.desktop.service.PaymentDesktopService;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentDialogController {

    private final InvoiceResponse invoice;
    private final PaymentDesktopService paymentService;
    private PaymentResponse recordedPayment;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private Label patientLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private ComboBox<String> methodCombo;

    @FXML
    private TextField amountField;

    @FXML
    private TextField transactionRefField;

    @FXML
    private TextField noteField;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Button confirmButton;

    public PaymentDialogController(InvoiceResponse invoice, PaymentDesktopService paymentService) {
        this.invoice = invoice;
        this.paymentService = paymentService;
    }

    @FXML
    private void initialize() {
        invoiceNumberLabel.setText(invoice.getInvoiceNumber() == null ? "-" : invoice.getInvoiceNumber());
        patientLabel.setText(formatPatient(invoice));

        BigDecimal total = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
        totalAmountLabel.setText(formatCurrency(total));
        amountField.setText(total.toPlainString());

        methodCombo.setItems(FXCollections.observableArrayList("CASH", "BANK_TRANSFER", "CREDIT_CARD"));
        methodCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onConfirm() {
        clearError();

        String method = methodCombo.getValue();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();
        String ref = transactionRefField.getText() == null ? "" : transactionRefField.getText().trim();
        String note = noteField.getText() == null ? "" : noteField.getText().trim();

        if (amountText.isBlank()) {
            showError("Please enter payment amount.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Payment amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Invalid payment amount format.");
            return;
        }

        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);
        request.setMethod(method);
        request.setTransactionRef(ref.isBlank() ? null : ref);
        request.setNote(note.isBlank() ? null : note);

        setLoading(true);
        paymentService.recordPayment(invoice.getId(), request)
                .whenComplete((payment, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        String msg = cause.getMessage();
                        showError(msg == null || msg.isBlank() ? "Payment failed." : msg);
                        return;
                    }
                    recordedPayment = payment;
                    closeDialog();
                }));
    }

    @FXML
    private void onCancel() {
        recordedPayment = null;
        closeDialog();
    }

    public Optional<PaymentResponse> getResult() {
        return Optional.ofNullable(recordedPayment);
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
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }
        confirmButton.setDisable(loading);
    }

    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private String formatPatient(InvoiceResponse inv) {
        String name = inv.getPatientName() == null ? "Unknown" : inv.getPatientName();
        String code = inv.getPatientCode();
        return code == null || code.isBlank() ? name : name + " (" + code + ")";
    }

    private String formatCurrency(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
