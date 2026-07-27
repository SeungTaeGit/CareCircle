package com.carebridge.api.domain.mission.service;

import com.carebridge.api.domain.guardian.entity.Guardian;
import com.carebridge.api.domain.guardian.repository.GuardianRepository;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.entity.MissionProposal;
import com.carebridge.api.domain.mission.entity.MissionTemplate;
import com.carebridge.api.domain.mission.enums.MissionType;
import com.carebridge.api.domain.mission.enums.ProposalStatus;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.mission.repository.MissionProposalRepository;
import com.carebridge.api.domain.mission.repository.MissionTemplateRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionProposalService {

    private final MissionProposalRepository missionProposalRepository;
    private final GuardianRepository guardianRepository;

    private final MissionTemplateRepository missionTemplateRepository;
    private final DailyMissionRepository dailyMissionRepository;

    @Transactional
    public Long proposeMission(Long guardianId, String content) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new IllegalArgumentException("보호자를 찾을 수 없습니다. id=" + guardianId));

        Senior senior = guardian.getSenior();

        MissionProposal proposal = MissionProposal.builder()
                .guardian(guardian)
                .senior(senior)
                .proposedContent(content)
                .build();

        return missionProposalRepository.save(proposal).getId();
    }

    @Transactional
    public void processProposal(Long proposalId, ProposalStatus status, String adminComment) {
        MissionProposal proposal = missionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("제안 내역을 찾을 수 없습니다. id=" + proposalId));

        proposal.processProposal(status, adminComment);

        if (status == ProposalStatus.APPROVED) {

            MissionTemplate newTemplate = MissionTemplate.builder()
                    .title("가족이 보낸 특별 미션 💌")
                    .content(proposal.getProposedContent())
                    .type(MissionType.VOICE)
                    .rewardXp(20)
                    .build();

            missionTemplateRepository.save(newTemplate);

            DailyMission newDailyMission = DailyMission.builder()
                    .senior(proposal.getSenior())
                    .missionTemplate(newTemplate)
                    .build();

            dailyMissionRepository.save(newDailyMission);
        }
    }
}