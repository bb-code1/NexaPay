package com.nexapay.audit.service;

import com.nexapay.audit.entity.AuditEventEntity;
import com.nexapay.audit.repository.AuditEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public AuditEventEntity logEvent(String eventType, String entityType, String entityId, String actorId, String actorRole, String payload) {
        AuditEventEntity event = new AuditEventEntity(
                UUID.randomUUID().toString(),
                eventType,
                entityType,
                entityId,
                actorId,
                actorRole,
                payload
        );
        return auditEventRepository.save(event);
    }

    @Async
    public void logEventAsync(String eventType, String entityType, String entityId, String actorId, String actorRole, String payload) {
        logEvent(eventType, entityType, entityId, actorId, actorRole, payload);
    }

    public List<AuditEventEntity> getAuditTrailForEntity(String entityId) {
        return auditEventRepository.findByEntityIdOrderByTimestampDesc(entityId, PageRequest.of(0, 50));
    }
}
