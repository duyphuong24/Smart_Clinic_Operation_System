package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TodayAppointmentsController implements NavigationAware {

    private final AppointmentDesktopService appointmentService;

    @FXML
    private Label statusLabel;

    public TodayAppointmentsController(AppointmentDesktopService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void onNavigate() {
        statusLabel.setText("Ready to load data from GET /api/v1/appointments/today.");
    }
}
