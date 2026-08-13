package com.carebridge.api.domain.report.entity;

import com.carebridge.api.domain.report.enums.ReportStatus;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyReport extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reportContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Builder
    public WeeklyReport(Senior senior, LocalDate startDate, LocalDate endDate, String reportContent) {
        this.senior = senior;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportContent = reportContent;
        this.status = ReportStatus.PENDING;
    }

    public void markAsSent() {
        this.status = ReportStatus.SENT;
    }

    public void updateReportContent(String newContent) {
        this.reportContent = newContent;
    }
}