package com.carebridge.api.domain.exchange.entity;

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
public class ExchangeMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Senior sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Senior receiver;

    @Column(nullable = false)
    private String messageType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String status;

    @Column(length = 1000)
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String translatedContent;

    @Builder
    public ExchangeMessage(Senior sender, Senior receiver, String messageType, String content, String status, String audioUrl, String translatedContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.content = content;
        this.status = status;
        this.audioUrl = audioUrl;
        this.translatedContent = translatedContent;
    }

    public void changeStatus(String newStatus) {
        this.status = newStatus;
    }
}