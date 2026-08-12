package com.carebridge.api.domain.ai.service;

import com.carebridge.api.domain.ai.dto.AiResultDto;
import com.carebridge.api.domain.mission.dto.response.AiMissionEvaluationResponse;
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
                        너는 노인 돌봄 서비스의 데이터 분석 AI야. 입력된 사용자의 원본 음성을 변형 없이 그대로 바탕으로 하여 아래 5가지 결과를 반드시 JSON 형식으로만 응답해줘.
        
                        1. stt: 음성을 텍스트로 그대로 받아쓰기
                        2. emotion: 텍스트에서 느껴지는 주된 감정. 무조건 다음 5개 중 하나만 선택해: [FEAR_ANXIETY, ANGER, SADNESS, JOY, NEUTRAL]
                        3. isHarmful: 원본 텍스트 내용에 욕설, 위협, 개인정보/금전 요구, 위기 신호 등 유해한 내용이 포함되어 있는지 여부 (boolean)
                        4. toxicCategory: 만약 isHarmful이 true라면 다음 6개 중 하나를 선택하고, false라면 "NONE"을 반환해: [VERBAL_ABUSE, THREAT, IDENTITY_ATTACK, SEXUAL_RISK, PRIVACY_FINANCIAL_RISK, CRISIS_ABUSE_SIGNAL]
                        5. translatedText: 원본 텍스트를 바탕으로 문맥을 살려 %s(으)로 자연스럽게 번역한 텍스트
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

    public AiResultDto analyzeText(String text, String targetLanguage) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String systemInstruction = """
                        너는 노인 돌봄 서비스의 데이터 분석 AI야. 입력된 사용자의 텍스트를 바탕으로 아래 5가지 결과를 반드시 JSON 형식으로만 응답해줘.
        
                        1. stt: 입력된 텍스트를 그대로 반환 (DTO 구조 유지를 위함)
                        2. emotion: 텍스트에서 느껴지는 주된 감정. 무조건 다음 5개 중 하나만 선택해: [FEAR_ANXIETY, ANGER, SADNESS, JOY, NEUTRAL]
                        3. isHarmful: 텍스트 내용에 욕설, 위협, 개인정보/금전 요구, 위기 신호 등 유해한 내용이 포함되어 있는지 여부 (boolean)
                        4. toxicCategory: 만약 isHarmful이 true라면 다음 6개 중 하나를 선택하고, false라면 "NONE"을 반환해: [VERBAL_ABUSE, THREAT, IDENTITY_ATTACK, SEXUAL_RISK, PRIVACY_FINANCIAL_RISK, CRISIS_ABUSE_SIGNAL]
                        5. translatedText: 원본 텍스트를 바탕으로 문맥을 살려 %s(으)로 자연스럽게 번역한 텍스트
                        """.formatted(targetLanguage);

                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;

                Map<String, Object> requestBody = new HashMap<>();
                Map<String, Object> part = Map.of("text", systemInstruction + "\n\n[입력된 텍스트]: " + text);

                requestBody.put("contents", List.of(Map.of("parts", List.of(part))));
                requestBody.put("generationConfig", Map.of("response_mime_type", "application/json"));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                String responseJson = restTemplate.postForObject(url, requestEntity, String.class);

                JsonNode rootNode = objectMapper.readTree(responseJson);
                String resultText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

                log.info("Gemini AI 텍스트 응답 성공 (타겟 언어: {})", targetLanguage);

                return objectMapper.readValue(resultText, AiResultDto.class);

            } catch (Exception e) {
                attempt++;
                log.warn("AI 텍스트 API 호출 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    throw new RuntimeException("AI 서비스가 너무 바쁩니다. 잠시 후 다시 시도해주세요.");
                }
            }
        }
        throw new RuntimeException("AI 텍스트 분석 중 오류가 발생했습니다.");
    }

    public AiMissionEvaluationResponse evaluateMissionText(String missionTitle, String text) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String systemInstruction = """
                        너는 노인 돌봄 서비스의 따뜻하고 친절한 AI 전문가야. 어르신이 미션 주제에 맞게 답변했는지 의도와 문맥을 파악해서 아래 5가지 결과를 반드시 JSON 형식으로만 응답해줘.
                        가장 중요한 점: 정답을 맞히는 것이 아니라, 주제에 대해 본인의 언어로 표현하고 대화하려는 의도가 있었는지를 기준으로 '아주 관대하게' 통과(true) 처리해.
                        
                        1. isPass: 어르신의 답변이 미션 주제와 조금이라도 연관이 있으면 무조건 true, 아예 침묵하거나 전혀 파악이 안 될 때만 false (boolean)
                        2. emotion: 답변에서 느껴지는 주된 감정. 무조건 다음 5개 중 하나만 선택해: [FEAR_ANXIETY, ANGER, SADNESS, JOY, NEUTRAL]
                        3. aiComment: 어르신에게 전달할 따뜻한 피드백 (최대 50자). 통과(true)라면 칭찬과 공감을, 유해한 내용(isHarmful=true)이라면 부드럽게 재시도를 유도해.
                        4. isHarmful: 답변 내용에 욕설, 위협, 혐오, 개인정보 노출, 위기 신호 등 유해한 내용이 포함되어 있는지 여부 (boolean)
                        5. toxicCategory: 만약 isHarmful이 true라면 다음 6개 중 하나를 선택하고, false라면 "NONE"을 반환해: [VERBAL_ABUSE, THREAT, IDENTITY_ATTACK, SEXUAL_RISK, PRIVACY_FINANCIAL_RISK, CRISIS_ABUSE_SIGNAL]
                        """;

                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;

                Map<String, Object> requestBody = new HashMap<>();
                String promptContext = systemInstruction + "\n\n[미션 주제]: " + missionTitle + "\n[어르신 답변]: " + text;
                Map<String, Object> part = Map.of("text", promptContext);

                requestBody.put("contents", List.of(Map.of("parts", List.of(part))));
                requestBody.put("generationConfig", Map.of("response_mime_type", "application/json"));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                String responseJson = restTemplate.postForObject(url, requestEntity, String.class);

                JsonNode rootNode = objectMapper.readTree(responseJson);
                String resultText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

                log.info("👀 [AI 원본 응답]: {}", resultText);

                log.info("Gemini AI 미션 평가 성공 (주제: {})", missionTitle);

                return objectMapper.readValue(resultText, AiMissionEvaluationResponse.class);

            } catch (Exception e) {
                attempt++;
                log.warn("AI 미션 평가 API 호출 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    throw new RuntimeException("AI 서비스가 너무 바쁩니다. 잠시 후 다시 시도해주세요.");
                }
            }
        }
        throw new RuntimeException("AI 미션 평가 중 오류가 발생했습니다.");
    }

    public String generateWeeklyReport(String seniorName, String activityData) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String systemInstruction = """
                        너는 노인 돌봄 서비스의 따뜻하고 전문적인 복지사야.
                        아래 제공되는 어르신의 지난주 '미션 참여 기록'과 '교류 활동(채팅) 감정 데이터'를 바탕으로, 
                        보호자에게 전달할 주간 요약 리포트를 작성해줘.
                        
                        [작성 가이드]
                        1. 보호자에게 다정하고 안심이 되는 어조로 작성할 것 (예: ~하셨습니다, ~하는 모습을 보이셨어요)
                        2. 어르신의 주된 감정 상태(기쁨, 슬픔 등)와 활동 참여도를 자연스럽게 요약할 것
                        3. 부정적인 감정(우울, 불안)이 있었다면 너무 심각하지 않게 부드럽게 전달하고, 복지사가 잘 케어하고 있다는 뉘앙스를 줄 것
                        4. 길이는 200~300자 내외의 일반 텍스트로 작성할 것 (JSON 절대 금지)
                        """;

                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;

                Map<String, Object> requestBody = new HashMap<>();
                String promptContext = systemInstruction + "\n\n[어르신 이름]: " + seniorName + "\n[활동 데이터]:\n" + activityData;
                Map<String, Object> part = Map.of("text", promptContext);

                requestBody.put("contents", List.of(Map.of("parts", List.of(part))));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                String responseJson = restTemplate.postForObject(url, requestEntity, String.class);
                JsonNode rootNode = objectMapper.readTree(responseJson);

                String reportText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                return reportText.trim();

            } catch (Exception e) {
                attempt++;
                log.warn("AI 리포트 생성 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt >= maxRetries) {
                    throw new RuntimeException("AI 리포트 생성 중 오류가 발생했습니다.");
                }
                try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        return "리포트 생성에 실패했습니다.";
    }
}