package com.dev.jobradar.util;

import com.dev.jobradar.model.JobDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JobFilterUtil {

    private static final String LOCATION_BENGALURU = "Bengaluru";
    private static final String LOCATION_BANGALORE = "Bangalore";

    public static List<JobDTO> filterByLocation(List<JobDTO> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return List.of();
        }

        List<JobDTO> filtered = jobs.stream()
                .filter(job -> job.getLocation() != null && 
                        (job.getLocation().contains(LOCATION_BENGALURU) || 
                         job.getLocation().contains(LOCATION_BANGALORE)))
                .collect(Collectors.toList());

        log.info("Filtered {} jobs by location (Bengaluru/Bangalore) from {} total jobs", 
                filtered.size(), jobs.size());
        return filtered;
    }

    public static List<JobDTO> deduplicateByJobUrl(List<JobDTO> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return List.of();
        }

        int originalSize = jobs.size();
        
        List<JobDTO> deduplicated = new ArrayList<>(jobs.stream()
                .filter(job -> job.getJobUrl() != null && !job.getJobUrl().isBlank())
                .collect(Collectors.toMap(
                        JobDTO::getJobUrl,
                        job -> job,
                        (existing, replacement) -> existing
                ))
                .values());

        int duplicatesRemoved = originalSize - deduplicated.size();
        if (duplicatesRemoved > 0) {
            log.info("Removed {} duplicate jobs. Final count: {}", duplicatesRemoved, deduplicated.size());
        }

        return deduplicated;
    }

    public static List<JobDTO> applyFilters(List<JobDTO> jobs) {
        log.info("Applying filters to {} jobs", jobs != null ? jobs.size() : 0);
        List<JobDTO> filtered = filterByLocation(jobs);
        List<JobDTO> deduplicated = deduplicateByJobUrl(filtered);
        log.info("Final job count after all filters: {}", deduplicated.size());
        return deduplicated;
    }
}
