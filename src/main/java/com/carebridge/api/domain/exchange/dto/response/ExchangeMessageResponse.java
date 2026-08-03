package com.carebridge.api.domain.exchange.dto.response;

import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExchangeMessageResponse {
    private Long messageId;
    private String senderName;
    private String messageType;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private String audioUrl;
    private String translatedContent;
    private String imageUrl;

    public ExchangeMessageResponse(ExchangeMessage message) {
        this.messageId = message.getId();
        this.senderName = message.getSender().getName();
        this.messageType = message.getMessageType();
        this.content = message.getContent();
        this.status = message.getStatus();
        this.createdAt = message.getCreatedAt();
        this.audioUrl = message.getAudioUrl();
        this.translatedContent = message.getTranslatedContent();
        this.imageUrl = message.getImageUrl();
    }
}