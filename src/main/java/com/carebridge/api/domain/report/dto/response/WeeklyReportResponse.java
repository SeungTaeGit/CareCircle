package com.carebridge.api.domain.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WeeklyReportResponse {
    private Long reportId;
    private String seniorName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reportContent;
    private String status;
}