package com.carebridge.api.domain.mission.entity;

import com.carebridge.api.domain.mission.enums.ProposalStatus;
import com.carebridge.api.domain.guardian.entity.Guardian;
import com.carebridge.api.domain.senior.entity.Senior;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionProposal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private Guardian guardian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String proposedContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(columnDefinition = "TEXT")
    private String adminComment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MissionProposal(Guardian guardian, Senior senior, String proposedContent) {
        this.guardian = guardian;
        this.senior = senior;
        this.proposedContent = proposedContent;
        this.status = ProposalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void processProposal(ProposalStatus status, String adminComment) {
        this.status = status;
        this.adminComment = adminComment;
    }
}