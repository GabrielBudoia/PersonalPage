package com.personal.page.service;

import com.personal.page.model.Payment;
import com.personal.page.model.PaymentStatus;
import com.personal.page.repository.PaymentRepository;
import com.personal.page.repository.WebhookEventRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    private static final long AMOUNT_CENTS = 4990L;   // €49.90
    private static final String CURRENCY = "eur";
    private static final String PRODUCT_NAME = "Demo product";

    private final PaymentRepository paymentRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final String baseUrl;

    public PaymentService(PaymentRepository paymentRepository,
                          WebhookEventRepository webhookEventRepository,
                          @Value("${app.base-url}") String baseUrl) {
        this.paymentRepository = paymentRepository;
        this.webhookEventRepository = webhookEventRepository;
        this.baseUrl = baseUrl;
    }

    public String createCheckout() {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/#demo")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(CURRENCY)
                                .setUnitAmount(AMOUNT_CENTS)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(PRODUCT_NAME)
                                        .build())
                                .build())
                        .build())
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.error("Could not create Stripe checkout session", e);
            throw new IllegalStateException("Payment provider unavailable", e);
        }

        paymentRepository.save(new Payment(session.getId(), AMOUNT_CENTS, CURRENCY));
        log.info("Created PENDING payment for session {}", session.getId());

        return session.getUrl();
    }

    @Transactional
    public void handleCheckoutCompleted(String eventId, String eventType, String sessionId) {
        int inserted = webhookEventRepository.insertIfAbsent(eventId, eventType);
        if (inserted == 0) {
            log.info("Event {} already processed, ignoring", eventId);
            return;
        }
        markAsPaid(sessionId);
    }

    @Transactional
    public void markAsPaid(String sessionId) {
        Optional<Payment> found = paymentRepository.findByStripeSessionId(sessionId);

        if (found.isEmpty()) {
            log.warn("Webhook for unknown session {}", sessionId);
            return;
        }

        Payment payment = found.get();
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("Session {} already PAID, ignoring duplicate event", sessionId);
            return;
        }

        payment.markAsPaid();
        log.info("Payment for session {} marked as PAID", sessionId);
    }

    public Payment findBySessionId(String sessionId) {
        return paymentRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown session " + sessionId));
    }
}