package com.carebridge.api.domain.notification.service;

import com.carebridge.api.domain.notification.constant.SopRule;
import com.carebridge.api.domain.notification.entity.Notification;
import com.carebridge.api.domain.notification.repository.NotificationRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createSopNotification(Senior senior, SopRule rule) {

        Notification notification = Notification.builder()
                .senior(senior)
                .triggerType(rule.name())
                .message(rule.getMessage())
                .sopGuide(rule.getGuide())
                .build();

        notificationRepository.save(notification);
    }
}