package com.personal.page.dto;

import java.util.List;

public record AdminDashboard(
        long totalCount,
        long paidCount,
        long paidCents,
        double conversionRate,
        List<StatusSummary> byStatus,
        List<DailySummary> byDay,
        List<AdminPaymentRow> recent
) {
}