package com.carebridge.api.domain.activity.entity;

import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(nullable = false)
    private String activityType;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int playTimeSeconds;

    @Builder
    public ActivityRecord(Senior senior, String activityType, int score, int playTimeSeconds) {
        this.senior = senior;
        this.activityType = activityType;
        this.score = score;
        this.playTimeSeconds = playTimeSeconds;
    }
}