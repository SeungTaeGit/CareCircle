package com.carebridge.api.domain.exchange.service;

import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.notification.constant.SopRule;
import com.carebridge.api.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeScheduler {

    private final ExchangeMessageRepository exchangeMessageRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkDelayedMessages() {
        log.info("🚓 [스케줄러 실행] 응답 지연 교류 메시지 순찰을 시작합니다...");

        LocalDateTime fortyEightHoursAgo = LocalDateTime.now().minusHours(48);

        List<ExchangeMessage> delayedMessages = exchangeMessageRepository
                .findAllByStatusAndCreatedAtBefore("UNREAD", fortyEightHoursAgo);

        for (ExchangeMessage message : delayedMessages) {

            message.changeStatus("DELAY_ALERTED");

            notificationService.createSopNotification(message.getReceiver(), SopRule.EXCHANGE_ISOLATION);

            log.info("🚨 [응답 지연 감지] 어르신 ID: {} 님에게 고립 알림이 발송되었습니다.", message.getReceiver().getId());
        }

        log.info("✅ [스케줄러 종료] 순찰 완료. 총 {}건의 지연 감지.", delayedMessages.size());
    }
}