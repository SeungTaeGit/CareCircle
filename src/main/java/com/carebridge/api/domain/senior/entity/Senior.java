package com.carebridge.api.domain.senior.entity;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Senior extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false, unique = true, length = 6)
    private String pinCode;

    @Column(nullable = false, unique = true)
    private String linkCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    private String country;
    private String language;
    private String matchStatus;
    private String hobbies;

    @Builder
    public Senior(String name, String gender, String birthDate, String pinCode, String linkCode, Admin admin) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.pinCode = pinCode;
        this.linkCode = linkCode;
        this.admin = admin;
    }
}