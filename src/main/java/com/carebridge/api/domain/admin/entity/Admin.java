package com.carebridge.api.domain.admin.entity;

import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String institutionName;

    @Column(nullable = false)
    private String managerName;

    @Builder
    public Admin(String email, String password, String institutionName, String managerName) {
        this.email = email;
        this.password = password;
        this.institutionName = institutionName;
        this.managerName = managerName;
    }
}