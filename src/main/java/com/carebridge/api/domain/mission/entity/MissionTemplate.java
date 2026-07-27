package com.carebridge.api.domain.mission.entity;

import com.carebridge.api.domain.mission.enums.MissionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionTemplate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    @Column(nullable = false)
    private int rewardXp;

    @Builder
    public MissionTemplate(String title, String content, MissionType type, int rewardXp) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.rewardXp = rewardXp;
    }
}