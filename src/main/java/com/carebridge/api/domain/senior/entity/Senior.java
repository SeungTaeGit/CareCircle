package com.carebridge.api.domain.senior.entity;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.senior.enums.InterestLevel;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Senior extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contact;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false, unique = true, length = 6)
    private String pinCode;

    @Column(nullable = false, unique = true)
    private String linkCode;

    @Column(name = "partner_id")
    private Long partnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    private String country;
    private String language;
    private String matchStatus;
    private String hobbies;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(nullable = false)
    private int xp = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestLevel interestLevel = InterestLevel.NONE;

    @Column(length = 100)
    private String recommendedAction = "-";

    @Builder
    public Senior(String name, String contact, String gender, String birthDate,
                  String country, String language, String hobbies,
                  String pinCode, String linkCode, Admin admin) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.birthDate = birthDate;
        this.country = country;
        this.language = language;
        this.hobbies = hobbies;
        this.pinCode = pinCode;
        this.linkCode = linkCode;
        this.admin = admin;
    }

    public void updateMatchInfo(String matchStatus, Long partnerId) {
        this.matchStatus = matchStatus;
        this.partnerId = partnerId;
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now();
    }

    public void addXp(int amount) {
        this.xp += amount;
    }

    public void updateInterestLevel(InterestLevel level, String recommendedAction) {
        this.interestLevel = level;
        this.recommendedAction = recommendedAction;
    }
}