package com.nexapay.audit.repository;

import com.nexapay.audit.entity.AuditEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {
    List<AuditEventEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, String entityId);
    List<AuditEventEntity> findByEntityIdOrderByTimestampDesc(String entityId, Pageable pageable);
    List<AuditEventEntity> findByActorIdOrderByTimestampDesc(String actorId, Pageable pageable);
}
