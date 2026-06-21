package com.carebridge.api.domain.notification.service;

import com.carebridge.api.domain.notification.constant.SopRule;
import com.carebridge.api.domain.notification.dto.response.NotificationResponse;
import com.carebridge.api.domain.notification.entity.Notification;
import com.carebridge.api.domain.notification.repository.NotificationRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {
        return notificationRepository.findAllByIsReadFalseOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        notification.markAsRead();
    }
}