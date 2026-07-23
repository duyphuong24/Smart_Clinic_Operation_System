package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.DoctorApiClient;
import com.smartclinic.desktop.api.QueueApiClient;
import com.smartclinic.desktop.api.RoomApiClient;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.DoctorResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.dto.RoomResponse;
import com.smartclinic.desktop.dto.WalkInQueueRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QueueDesktopService {

    private static final int PATIENT_LOOKUP_SIZE = 200;

    private final QueueApiClient queueApiClient;
    private final DoctorApiClient doctorApiClient;
    private final RoomApiClient roomApiClient;
    private final PatientDesktopService patientDesktopService;

    public QueueDesktopService(
            QueueApiClient queueApiClient,
            DoctorApiClient doctorApiClient,
            RoomApiClient roomApiClient,
            PatientDesktopService patientDesktopService
    ) {
        this.queueApiClient = queueApiClient;
        this.doctorApiClient = doctorApiClient;
        this.roomApiClient = roomApiClient;
        this.patientDesktopService = patientDesktopService;
    }

    public CompletableFuture<QueueItemResponse> createWalkIn(WalkInQueueRequest request) {
        return queueApiClient.createWalkIn(request)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<List<DoctorResponse>> getActiveDoctors() {
        return doctorApiClient.findAll()
                .thenApply(ApiResponse::getData)
                .thenApply(doctors -> doctors.stream().filter(DoctorResponse::isActive).toList());
    }

    public CompletableFuture<List<RoomResponse>> getActiveRooms() {
        return roomApiClient.findAll()
                .thenApply(ApiResponse::getData)
                .thenApply(rooms -> rooms.stream().filter(RoomResponse::isActive).toList());
    }

    public CompletableFuture<List<PatientResponse>> getActivePatients() {
        return patientDesktopService.search("", 0, PATIENT_LOOKUP_SIZE)
                .thenApply(page -> page.getItems().stream().filter(PatientResponse::isActive).toList());
    }

    public CompletableFuture<List<QueueItemResponse>> findActiveQueue(LocalDate date, Long doctorId) {
        return queueApiClient.findActive(date, doctorId)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<QueueItemResponse> call(Long queueItemId) {
        return queueApiClient.call(queueItemId)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<QueueItemResponse> startService(Long queueItemId) {
        return queueApiClient.startService(queueItemId)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<QueueItemResponse> done(Long queueItemId) {
        return queueApiClient.done(queueItemId)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<QueueItemResponse> skip(Long queueItemId) {
        return skip(queueItemId, null);
    }

    public CompletableFuture<QueueItemResponse> skip(Long queueItemId, String reason) {
        return queueApiClient.skip(queueItemId, reason)
                .thenApply(ApiResponse::getData);
    }
}
