package com.smartclinic.desktop.util;

import com.smartclinic.desktop.controller.PatientFormDialogController;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.service.PatientDesktopService;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class PatientFormDialog {

    private PatientFormDialog() {
    }

    public static Optional<PatientResponse> showCreate(
            PatientDesktopService patientService,
            Window owner
    ) {
        return show(patientService, null, owner);
    }

    public static Optional<PatientResponse> showEdit(
            PatientDesktopService patientService,
            PatientResponse patientToEdit,
            Window owner
    ) {
        return show(patientService, patientToEdit, owner);
    }

    private static Optional<PatientResponse> show(
            PatientDesktopService patientService,
            PatientResponse patientToEdit,
            Window owner
    ) {
        try {
            PatientFormDialogController controller = new PatientFormDialogController(patientService, patientToEdit);
            FXMLLoader loader = new FXMLLoader(
                    PatientFormDialog.class.getResource("/fxml/dialogs/patient-form-dialog.fxml")
            );
            loader.setController(controller);
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(patientToEdit == null ? "Register New Patient" : "Edit Patient Profile");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                dialogStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            var stylesheet = PatientFormDialog.class.getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            return controller.getResult();
        } catch (IOException ex) {
            AlertUtil.showError("Patient Form Dialog", ex);
            return Optional.empty();
        }
    }
}
