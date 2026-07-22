package com.smartclinic.report.service;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final List<QueueStatus> CLOSED_QUEUE_STATUSES = List.of(QueueStatus.DONE, QueueStatus.SKIPPED);

    private final AppointmentRepository appointmentRepository;
    private final QueueItemRepository queueItemRepository;
    private final VisitRepository visitRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardMetricsResponse dashboardMetrics() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return DashboardMetricsResponse.builder()
                .todayAppointments(appointmentRepository.countByScheduledStartBetween(start, end))
                .activeQueueItems(queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(today, CLOSED_QUEUE_STATUSES).size())
                .completedVisits(visitRepository.countByStatus(VisitStatus.COMPLETED))
                .todayRevenue(paymentRepository.sumAmountByPaidAtBetweenAndStatus(start, end, PaymentStatus.SUCCESS))
                .build();
    }
}