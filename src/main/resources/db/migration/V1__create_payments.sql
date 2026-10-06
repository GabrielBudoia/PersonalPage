CREATE TABLE payments (
                          id                 BIGSERIAL    PRIMARY KEY,
                          stripe_session_id  VARCHAR(255) NOT NULL UNIQUE,
                          amount_cents       BIGINT       NOT NULL CHECK (amount_cents > 0),
                          currency           VARCHAR(3)   NOT NULL,
                          status             VARCHAR(20)  NOT NULL
                              CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'EXPIRED')),
                          created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          paid_at            TIMESTAMPTZ
);

CREATE INDEX idx_payments_status     ON payments (status);
CREATE INDEX idx_payments_created_at ON payments (created_at);