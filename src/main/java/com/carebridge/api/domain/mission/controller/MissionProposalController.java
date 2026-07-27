package com.carebridge.api.domain.mission.controller;

import com.carebridge.api.domain.mission.dto.request.MissionProposalProcessRequest;
import com.carebridge.api.domain.mission.dto.request.MissionProposeRequest;
import com.carebridge.api.domain.mission.service.MissionProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/proposals")
public class MissionProposalController {

    private final MissionProposalService missionProposalService;

    @PostMapping
    public ResponseEntity<Long> proposeMission(@RequestBody MissionProposeRequest request) {
        Long proposalId = missionProposalService.proposeMission(
                request.getGuardianId(),
                request.getContent()
        );
        return ResponseEntity.ok(proposalId);
    }

    @PostMapping("/{proposalId}/process")
    public ResponseEntity<String> processProposal(
            @PathVariable Long proposalId,
            @RequestBody MissionProposalProcessRequest request) {

        missionProposalService.processProposal(
                proposalId,
                request.getStatus(),
                request.getAdminComment()
        );
        return ResponseEntity.ok("제안 처리가 완료되었습니다.");
    }
}