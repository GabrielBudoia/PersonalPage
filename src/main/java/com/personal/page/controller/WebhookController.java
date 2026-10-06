package com.personal.page.controller;

import com.personal.page.service.PaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WebhookController {

    private final PaymentService paymentService;
    private final String webhookSecret;

    public WebhookController(PaymentService paymentService,
                             @Value("${stripe.webhook-secret}") String webhookSecret) {
        this.paymentService = paymentService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handle(@RequestBody String payload,
                                         @RequestHeader("Stripe-Signature") String signature) {

        // 1. Prove the request really came from Stripe
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature");
            return ResponseEntity.badRequest().body("invalid signature");
        }

        // 2. Only react to the event we care about
        if ("checkout.session.completed".equals(event.getType())) {
            Session session;
            try {
                session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                log.error("Could not read session from event {}", event.getId(), e);
                return ResponseEntity.badRequest().body("unreadable event");
            }

            if ("paid".equals(session.getPaymentStatus())) {
                paymentService.handleCheckoutCompleted(event.getId(), event.getType(), session.getId());
            }
        }

        // 3. Tell Stripe we received it, so it doesn't retry
        return ResponseEntity.ok("received");
    }
}