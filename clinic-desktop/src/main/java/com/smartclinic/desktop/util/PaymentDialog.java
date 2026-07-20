package com.smartclinic.desktop.util;

import com.smartclinic.desktop.controller.PaymentDialogController;
import com.smartclinic.desktop.dto.InvoiceResponse;
import com.smartclinic.desktop.dto.PaymentResponse;
import com.smartclinic.desktop.service.PaymentDesktopService;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class PaymentDialog {

    private PaymentDialog() {
    }

    public static Optional<PaymentResponse> show(
            InvoiceResponse invoice,
            PaymentDesktopService paymentService,
            Window owner
    ) {
        try {
            PaymentDialogController controller = new PaymentDialogController(invoice, paymentService);
            FXMLLoader loader = new FXMLLoader(
                    PaymentDialog.class.getResource("/fxml/dialogs/payment-dialog.fxml")
            );
            loader.setController(controller);
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirm Payment");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                dialogStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            var stylesheet = PaymentDialog.class.getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            return controller.getResult();
        } catch (IOException ex) {
            AlertUtil.showError("Payment Dialog", ex);
            return Optional.empty();
        }
    }
}
