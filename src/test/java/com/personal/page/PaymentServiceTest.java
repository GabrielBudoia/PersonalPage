package com.personal.page;

import com.personal.page.model.Payment;
import com.personal.page.model.PaymentStatus;
import com.personal.page.repository.PaymentRepository;
import com.personal.page.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest extends IntegrationTestBase {

    @Autowired PaymentService paymentService;
    @Autowired PaymentRepository paymentRepository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void duplicateWebhookEventIsProcessedOnlyOnce() {
        String sessionId = "cs_test_" + UUID.randomUUID();
        String eventId = "evt_" + UUID.randomUUID();
        Payment payment = paymentRepository.save(new Payment(sessionId, 4990, "eur"));

        // Stripe delivers the same event twice
        paymentService.handleCheckoutCompleted(eventId, "checkout.session.completed", sessionId);
        paymentService.handleCheckoutCompleted(eventId, "checkout.session.completed", sessionId);

        assertThat(count("SELECT count(*) FROM webhook_events WHERE event_id = ?", eventId))
                .isEqualTo(1);
        // NULL -> PENDING and PENDING -> PAID, never a second PAID row
        assertThat(count("SELECT count(*) FROM payment_status_history WHERE payment_id = ?", payment.getId()))
                .isEqualTo(2);
        assertThat(paymentRepository.findByStripeSessionId(sessionId).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void databaseRejectsNegativeAmount() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO payments (stripe_session_id, amount_cents, currency, status) VALUES (?, -100, 'eur', 'PENDING')",
                "cs_test_" + UUID.randomUUID()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void databaseRejectsUnknownStatus() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO payments (stripe_session_id, amount_cents, currency, status) VALUES (?, 4990, 'eur', 'REFUNDED')",
                "cs_test_" + UUID.randomUUID()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private long count(String sql, Object arg) {
        return jdbc.queryForObject(sql, Long.class, arg);
    }
}