package com.carebridge.api.domain.exchange.service;

import com.carebridge.api.domain.ai.dto.AiResultDto;
import com.carebridge.api.domain.ai.service.CareAiService;
import com.carebridge.api.domain.exchange.dto.request.ExchangeMessageRequest;
import com.carebridge.api.domain.exchange.dto.response.ExchangeMessageResponse;
import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import com.carebridge.api.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeMessageService {

    private final ExchangeMessageRepository exchangeMessageRepository;
    private final SeniorRepository seniorRepository;
    private final S3Uploader s3Uploader;
    private final CareAiService careAiService;

    @Transactional
    public void sendMessage(Long senderId, ExchangeMessageRequest request, MultipartFile audioFile) {

        Senior sender = seniorRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신자 어르신 정보를 찾을 수 없습니다."));
        Senior receiver = seniorRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자 어르신 정보를 찾을 수 없습니다."));

        sender.updateLastActiveAt();

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 메시지를 보낼 수 없습니다.");
        }

        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("음성 파일이 필수입니다.");
        }

        String uploadedAudioUrl = s3Uploader.upload(audioFile, "exchange");

        try {
            String targetLanguage = receiver.getLanguage();

            if (targetLanguage == null || targetLanguage.isBlank()) {
                targetLanguage = "JP".equalsIgnoreCase(receiver.getCountry()) ? "일본어" : "한국어";
            }

            AiResultDto aiResult = careAiService.analyzeAudio(audioFile.getBytes(), targetLanguage);

            log.info("📊 [대시보드 전송 데이터] {} 어르신의 감정 상태: {}", sender.getName(), aiResult.getEmotionWeights());


            if (aiResult.isHarmful()) {
                log.warn("🚨 [차단됨] 부적절한 표현 감지! 원본 STT: {}", aiResult.getStt());
                throw new IllegalArgumentException("부적절한 표현이 감지되어 메시지가 전송되지 않았습니다.");
            }

            ExchangeMessage message = ExchangeMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .messageType(request.getMessageType())
                    .content(aiResult.getStt())
                    .translatedContent(aiResult.getTranslatedText())
                    .audioUrl(uploadedAudioUrl)
                    .status("UNREAD")
                    .build();

            exchangeMessageRepository.save(message);
            log.info("✅ [전송 완료] {} 번역본: {}", targetLanguage, aiResult.getTranslatedText());

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void sendTextMessage(Long senderId, ExchangeMessageRequest request) {
        Senior sender = getSeniorById(senderId);
        Senior receiver = getSeniorById(request.getReceiverId());
        validateSenderAndReceiver(sender, receiver);

        sender.updateLastActiveAt();

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("텍스트 내용이 필수입니다.");
        }

        try {
            String targetLanguage = determineTargetLanguage(receiver);

            AiResultDto aiResult = careAiService.analyzeText(request.getContent(), targetLanguage);

            if (aiResult.isHarmful()) {
                throw new IllegalArgumentException("부적절한 표현이 감지되어 메시지가 전송되지 않았습니다.");
            }

            ExchangeMessage message = ExchangeMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .messageType("TEXT")
                    .content(request.getContent())
                    .translatedContent(aiResult.getTranslatedText())
                    .status("UNREAD")
                    .build();

            exchangeMessageRepository.save(message);

        } catch (Exception e) {
            log.error("텍스트 메시지 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void sendImageMessage(Long senderId, ExchangeMessageRequest request, MultipartFile imageFile) {
        Senior sender = getSeniorById(senderId);
        Senior receiver = getSeniorById(request.getReceiverId());
        validateSenderAndReceiver(sender, receiver);

        sender.updateLastActiveAt();

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 필수입니다.");
        }

        String uploadedImageUrl = s3Uploader.upload(imageFile, "exchange-images");

        try {
            String targetLanguage = determineTargetLanguage(receiver);

            String originalContent = request.getContent() != null ? request.getContent() : "사진을 보냈습니다.";
            AiResultDto aiResult = careAiService.analyzeText(originalContent, targetLanguage);

            ExchangeMessage message = ExchangeMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .messageType("IMAGE")
                    .content(originalContent)
                    .translatedContent(aiResult.getTranslatedText())
                    .imageUrl(uploadedImageUrl)
                    .status("UNREAD")
                    .build();

            exchangeMessageRepository.save(message);

        } catch (Exception e) {
            log.error("이미지 메시지 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
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

    private Senior getSeniorById(Long id) {
        return seniorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("어르신 정보를 찾을 수 없습니다. (ID: " + id + ")"));
    }

    private void validateSenderAndReceiver(Senior sender, Senior receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 메시지를 보낼 수 없습니다.");
        }
    }

    private String determineTargetLanguage(Senior receiver) {
        String targetLanguage = receiver.getLanguage();
        if (targetLanguage == null || targetLanguage.isBlank()) {
            return "JP".equalsIgnoreCase(receiver.getCountry()) ? "일본어" : "한국어";
        }
        return targetLanguage;
    }
}