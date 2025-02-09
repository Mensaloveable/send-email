package com.project.app.service;

import com.project.app.entity.AuditLog;
import com.project.app.enums.AuditAction;
import com.project.app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityName, Long entityId, String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(AuditAction.valueOf(action))
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
    }
}