package com.carebridge.api.domain.activity.service;

import com.carebridge.api.domain.activity.dto.request.ActivitySaveRequest;
import com.carebridge.api.domain.activity.dto.response.ActivityDto;
import com.carebridge.api.domain.activity.dto.response.ActivityRecordResponse;
import com.carebridge.api.domain.activity.dto.response.GuardianDashboardResponse;
import com.carebridge.api.domain.activity.entity.ActivityRecord;
import com.carebridge.api.domain.activity.repository.ActivityRepository;
import com.carebridge.api.domain.guardian.entity.Guardian;
import com.carebridge.api.domain.guardian.repository.GuardianRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SeniorRepository seniorRepository;
    private final GuardianRepository guardianRepository;

    @Transactional
    public void saveActivity(Long seniorId, ActivitySaveRequest request, MultipartFile audioFile) {

        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String uploadedAudioUrl = null;
        if (audioFile != null && !audioFile.isEmpty()) {
            // uploadedAudioUrl = s3Uploader.upload(audioFile, "activities"); // S3Uploader 완성 후 주석 해제
            uploadedAudioUrl = "임시_테스트_URL_나중에_S3랑_연결할것.mp3";
        }

        ActivityRecord record = ActivityRecord.builder()
                .senior(senior)
                .activityType(request.getActivityType())
                .score(request.getScore())
                .playTimeSeconds(request.getPlayTimeSeconds())
                .audioUrl(uploadedAudioUrl)
                .build();

        activityRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<ActivityRecordResponse> getActivitiesForGuardian(String guardianEmail) {

        Guardian guardian = guardianRepository.findByEmail(guardianEmail)
                .orElseThrow(() -> new IllegalArgumentException("보호자 정보를 찾을 수 없습니다."));

        Long seniorId = guardian.getSenior().getId();

        List<ActivityRecord> records = activityRepository.findAllBySeniorIdOrderByCreatedAtDesc(seniorId);

        return records.stream()
                .map(ActivityRecordResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuardianDashboardResponse getGuardianDashboard(String guardianEmail) {

        Guardian guardian = guardianRepository.findByEmail(guardianEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보호자입니다."));

        Senior senior = guardian.getSenior();
        if (senior == null) {
            throw new IllegalStateException("아직 매칭된 시니어가 없습니다.");
        }

        List<ActivityRecord> records = activityRepository.findAllBySeniorIdOrderByCreatedAtDesc(senior.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<ActivityDto> activityDtos = records.stream().map(record -> {
            String summary = String.format("인지 활동 게임 진행 - 점수: %d점, 플레이 시간: %d초",
                    record.getScore(), record.getPlayTimeSeconds());

            return ActivityDto.builder()
                    .id(record.getId())
                    .date(record.getCreatedAt() != null ? record.getCreatedAt().format(formatter) : "")
                    .missionTitle(record.getActivityType() + " 인지 훈련 완료!")
                    .type(record.getActivityType())
                    .contentSummary(summary)
                    .build();
        }).collect(Collectors.toList());

        int calculatedGardenLevel = (records.size() / 5) + 1;

        return GuardianDashboardResponse.builder()
                .seniorName(senior.getName())
                .sentiment("Sunny")
                .gardenLevel(calculatedGardenLevel)
                .activities(activityDtos)
                .build();
    }
}