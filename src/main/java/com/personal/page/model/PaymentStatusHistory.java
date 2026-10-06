package com.personal.page.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "payment_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private PaymentStatus newStatus;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;

    PaymentStatusHistory(Payment payment, PaymentStatus oldStatus, PaymentStatus newStatus) {
        this.payment = payment;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = Instant.now();
    }
}