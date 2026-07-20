package com.smartclinic.desktop.util;

import com.smartclinic.desktop.controller.BookAppointmentDialogController;
import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.service.QueueDesktopService;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class BookAppointmentDialog {

    private BookAppointmentDialog() {
    }

    public static Optional<AppointmentResponse> show(
            QueueDesktopService queueService,
            AppointmentDesktopService appointmentService,
            Window owner
    ) {
        try {
            BookAppointmentDialogController controller = new BookAppointmentDialogController(queueService, appointmentService);
            FXMLLoader loader = new FXMLLoader(
                    BookAppointmentDialog.class.getResource("/fxml/dialogs/book-appointment-dialog.fxml")
            );
            loader.setController(controller);
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Book Appointment");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                dialogStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            var stylesheet = BookAppointmentDialog.class.getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            return controller.getResult();
        } catch (IOException ex) {
            AlertUtil.showError("Book Appointment Dialog", ex);
            return Optional.empty();
        }
    }
}
