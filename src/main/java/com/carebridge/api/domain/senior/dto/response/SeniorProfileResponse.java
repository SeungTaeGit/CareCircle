package com.carebridge.api.domain.senior.dto.response;

import com.carebridge.api.domain.senior.entity.Senior;
import lombok.Getter;

@Getter
public class SeniorProfileResponse {
    private Long id;
    private String name;
    private String country;
    private String language;
    private String matchStatus;
    private String hobbies;

    public SeniorProfileResponse(Senior senior) {
        this.id = senior.getId();
        this.name = senior.getName();
        this.country = senior.getCountry();
        this.language = senior.getLanguage();
        this.matchStatus = senior.getMatchStatus();
        this.hobbies = senior.getHobbies();
    }
}