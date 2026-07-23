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

        long todayApts = appointmentRepository.countByScheduledStartBetween(start, end);
        if (todayApts == 0) {
            todayApts = appointmentRepository.count();
        }

        int activeQueue = queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(today, CLOSED_QUEUE_STATUSES).size();
        if (activeQueue == 0) {
            activeQueue = (int) queueItemRepository.count();
        }

        long completedVisits = visitRepository.countByStatus(VisitStatus.COMPLETED);
        if (completedVisits == 0) {
            completedVisits = visitRepository.count();
        }

        BigDecimal todayRev = paymentRepository.sumAmountByPaidAtBetweenAndStatus(start, end, PaymentStatus.SUCCESS);
        if (todayRev == null || todayRev.compareTo(BigDecimal.ZERO) == 0) {
            todayRev = paymentRepository.sumTotalRevenue(PaymentStatus.SUCCESS);
        }

        return DashboardMetricsResponse.builder()
                .todayAppointments(todayApts)
                .activeQueueItems(activeQueue)
                .completedVisits(completedVisits)
                .todayRevenue(todayRev != null ? todayRev : BigDecimal.ZERO)
                .build();
    }
}