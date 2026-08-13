package com.carebridge.api.domain.reward.controller;

import com.carebridge.api.domain.reward.dto.response.GardenResponse;
import com.carebridge.api.domain.reward.service.GardenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/garden")
@RequiredArgsConstructor
public class GardenController {

    private final GardenService gardenService;

    @GetMapping("/{seniorId}")
    public ResponseEntity<GardenResponse> getGarden(@PathVariable Long seniorId) {
        GardenResponse response = gardenService.getGardenInfo(seniorId);
        return ResponseEntity.ok(response);
    }
}