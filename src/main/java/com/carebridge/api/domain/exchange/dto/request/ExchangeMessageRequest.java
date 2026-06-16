package com.carebridge.api.domain.exchange.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExchangeMessageRequest {
    private Long receiverId;
    private String messageType;
    private String content;
}