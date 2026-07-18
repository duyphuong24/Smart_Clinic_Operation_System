package com.smartclinic.appointment.service;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.dto.AppointmentRescheduleRequest;
import com.smartclinic.appointment.dto.AppointmentResponse;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<AppointmentResponse> findAll(LocalDate date);

    AppointmentResponse getById(Long id);

    AppointmentResponse create(AppointmentRequest request);

    AppointmentResponse reschedule(Long id, AppointmentRescheduleRequest request);

    AppointmentResponse cancel(Long id, String reason);
}