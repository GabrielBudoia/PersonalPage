package com.personal.page.service;

import com.personal.page.dto.AdminDashboard;
import com.personal.page.dto.AdminPaymentRow;
import com.personal.page.dto.StatusSummary;
import com.personal.page.model.Payment;
import com.personal.page.model.PaymentStatus;
import com.personal.page.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Madrid");

    private final PaymentRepository paymentRepository;

    public AdminService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public AdminDashboard buildDashboard(PaymentStatus filter) {
        List<StatusSummary> byStatus = paymentRepository.summarizeByStatus();

        long total = byStatus.stream().mapToLong(StatusSummary::count).sum();
        StatusSummary paid = byStatus.stream()
                .filter(s -> s.status() == PaymentStatus.PAID)
                .findFirst()
                .orElse(new StatusSummary(PaymentStatus.PAID, 0L, 0L));
        double conversionRate = total == 0 ? 0.0 : (double) paid.count() / total;

        List<Payment> recent = (filter == null)
                ? paymentRepository.findTop20ByOrderByCreatedAtDesc()
                : paymentRepository.findTop20ByStatusOrderByCreatedAtDesc(filter);

        List<AdminPaymentRow> rows = recent.stream()
                .map(p -> AdminPaymentRow.from(p, ZONE))
                .toList();

        return new AdminDashboard(
                total, paid.count(), paid.totalCents(), conversionRate,
                byStatus, paymentRepository.summarizeLast30Days(), rows);
    }
}