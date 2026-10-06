package com.personal.page.repository;

import com.personal.page.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, String> {

    // Returns 1 if the event was new, 0 if it was already recorded
    @Modifying
    @Query(value = """
            INSERT INTO webhook_events (event_id, type)
            VALUES (:eventId, :type)
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("eventId") String eventId, @Param("type") String type);
}