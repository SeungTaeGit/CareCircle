package com.carebridge.api.domain.admin.dto.response;

import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.enums.InterestLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private String hobbies;
    private String matchStatus;
    private Long partnerId;
    private InterestLevel interestLevel;
    private String recommendedAction;
    private LocalDateTime lastActiveAt;
    private int xp;

    public static SeniorDetailResponse from(Senior senior) {
        return SeniorDetailResponse.builder()
                .seniorId(senior.getId())
                .name(senior.getName())
                .contact(senior.getContact())
                .gender(senior.getGender())
                .birthDate(senior.getBirthDate())
                .country(senior.getCountry())
                .language(senior.getLanguage())
                .hobbies(senior.getHobbies())
                .matchStatus(senior.getMatchStatus())
                .partnerId(senior.getPartnerId())
                .interestLevel(senior.getInterestLevel())
                .recommendedAction(senior.getRecommendedAction())
                .lastActiveAt(senior.getLastActiveAt())
                .xp(senior.getXp())
                .build();
    }
}