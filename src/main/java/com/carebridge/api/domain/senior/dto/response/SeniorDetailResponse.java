package com.carebridge.api.domain.senior.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class SeniorDetailResponse {
    private Long seniorId;
    private String name;
    private String contact;
    private String gender;
    private String birthDate;
    private String country;
    private String language;
    private String matchStatus;
    private String hobbies;
    private int xp;
    private String interestLevel;
    private String recommendedAction;

    private List<GuardianInfo> guardians;

    @Getter
    @Builder
    public static class GuardianInfo {
        private String name;
        private String phoneNumber;
        private String email;
    }
}