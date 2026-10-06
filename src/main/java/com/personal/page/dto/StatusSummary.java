package com.personal.page.dto;

import com.personal.page.model.PaymentStatus;

public record StatusSummary(PaymentStatus status, Long count, Long totalCents) {
}