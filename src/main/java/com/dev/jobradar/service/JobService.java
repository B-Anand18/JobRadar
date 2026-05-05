package com.dev.jobradar.service;

import com.dev.jobradar.model.Job;
import com.dev.jobradar.model.JobDTO;
import com.dev.jobradar.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public List<JobDTO> filterAndSaveNewJobs(List<JobDTO> jobs) {
        log.info("Filtering {} jobs for new entries", jobs.size());

        List<JobDTO> newJobs = jobs.stream()
                .filter(jobDTO -> !jobRepository.existsByJobUrl(jobDTO.getJobUrl()))
                .collect(Collectors.toList());

        log.info("Found {} new jobs out of {}", newJobs.size(), jobs.size());

        if (!newJobs.isEmpty()) {
            List<Job> jobEntities = newJobs.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());

            jobRepository.saveAll(jobEntities);
            log.info("Saved {} new jobs to database", jobEntities.size());
        }

        return newJobs;
    }

    private Job convertToEntity(JobDTO dto) {
        return Job.builder()
                .jobUrl(dto.getJobUrl())
                .jobTitle(dto.getJobTitle())
                .companyName(dto.getCompanyName())
                .location(dto.getLocation())
                .experienceRequired(dto.getExperienceRequired())
                .salary(dto.getSalary())
                .skills(dto.getSkills() != null ? String.join(", ", dto.getSkills()) : null)
                .source(dto.getSource())
                .build();
    }
}
