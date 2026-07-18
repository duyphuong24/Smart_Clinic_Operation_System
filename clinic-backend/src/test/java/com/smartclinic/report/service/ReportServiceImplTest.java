package com.smartclinic.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.payment.entity.PaymentStatus;
import com.smartclinic.payment.repository.PaymentRepository;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ReportServiceImpl service;

    @Test
    void dashboardMetricsShouldAggregateOperationalCounts() {
        when(appointmentRepository.countByScheduledStartBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(12L);
        when(queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(eq(LocalDate.now()), any()))
                .thenReturn(List.of(new QueueItem(), new QueueItem()));
        when(visitRepository.countByStatus(VisitStatus.COMPLETED)).thenReturn(8L);
        when(paymentRepository.sumAmountByPaidAtBetweenAndStatus(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
                .thenReturn(BigDecimal.valueOf(3250000));

        var response = service.dashboardMetrics();

        assertThat(response.getTodayAppointments()).isEqualTo(12L);
        assertThat(response.getActiveQueueItems()).isEqualTo(2L);
        assertThat(response.getCompletedVisits()).isEqualTo(8L);
        assertThat(response.getTodayRevenue()).isEqualByComparingTo("3250000");
    }
}