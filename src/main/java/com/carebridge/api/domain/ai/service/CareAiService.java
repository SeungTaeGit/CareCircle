package com.carebridge.api.domain.ai.service;

import com.carebridge.api.domain.ai.dto.AiResultDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CareAiService {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api-key}")
    private String apiKey;

    public AiResultDto analyzeAudio(byte[] audioData, String targetLanguage) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String systemInstruction = """
                        너는 노인 돌봄 서비스의 데이터 분석 AI야. 입력된 사용자의 원본 음성을 변형 없이 그대로 바탕으로 하여 아래 4가지 결과를 반드시 JSON 형식으로만 응답해줘.
                        1. stt: 음성을 텍스트로 그대로 받아쓰기
                        2. isHarmful: 원본 텍스트 내용이 욕설, 비하, 공격성 등 유해한지 여부 (boolean)
                        3. emotionWeights: 원본 텍스트에서 느껴지는 실제 감정을 기쁨, 슬픔, 분노, 불안 4가지로 나누고, 총합이 100이 되도록 정수 비율로 응답해 (예: {"기쁨": 10, "슬픔": 10, "분노": 80, "불안": 0})
                        4. translatedText: 원본 텍스트를 바탕으로 문맥을 살려 %s(으)로 자연스럽게 번역한 텍스트
                        """.formatted(targetLanguage);

                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;
                String base64Audio = Base64.getEncoder().encodeToString(audioData);

                Map<String, Object> requestBody = new HashMap<>();
                Map<String, Object> part1 = Map.of("text", systemInstruction);
                Map<String, Object> part2 = Map.of("inline_data", Map.of(
                        "mime_type", "audio/mp3",
                        "data", base64Audio
                ));
                requestBody.put("contents", List.of(Map.of("parts", List.of(part1, part2))));
                requestBody.put("generationConfig", Map.of("response_mime_type", "application/json"));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                String responseJson = restTemplate.postForObject(url, requestEntity, String.class);

                JsonNode rootNode = objectMapper.readTree(responseJson);
                String resultText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

                log.info("Gemini AI 응답 성공 (타겟 언어: {})", targetLanguage);

                return objectMapper.readValue(resultText, AiResultDto.class);

            } catch (Exception e) {
                attempt++;
                log.warn("AI API 호출 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    throw new RuntimeException("AI 서비스가 너무 바쁩니다. 잠시 후 다시 시도해주세요.");
                }
            }
        }
        throw new RuntimeException("AI 분석 중 오류가 발생했습니다.");
    }
}