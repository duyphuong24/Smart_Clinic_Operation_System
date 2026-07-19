package com.smartclinic.desktop.util;

import com.smartclinic.desktop.controller.CheckInDialogController;
import com.smartclinic.desktop.dto.AppointmentResponse;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class CheckInDialog {

    private CheckInDialog() {
    }

    public static Optional<CheckInDialogController.Result> show(
            AppointmentResponse appointment,
            Window owner
    ) {
        try {
            CheckInDialogController controller = new CheckInDialogController(appointment);
            FXMLLoader loader = new FXMLLoader(
                    CheckInDialog.class.getResource("/fxml/dialogs/check-in-dialog.fxml")
            );
            loader.setController(controller);
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirm Check-in");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                dialogStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            var stylesheet = CheckInDialog.class.getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            return controller.getResult();
        } catch (IOException ex) {
            AlertUtil.showError("Check-in Dialog", ex);
            return Optional.empty();
        }
    }
}
