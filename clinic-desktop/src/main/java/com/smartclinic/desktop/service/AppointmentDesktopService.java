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
        return checkIn(appointmentId, "NORMAL");
    }

    public CompletableFuture<QueueItemResponse> checkIn(Long appointmentId, String priority) {
        AppointmentCheckInRequest request = new AppointmentCheckInRequest(appointmentId);
        request.setPriority(priority == null || priority.isBlank() ? "NORMAL" : priority);
        return queueApiClient.checkIn(request)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<AppointmentResponse> create(Object request) {
        return appointmentApiClient.create(request)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<AppointmentResponse> cancel(Long id, String reason) {
        return appointmentApiClient.cancel(id, reason)
                .thenApply(ApiResponse::getData);
    }
}
