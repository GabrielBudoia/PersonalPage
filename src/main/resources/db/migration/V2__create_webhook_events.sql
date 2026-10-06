CREATE TABLE webhook_events (
                                event_id     VARCHAR(255) PRIMARY KEY,
                                type         VARCHAR(100) NOT NULL,
                                received_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);