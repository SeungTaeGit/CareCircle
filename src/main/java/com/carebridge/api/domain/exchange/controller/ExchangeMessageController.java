package com.carebridge.api.domain.exchange.controller;

import com.carebridge.api.domain.exchange.dto.request.ExchangeMessageRequest;
import com.carebridge.api.domain.exchange.dto.response.ExchangeMessageResponse;
import com.carebridge.api.domain.exchange.service.ExchangeMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeMessageController {

    private final ExchangeMessageService exchangeMessageService;

    @PostMapping("/voice")
    public ResponseEntity<String> sendMessage(
            @AuthenticationPrincipal String loginId,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestPart(value = "data") ExchangeMessageRequest request) {

        Long senderId = Long.parseLong(loginId);

        exchangeMessageService.sendMessage(senderId, request, audioFile);

        return ResponseEntity.ok("메시지가 성공적으로 전송되었습니다.");
    }

    @PostMapping("/text")
    public ResponseEntity<String> sendTextMessage(
            @AuthenticationPrincipal String loginId,
            @RequestBody ExchangeMessageRequest request) {

        Long senderId = Long.parseLong(loginId);
        exchangeMessageService.sendTextMessage(senderId, request);
        return ResponseEntity.ok("텍스트 메시지가 성공적으로 전송되었습니다.");
    }

    @PostMapping("/image")
    public ResponseEntity<String> sendImageMessage(
            @AuthenticationPrincipal String loginId,
            @RequestPart(value = "imageFile") MultipartFile imageFile,
            @RequestPart(value = "data") ExchangeMessageRequest request) {

        Long senderId = Long.parseLong(loginId);
        exchangeMessageService.sendImageMessage(senderId, request, imageFile);
        return ResponseEntity.ok("이미지 메시지가 성공적으로 전송되었습니다.");
    }

    @GetMapping("/received")
    public ResponseEntity<List<ExchangeMessageResponse>> getReceivedMessages(
            Authentication authentication) {

        String receiverIdString = authentication.getName();

        List<ExchangeMessageResponse> response = exchangeMessageService.getReceivedMessages(receiverIdString);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long messageId) {
        exchangeMessageService.markAsRead(messageId);
        return ResponseEntity.ok("메시지 읽음 처리 완료");
    }
}