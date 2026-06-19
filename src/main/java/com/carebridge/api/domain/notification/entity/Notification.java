package com.carebridge.api.domain.notification.entity;

import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(nullable = false)
    private String triggerType;

    @Column(nullable = false)
    private String message;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sopGuide;

    @Column(nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(Senior senior, String triggerType, String message, String sopGuide) {
        this.senior = senior;
        this.triggerType = triggerType;
        this.message = message;
        this.sopGuide = sopGuide;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}