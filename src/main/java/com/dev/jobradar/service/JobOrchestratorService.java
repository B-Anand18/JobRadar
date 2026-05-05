package com.dev.jobradar.service;

import com.dev.jobradar.client.TinyFishClientService;
import com.dev.jobradar.model.JobDTO;
import com.dev.jobradar.util.JobFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobOrchestratorService {

    private final TinyFishClientService tinyFishClientService;
    private final EmailService emailService;
    private final JobService jobService;

    public void executeJobPipeline() {
        log.info("=== Starting Job Pipeline Execution ===");

        try {
            // Step 1: Fetch jobs from TinyFish API
            log.info("Step 1: Fetching jobs from TinyFish API");
            List<JobDTO> rawJobs = tinyFishClientService.fetchJobs();

            if (rawJobs.isEmpty()) {
                log.warn("No jobs fetched from TinyFish API. Pipeline stopped.");
                return;
            }

            // Step 2: Apply filters (location + deduplication)
            log.info("Step 2: Applying filters to {} jobs", rawJobs.size());
            List<JobDTO> filteredJobs = JobFilterUtil.applyFilters(rawJobs);

            if (filteredJobs.isEmpty()) {
                log.warn("No jobs remaining after filtering. Email will not be sent.");
                return;
            }

            // Step 3: Filter new jobs and save to database
            log.info("Step 3: Checking for new jobs in database");
            List<JobDTO> newJobs = jobService.filterAndSaveNewJobs(filteredJobs);

            if (newJobs.isEmpty()) {
                log.info("No new jobs found. All jobs already exist in database.");
                return;
            }

            // Step 4: Send email with new job listings
            log.info("Step 4: Sending email with {} new jobs", newJobs.size());
            emailService.sendJobListings(newJobs);

            log.info("=== Job Pipeline Execution Completed Successfully ===");

        } catch (Exception e) {
            log.error("Job pipeline execution failed: {}", e.getMessage(), e);
            throw new RuntimeException("Job pipeline failed", e);
        }
    }
}
