package com.smartclinic.desktop.util;

import com.smartclinic.desktop.controller.InvoiceDetailDialogController;
import com.smartclinic.desktop.dto.InvoiceResponse;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class InvoiceDetailDialog {

    private InvoiceDetailDialog() {
    }

    public static void show(InvoiceResponse invoice, Window owner) {
        try {
            InvoiceDetailDialogController controller = new InvoiceDetailDialogController(invoice);
            FXMLLoader loader = new FXMLLoader(
                    InvoiceDetailDialog.class.getResource("/fxml/dialogs/invoice-detail-dialog.fxml")
            );
            loader.setController(controller);
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Invoice Details - " + invoice.getInvoiceNumber());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                dialogStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            var stylesheet = InvoiceDetailDialog.class.getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (IOException ex) {
            AlertUtil.showError("Invoice Detail Dialog", ex);
        }
    }
}
