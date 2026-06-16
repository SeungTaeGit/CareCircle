package com.carebridge.api.domain.exchange.controller;

import com.carebridge.api.domain.exchange.dto.request.ExchangeMessageRequest;
import com.carebridge.api.domain.exchange.dto.response.ExchangeMessageResponse;
import com.carebridge.api.domain.exchange.service.ExchangeMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeMessageController {

    private final ExchangeMessageService exchangeMessageService;

    @PostMapping
    public ResponseEntity<String> sendMessage(
            Authentication authentication,
            @RequestBody ExchangeMessageRequest request) {

        String senderIdString = authentication.getName();

        exchangeMessageService.sendMessage(senderIdString, request);

        return ResponseEntity.ok("교류 메시지가 성공적으로 전송되었습니다.");
    }

    @GetMapping("/received")
    public ResponseEntity<List<ExchangeMessageResponse>> getReceivedMessages(
            Authentication authentication) {

        String receiverIdString = authentication.getName();

        List<ExchangeMessageResponse> response = exchangeMessageService.getReceivedMessages(receiverIdString);

        return ResponseEntity.ok(response);
    }
}