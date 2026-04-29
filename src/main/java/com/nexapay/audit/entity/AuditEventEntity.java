package com.nexapay.audit.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType;

    @Column(name = "entity_type", length = 30, nullable = false)
    private String entityType;

    @Column(name = "entity_id", length = 50, nullable = false)
    private String entityId;

    @Column(name = "actor_id", length = 50, nullable = false)
    private String actorId;

    @Column(name = "actor_role", length = 30, nullable = false)
    private String actorRole;

    @Column(columnDefinition = "JSONB")
    private String payload;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    public AuditEventEntity() {}

    public AuditEventEntity(String id, String eventType, String entityType, String entityId, String actorId, String actorRole, String payload) {
        this.id = id;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.payload = payload != null ? payload : "{}";
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
