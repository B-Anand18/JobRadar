package com.dev.jobradar.controller;

import com.dev.jobradar.service.JobOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobOrchestratorService jobOrchestratorService;

    @PostMapping("/run-now")
    public ResponseEntity<Map<String, String>> triggerJobFetch() {
        log.info("Manual job fetch triggered via API");

        try {
            jobOrchestratorService.executeJobPipeline();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Job fetch pipeline executed successfully. Check logs for details."
            ));

        } catch (Exception e) {
            log.error("Manual job fetch failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Job fetch pipeline failed: " + e.getMessage()
            ));
        }
    }
}
