package com.dev.jobradar.service;

import com.dev.jobradar.model.JobDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.to}")
    private String recipientEmail;

    @Value("${mail.subject}")
    private String subject;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendJobListings(List<JobDTO> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            log.warn("No jobs to send in email");
            return;
        }

        try {
            String emailBody = formatEmailBody(jobs);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Successfully sent email with {} jobs to {}", jobs.size(), recipientEmail);

        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String formatEmailBody(List<JobDTO> jobs) {
        StringBuilder body = new StringBuilder();
        body.append("Found ").append(jobs.size()).append(" Java Backend Job(s) in Bengaluru:\n\n");
        body.append("=".repeat(80)).append("\n\n");

        for (int i = 0; i < jobs.size(); i++) {
            JobDTO job = jobs.get(i);
            body.append(i + 1).append(". ").append(job.getJobTitle())
                .append(" @ ").append(job.getCompanyName()).append("\n");
            
            body.append("   Location: ").append(job.getLocation()).append("\n");
            
            if (job.getExperienceRequired() != null && !job.getExperienceRequired().isBlank()) {
                body.append("   Experience: ").append(job.getExperienceRequired()).append("\n");
            }
            
            if (job.getSalary() != null && !job.getSalary().isBlank()) {
                body.append("   Salary: ").append(job.getSalary()).append("\n");
            }
            
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                body.append("   Skills: ").append(String.join(", ", job.getSkills())).append("\n");
            }
            
            body.append("   Link: ").append(job.getJobUrl()).append("\n");
            body.append("\n");
        }

        body.append("=".repeat(80)).append("\n");
        body.append("\nThis is an automated email from JobRadar.\n");

        return body.toString();
    }
}
