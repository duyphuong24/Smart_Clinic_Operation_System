package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.AppointmentApiClient;
import com.smartclinic.desktop.api.QueueApiClient;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.AppointmentCheckInRequest;
import com.smartclinic.desktop.dto.AppointmentResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppointmentDesktopService {

    private final AppointmentApiClient appointmentApiClient;
    private final QueueApiClient queueApiClient;

    public AppointmentDesktopService(AppointmentApiClient appointmentApiClient, QueueApiClient queueApiClient) {
        this.appointmentApiClient = appointmentApiClient;
        this.queueApiClient = queueApiClient;
    }

    public CompletableFuture<List<AppointmentResponse>> getTodayAppointments() {
        return findByDate(LocalDate.now());
    }

    public CompletableFuture<List<AppointmentResponse>> findByDate(LocalDate date) {
        return appointmentApiClient.findByDate(date)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<QueueItemResponse> checkIn(Long appointmentId) {
        AppointmentCheckInRequest request = new AppointmentCheckInRequest(appointmentId);
        return queueApiClient.checkIn(request)
                .thenApply(ApiResponse::getData);
    }
}
