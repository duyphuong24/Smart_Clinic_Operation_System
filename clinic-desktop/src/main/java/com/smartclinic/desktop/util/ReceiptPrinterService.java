package com.smartclinic.desktop.util;

import com.smartclinic.desktop.dto.InvoiceItemResponse;
import com.smartclinic.desktop.dto.InvoiceResponse;
import com.smartclinic.desktop.dto.PaymentResponse;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ReceiptPrinterService {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    public static void printReceipt(InvoiceResponse invoice, PaymentResponse payment, Window owner) {
        VBox receiptBox = buildReceiptNode(invoice, payment);

        Stage previewStage = new Stage();
        previewStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            previewStage.initOwner(owner);
        }
        previewStage.setTitle("Official Receipt - Print Preview");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");

        receiptBox.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Button printButton = new Button("🖨️ Print Receipt");
        printButton.getStyleClass().add("btn-primary");
        printButton.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 24;");

        printButton.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(previewStage)) {
                boolean success = job.printPage(receiptBox);
                if (success) {
                    job.endJob();
                    AlertUtil.showInfo("Print Complete", "Receipt printed successfully!");
                    previewStage.close();
                } else {
                    AlertUtil.showError("Print Failed", new Exception("Failed to print receipt page."));
                }
            }
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> previewStage.close());

        HBox btnBox = new HBox(12, printButton, closeButton);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(receiptBox, btnBox);

        Scene scene = new Scene(root, 480, 680);
        previewStage.setScene(scene);
        previewStage.show();
    }

    public static VBox buildReceiptNode(InvoiceResponse invoice, PaymentResponse payment) {
        VBox box = new VBox(8);
        box.setPrefWidth(400);

        Label title = new Label("SMART CLINIC MEDICAL CENTER");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");
        Label subtitle = new Label("Official Financial Receipt & POS Checkout");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

        VBox header = new VBox(2, title, subtitle);
        header.setAlignment(Pos.CENTER);

        VBox info = new VBox(4);
        info.getChildren().add(makeRow("Invoice No:", invoice.getInvoiceNumber() == null ? "-" : invoice.getInvoiceNumber()));
        info.getChildren().add(makeRow("Patient Name:", invoice.getPatientName() == null ? "-" : invoice.getPatientName()));
        if (invoice.getPatientCode() != null) {
            info.getChildren().add(makeRow("Patient Code:", invoice.getPatientCode()));
        }
        info.getChildren().add(makeRow("Date & Time:", invoice.getIssuedAt() != null ? invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "Today"));
        if (payment != null && payment.getMethod() != null) {
            info.getChildren().add(makeRow("Payment Method:", payment.getMethod()));
        }

        VBox itemsBox = new VBox(4);
        itemsBox.setPadding(new Insets(8, 0, 8, 0));

        Label itemsTitle = new Label("CHARGE BREAKDOWN");
        itemsTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        itemsBox.getChildren().add(itemsTitle);

        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            for (InvoiceItemResponse item : invoice.getItems()) {
                String name = item.getDescription() == null ? "Item" : item.getDescription();
                String price = item.getLineTotal() != null ? CURRENCY_FORMAT.format(item.getLineTotal()) : "$0.00";
                itemsBox.getChildren().add(makeRow("• " + name + " (x" + item.getQuantity() + ")", price));
            }
        } else {
            itemsBox.getChildren().add(makeRow("Consultation & Service Charges", CURRENCY_FORMAT.format(invoice.getTotalAmount())));
        }

        BigDecimal total = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();

        VBox summaryBox = new VBox(4);
        summaryBox.getChildren().add(makeRow("Total Amount:", CURRENCY_FORMAT.format(total), true));

        if (payment != null) {
            BigDecimal paid = payment.getAmount() == null ? total : payment.getAmount();
            summaryBox.getChildren().add(makeRow("Amount Received:", CURRENCY_FORMAT.format(paid)));

            if (paid.compareTo(total) > 0) {
                BigDecimal change = paid.subtract(total);
                summaryBox.getChildren().add(makeRow("Change Due:", CURRENCY_FORMAT.format(change)));
            }
        }

        Label footer = new Label("Thank you for choosing Smart Clinic!\nWish you good health.");
        footer.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af; -fx-alignment: center;");
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setAlignment(Pos.CENTER);

        box.getChildren().addAll(
                header,
                new Separator(),
                info,
                new Separator(),
                itemsBox,
                new Separator(),
                summaryBox,
                new Separator(),
                footer
        );

        return box;
    }

    private static HBox makeRow(String labelText, String valueText) {
        return makeRow(labelText, valueText, false);
    }

    private static HBox makeRow(String labelText, String valueText, boolean isBold) {
        HBox row = new HBox();
        Label label = new Label(labelText);
        Label value = new Label(valueText);

        if (isBold) {
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #111827;");
            value.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #059669;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
            value.setStyle("-fx-font-size: 12px; -fx-text-fill: #1f2937;");
        }

        HBox.setHgrow(label, Priority.ALWAYS);
        row.getChildren().addAll(label, value);
        return row;
    }
}
