package com.carebridge.api.domain.mission.repository;

import com.carebridge.api.domain.mission.entity.MissionProposal;
import com.carebridge.api.domain.mission.enums.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionProposalRepository extends JpaRepository<MissionProposal, Long> {

    List<MissionProposal> findByGuardianId(Long guardianId);

    List<MissionProposal> findByStatus(ProposalStatus status);
}