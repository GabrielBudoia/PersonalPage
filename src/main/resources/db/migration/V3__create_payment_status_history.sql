CREATE TABLE payment_status_history (
                                        id          BIGSERIAL    PRIMARY KEY,
                                        payment_id  BIGINT       NOT NULL REFERENCES payments (id),
                                        old_status  VARCHAR(20),
                                        new_status  VARCHAR(20)  NOT NULL,
                                        changed_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_status_history_payment ON payment_status_history (payment_id);

-- Backfill: rebuild history for payments that already exist
INSERT INTO payment_status_history (payment_id, old_status, new_status, changed_at)
SELECT id, NULL, 'PENDING', created_at FROM payments;

INSERT INTO payment_status_history (payment_id, old_status, new_status, changed_at)
SELECT id, 'PENDING', 'PAID', paid_at FROM payments WHERE status = 'PAID';