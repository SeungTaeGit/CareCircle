package com.carebridge.api.domain.mission.entity;

import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.mission.enums.ToxicCategory;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToxicExpressionLog extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_mission_id")
    private DailyMission dailyMission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_message_id")
    private ExchangeMessage exchangeMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ToxicCategory mainCategory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String detectedContent;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Builder
    public ToxicExpressionLog(Senior senior, DailyMission dailyMission, ExchangeMessage exchangeMessage, ToxicCategory mainCategory, String detectedContent) {
        this.senior = senior;
        this.dailyMission = dailyMission;
        this.exchangeMessage = exchangeMessage;
        this.mainCategory = mainCategory;
        this.detectedContent = detectedContent;
        this.occurredAt = LocalDateTime.now();
    }
}