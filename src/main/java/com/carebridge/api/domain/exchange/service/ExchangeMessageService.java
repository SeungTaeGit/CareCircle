package com.carebridge.api.domain.exchange.service;

import com.carebridge.api.domain.exchange.dto.request.ExchangeMessageRequest;
import com.carebridge.api.domain.exchange.dto.response.ExchangeMessageResponse;
import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeMessageService {

    private final ExchangeMessageRepository exchangeMessageRepository;
    private final SeniorRepository seniorRepository;

    @Transactional
    public void sendMessage(String senderIdString, ExchangeMessageRequest request) {

        Long senderId = Long.parseLong(senderIdString);
        Senior sender = seniorRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신자 어르신 정보를 찾을 수 없습니다."));

        Senior receiver = seniorRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자 어르신 정보를 찾을 수 없습니다."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 메시지를 보낼 수 없습니다.");
        }

        String uploadedAudioUrl = null;
        if (audioFile != null && !audioFile.isEmpty()) {
            uploadedAudioUrl = s3Uploader.upload(audioFile, "exchange");
        }

        ExchangeMessage message = ExchangeMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .messageType(request.getMessageType())
                .content(request.getContent())
                .status("UNREAD")
                .build();

        exchangeMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ExchangeMessageResponse> getReceivedMessages(String receiverIdString) {

        Long receiverId = Long.parseLong(receiverIdString);

        List<ExchangeMessage> messages = exchangeMessageRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId);

        return messages.stream()
                .map(ExchangeMessageResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long messageId) {
        ExchangeMessage message = exchangeMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.changeStatus("READ");
    }
}