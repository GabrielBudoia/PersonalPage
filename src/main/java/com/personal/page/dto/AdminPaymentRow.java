package com.personal.page.dto;

import com.personal.page.model.Payment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record AdminPaymentRow(
        Long id,
        String stripeSessionId,
        String status,
        long amountCents,
        String currency,
        ZonedDateTime createdAt,
        ZonedDateTime paidAt
) {
    public static AdminPaymentRow from(Payment payment, ZoneId zone) {
        return new AdminPaymentRow(
                payment.getId(),
                payment.getStripeSessionId(),
                payment.getStatus().name(),
                payment.getAmountCents(),
                payment.getCurrency(),
                payment.getCreatedAt().atZone(zone),
                payment.getPaidAt() == null ? null : payment.getPaidAt().atZone(zone)
        );
    }
}