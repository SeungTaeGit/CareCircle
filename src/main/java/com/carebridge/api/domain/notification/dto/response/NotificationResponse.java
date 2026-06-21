package com.carebridge.api.domain.notification.dto.response;

import com.carebridge.api.domain.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private Long notificationId;
    private String seniorName;
    private String triggerType;
    private String message;
    private String sopGuide;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification notification) {
        this.notificationId = notification.getId();
        this.seniorName = notification.getSenior().getName();
        this.triggerType = notification.getTriggerType();
        this.message = notification.getMessage();
        this.sopGuide = notification.getSopGuide();
        this.createdAt = notification.getCreatedAt();
    }
}