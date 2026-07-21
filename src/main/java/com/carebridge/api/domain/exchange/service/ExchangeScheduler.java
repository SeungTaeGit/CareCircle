//package com.carebridge.api.domain.exchange.service;
//
//import com.carebridge.api.domain.admin.entity.DangerSignal;
//import com.carebridge.api.domain.admin.entity.enums.DangerType;
//import com.carebridge.api.domain.admin.repository.DangerSignalRepository;
//import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
//import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
//import com.carebridge.api.domain.notification.constant.SopRule;
//import com.carebridge.api.domain.notification.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ExchangeScheduler {
//
//    private final ExchangeMessageRepository exchangeMessageRepository;
//    private final NotificationService notificationService;
//
//    private final DangerSignalRepository dangerSignalRepository;
//
//    @Scheduled(cron = "0 0 * * * *")
//    @Transactional
//    public void checkDelayedMessages() {
//        log.info("🚓 [스케줄러 실행] 응답 지연 교류 메시지 순찰을 시작합니다...");
//
//        LocalDateTime fortyEightHoursAgo = LocalDateTime.now().minusHours(48);
//
//        List<ExchangeMessage> delayedMessages = exchangeMessageRepository
//                .findAllByStatusAndCreatedAtBefore("UNREAD", fortyEightHoursAgo);
//
//        for (ExchangeMessage message : delayedMessages) {
//
//            message.changeStatus("DELAY_ALERTED");
//
//            notificationService.createSopNotification(message.getReceiver(), SopRule.EXCHANGE_ISOLATION);
//
//            DangerSignal dangerSignal = DangerSignal.builder()
//                    .senior(message.getReceiver())
//                    .dangerType(DangerType.LONG_TERM_OFFLINE)
//                    .description("48시간 이상 교류 메시지 미확인 (고립 의심)")
//                    .build();
//
//            dangerSignalRepository.save(dangerSignal);
//
//            log.info("🚨 [응답 지연 감지] 어르신 ID: {} 님에게 고립 알림 발송 및 관리자 위험 신호 등록 완료.", message.getReceiver().getId());
//        }
//
//        log.info("✅ [스케줄러 종료] 순찰 완료. 총 {}건의 지연 감지.", delayedMessages.size());
//    }
//}