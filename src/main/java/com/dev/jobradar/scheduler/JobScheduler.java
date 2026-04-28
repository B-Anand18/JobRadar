package com.dev.jobradar.scheduler;

import com.dev.jobradar.service.JobOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class JobScheduler {

    private final JobOrchestratorService jobOrchestratorService;

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledJobFetch() {
        log.info("=== Scheduled Job Fetch Started ===");

        try {
            jobOrchestratorService.executeJobPipeline();
            log.info("=== Scheduled Job Fetch Completed ===");

        } catch (Exception e) {
            log.error("Scheduled job fetch failed: {}", e.getMessage(), e);
        }
    }
}
