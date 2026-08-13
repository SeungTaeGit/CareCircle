package com.carebridge.api.domain.reward.entity;

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
public class Garden extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    private int plantLevel;
    private int currentExp;

    @Version
    private Long version;

    @Builder
    public Garden(Senior senior) {
        this.senior = senior;
        this.plantLevel = 1;
        this.currentExp = 0;
    }

    public void addExp(int exp) {
        this.currentExp += exp;
        if (this.currentExp >= this.plantLevel * 100) {
            this.plantLevel++;
        }
    }
}