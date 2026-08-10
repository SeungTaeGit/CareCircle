package com.carebridge.api.domain.admin.dto.response;

import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ExchangeHistoryResponse {
    private Long id;
    private String sender;
    private String country;
    private String type;
    private String content;
    private String translated;
    private String date;

    public static ExchangeHistoryResponse from(ExchangeMessage message) {
        String formattedDate = "";
        if (message.getCreatedAt() != null) {
            formattedDate = message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        return ExchangeHistoryResponse.builder()
                .id(message.getId())
                .sender(message.getSender().getName())
                .country(message.getSender().getCountry())
                .type(message.getMessageType())
                .content(message.getContent())
                .translated(message.getTranslatedContent())
                .date(formattedDate)
                .build();
    }
}