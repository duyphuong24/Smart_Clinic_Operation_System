package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;

/**
 * Mẫu FXML Controller hướng dẫn cho Member 3 tích hợp gọi API không đồng bộ.
 * 
 * Quy tắc:
 * 1. Không dùng HTTP Client hay Repository trực tiếp trong Controller.
 * 2. Luôn bọc logic UI cập nhật trong Platform.runLater().
 * 3. Bắt lỗi API Exception và hiển thị Alert đẹp mắt.
 */
public class TodayAppointmentControllerSample {

    private final AppointmentDesktopService appointmentService;

    @FXML
    private TableView<AppointmentResponse> appointmentTable;

    public TodayAppointmentControllerSample(AppointmentDesktopService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Tải danh sách lịch hẹn hôm nay.
     */
    public void loadTodayAppointments() {
        appointmentService.getTodayAppointments()
                .thenAccept(appointments -> Platform.runLater(() -> {
                    appointmentTable.getItems().clear();
                    appointmentTable.getItems().addAll(appointments);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showErrorAlert("Lỗi tải lịch hẹn", ex.getMessage()));
                    return null;
                });
    }

    /**
     * Xử lý check-in khi bấm nút.
     */
    public void handleCheckIn(AppointmentResponse selectedAppointment) {
        if (selectedAppointment == null) {
            showWarningAlert("Chú ý", "Vui lòng chọn một lịch hẹn!");
            return;
        }

        appointmentService.checkIn(selectedAppointment.getId())
                .thenAccept(queueItem -> Platform.runLater(() -> {
                    showInfoAlert("Thành công", "Đã check-in thành công bệnh nhân! Số thứ tự hàng đợi: " + queueItem.getQueueNumber());
                    loadTodayAppointments(); // Refresh list
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showErrorAlert("Lỗi check-in", ex.getMessage()));
                    return null;
                });
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
