package com.personal.page.repository;

import com.personal.page.dto.DailySummary;
import com.personal.page.dto.StatusSummary;
import com.personal.page.model.Payment;
import com.personal.page.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    List<Payment> findTop20ByOrderByCreatedAtDesc();

    List<Payment> findTop20ByStatusOrderByCreatedAtDesc(PaymentStatus status);

    // JPQL: works on entities, the result goes straight into the record
    @Query("""
            SELECT new com.personal.page.dto.StatusSummary(
                       p.status, COUNT(p), COALESCE(SUM(p.amountCents), 0L))
            FROM Payment p
            GROUP BY p.status
            ORDER BY p.status
            """)
    List<StatusSummary> summarizeByStatus();

    // Native SQL: uses PostgreSQL features (AT TIME ZONE, FILTER)
    @Query(value = """
            SELECT to_char(created_at AT TIME ZONE 'Europe/Madrid', 'YYYY-MM-DD') AS "day",
                   COUNT(*)                                                       AS "created",
                   COUNT(*) FILTER (WHERE status = 'PAID')                        AS "paid",
                   CAST(COALESCE(SUM(amount_cents) FILTER (WHERE status = 'PAID'), 0) AS BIGINT)
                                                                                  AS "paidCents"
            FROM payments
            WHERE created_at >= now() - INTERVAL '30 days'
            GROUP BY 1
            ORDER BY 1 DESC
            """, nativeQuery = true)
    List<DailySummary> summarizeLast30Days();
}