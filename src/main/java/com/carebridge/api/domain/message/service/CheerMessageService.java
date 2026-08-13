package com.carebridge.api.domain.message.service;

import com.carebridge.api.domain.message.entity.CheerMessage;
import com.carebridge.api.domain.message.repository.CheerMessageRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import com.carebridge.api.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheerMessageService {

    private final CheerMessageRepository cheerMessageRepository;
    private final SeniorRepository seniorRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void sendCheerMessage(Long seniorId, String senderName, String messageType, String content, MultipartFile file) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("어르신을 찾을 수 없습니다."));

        String uploadedAudioUrl = null;
        String uploadedImageUrl = null;

        if ("AUDIO".equalsIgnoreCase(messageType) && file != null && !file.isEmpty()) {
            uploadedAudioUrl = s3Uploader.upload(file, "cheer-audio");
        } else if ("IMAGE".equalsIgnoreCase(messageType) && file != null && !file.isEmpty()) {
            uploadedImageUrl = s3Uploader.upload(file, "cheer-image");
        }

        CheerMessage message = CheerMessage.builder()
                .senior(senior)
                .senderName(senderName)
                .messageType(messageType.toUpperCase())
                .content(content)
                .audioUrl(uploadedAudioUrl)
                .imageUrl(uploadedImageUrl)
                .build();

        cheerMessageRepository.save(message);
        log.info("💌 보호자[{}]가 어르신[{}]에게 {} 메시지 전송 완료", senderName, senior.getName(), messageType);
    }

    @Transactional(readOnly = true)
    public List<CheerMessage> getDashboardMessages(Long seniorId) {
        return cheerMessageRepository.findBySeniorIdOrderByCreatedAtDesc(seniorId);
    }
}