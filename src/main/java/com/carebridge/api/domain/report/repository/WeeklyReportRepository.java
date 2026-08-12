package com.carebridge.api.domain.report.repository;

import com.carebridge.api.domain.report.entity.WeeklyReport;
import com.carebridge.api.domain.report.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    List<WeeklyReport> findByStatus(ReportStatus status);
}