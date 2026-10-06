package com.personal.page.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_session_id", nullable = false, unique = true)
    private String stripeSessionId;

    @Column(name = "amount_cents", nullable = false)
    private long amountCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentStatusHistory> statusHistory = new ArrayList<>();

    public List<PaymentStatusHistory> getStatusHistory() {
        return List.copyOf(statusHistory);
    }


    public Payment(String stripeSessionId, long amountCents, String currency) {
        this.stripeSessionId = stripeSessionId;
        this.amountCents = amountCents;
        this.currency = currency;
        changeStatus(PaymentStatus.PENDING);
    }


    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    private void changeStatus(PaymentStatus newStatus) {
        statusHistory.add(new PaymentStatusHistory(this, this.status, newStatus));
        this.status = newStatus;
    }

    public void markAsPaid() {
        changeStatus(PaymentStatus.PAID);
        this.paidAt = Instant.now();
    }

}