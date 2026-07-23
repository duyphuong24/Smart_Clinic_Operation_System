package com.smartclinic.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private QueueItemRepository queueItemRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void dashboardMetrics_ShouldReturnMetricsWithFallbackValues() {
        when(appointmentRepository.countByScheduledStartBetween(any(), any())).thenReturn(0L);
        when(appointmentRepository.count()).thenReturn(5L);

        when(queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(any(), any())).thenReturn(List.of());
        when(queueItemRepository.count()).thenReturn(2L);

        when(visitRepository.countByStatus(VisitStatus.COMPLETED)).thenReturn(0L);
        when(visitRepository.count()).thenReturn(10L);

        when(paymentRepository.sumAmountByPaidAtBetweenAndStatus(any(), any(), eq(PaymentStatus.SUCCESS))).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumTotalRevenue(PaymentStatus.SUCCESS)).thenReturn(new BigDecimal("500000.00"));

        DashboardMetricsResponse metrics = reportService.dashboardMetrics();

        assertNotNull(metrics);
        assertEquals(5L, metrics.getTodayAppointments());
        assertEquals(2, metrics.getActiveQueueItems());
        assertEquals(10L, metrics.getCompletedVisits());
        assertEquals(new BigDecimal("500000.00"), metrics.getTodayRevenue());
    }
}