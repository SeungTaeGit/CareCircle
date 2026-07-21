package com.carebridge.api.domain.senior.entity;

import com.carebridge.api.domain.admin.entity.Admin;
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

    @Column(nullable = false)
    private String linkCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    private String country;
    private String language;
    private String matchStatus;
    private String hobbies;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

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

    public void updateMatchInfo(String matchStatus, String linkCode) {
        this.matchStatus = matchStatus;
        this.linkCode = linkCode;
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now();
    }
}