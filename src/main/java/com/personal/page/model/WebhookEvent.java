package com.personal.page.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "webhook_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;
}