package com.project.app.entity;


import com.project.app.enums.AuditAction;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuditAction action;
    private String entityName; // e.g., User, Product
    private Long entityId; // ID of the affected entity
    private String details; // Additional details about the change

    @CreatedBy
    private String createdBy; // Username of the user who performed the action

    @CreatedDate
    private LocalDateTime createdAt; // Timestamp of the action
}
