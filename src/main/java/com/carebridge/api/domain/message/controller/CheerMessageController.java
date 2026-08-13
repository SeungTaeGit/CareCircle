package com.carebridge.api.domain.message.controller;

import com.carebridge.api.domain.message.entity.CheerMessage;
import com.carebridge.api.domain.message.service.CheerMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class CheerMessageController {

    private final CheerMessageService cheerMessageService;

    @PostMapping("/send/{seniorId}")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long seniorId,
            @RequestParam("senderName") String senderName,
            @RequestParam("messageType") String messageType,
            @RequestParam(value = "content", required = false) String content,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        cheerMessageService.sendCheerMessage(seniorId, senderName, messageType, content, file);
        return ResponseEntity.ok("메시지가 성공적으로 전송되었습니다.");
    }

    @GetMapping("/{seniorId}")
    public ResponseEntity<List<CheerMessage>> getMessages(@PathVariable Long seniorId) {
        List<CheerMessage> messages = cheerMessageService.getDashboardMessages(seniorId);
        return ResponseEntity.ok(messages);
    }
}