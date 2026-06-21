package com.carebridge.api.domain.notification.controller;

import com.carebridge.api.domain.notification.dto.response.NotificationResponse;
import com.carebridge.api.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        List<NotificationResponse> response = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("알림이 성공적으로 확인 처리되었습니다.");
    }
}