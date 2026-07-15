package com.carebridge.api.domain.admin.entity;

import com.carebridge.api.domain.admin.entity.enums.DangerStatus;
import com.carebridge.api.domain.admin.entity.enums.DangerType;
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
public class DangerSignal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DangerType dangerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DangerStatus status;

    private String description;

    @Builder
    public DangerSignal(Senior senior, DangerType dangerType, String description) {
        this.senior = senior;
        this.dangerType = dangerType;
        this.status = DangerStatus.PENDING;
        this.description = description;
    }

    public void resolve() {
        this.status = DangerStatus.RESOLVED;
    }
}