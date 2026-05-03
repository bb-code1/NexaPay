package com.nexapay.ai.model.dto;

import java.time.Instant;

public record AuditEventDto(
        String eventId,
        String eventType,
        String entityType,
        String entityId,
        String actorId,
        String actorRole,
        Instant timestamp
) {}
