package com.carebridge.api.domain.activity.service;

import com.carebridge.api.domain.activity.dto.request.ActivitySaveRequest;
import com.carebridge.api.domain.activity.entity.ActivityRecord;
import com.carebridge.api.domain.activity.repository.ActivityRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SeniorRepository seniorRepository;

    @Transactional
    public void saveActivity(String seniorIdString, ActivitySaveRequest request) {

        Long seniorId = Long.parseLong(seniorIdString);

        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("어르신 정보를 찾을 수 없습니다."));

        ActivityRecord record = ActivityRecord.builder()
                .senior(senior)
                .activityType(request.getActivityType())
                .score(request.getScore())
                .playTimeSeconds(request.getPlayTimeSeconds())
                .build();

        activityRepository.save(record);
    }
}