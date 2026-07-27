package com.carebridge.api.domain.mission.entity;

import com.carebridge.api.domain.mission.enums.MissionStatus;
import com.carebridge.api.domain.senior.entity.Senior;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyMission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_template_id")
    private MissionTemplate missionTemplate;

    private String customTitle;

    @Column(columnDefinition = "TEXT")
    private String customContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;

    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String sttResult;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    @Builder
    public DailyMission(Senior senior, MissionTemplate missionTemplate, String customTitle, String customContent) {
        this.senior = senior;
        this.missionTemplate = missionTemplate;
        this.customTitle = customTitle;
        this.customContent = customContent;
        this.status = MissionStatus.PENDING;
        this.assignedAt = LocalDateTime.now();
    }

    public void completeMission(String audioUrl, String sttResult) {
        this.status = MissionStatus.COMPLETED;
        this.audioUrl = audioUrl;
        this.sttResult = sttResult;
        this.completedAt = LocalDateTime.now();
    }

    public void skipMission() {
        this.status = MissionStatus.SKIPPED;
        this.completedAt = LocalDateTime.now();
    }
}