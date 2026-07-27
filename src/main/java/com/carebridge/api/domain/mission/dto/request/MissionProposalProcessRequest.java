package com.carebridge.api.domain.mission.dto.request;

import com.carebridge.api.domain.mission.enums.ProposalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionProposalProcessRequest {
    private ProposalStatus status;
    private String adminComment;
}