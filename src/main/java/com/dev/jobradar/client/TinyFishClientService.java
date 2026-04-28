package com.dev.jobradar.client;

import com.dev.jobradar.model.JobDTO;
import com.dev.jobradar.util.SSEParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinyFishClientService {

    private final WebClient webClient;

    @Value("${tinyfish.linkedin.url}")
    private String linkedinUrl;

    @Value("${tinyfish.linkedin.goal}")
    private String goal;

    @Value("${tinyfish.api.timeout}")
    private int timeout;

    public List<JobDTO> fetchJobs() {
        log.info("Starting job fetch from TinyFish API");

        Map<String, String> requestBody = Map.of(
                "url", linkedinUrl,
                "goal", goal
        );

        AtomicReference<List<JobDTO>> jobsRef = new AtomicReference<>(Collections.emptyList());

        try {
            webClient.post()
                    .uri("/run-sse")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofSeconds(timeout))
                    .doOnNext(event -> log.debug("Received SSE event: {}", event))
                    .map(SSEParserUtil::stripDataPrefix)
                    .filter(jsonData -> !jsonData.isBlank())
                    .takeUntil(jsonData -> {
                        if (SSEParserUtil.isCompleteEvent(jsonData)) {
                            log.info("COMPLETE event received");
                            List<JobDTO> jobs = SSEParserUtil.extractJobs(jsonData);
                            jobsRef.set(jobs);
                            return true;
                        }
                        return false;
                    })
                    .then()
                    .block();

            List<JobDTO> jobs = jobsRef.get();
            log.info("Successfully fetched {} jobs from TinyFish API", jobs.size());
            return jobs;

        } catch (Exception e) {
            log.error("Error fetching jobs from TinyFish API: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
