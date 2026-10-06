package com.personal.page.dto;

import com.personal.page.model.Payment;

import java.time.Instant;

public record PaymentResponse(
        String status,
        long amountCents,
        String currency,
        Instant paidAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getStatus().name(),
                payment.getAmountCents(),
                payment.getCurrency(),
                payment.getPaidAt()
        );
    }
}