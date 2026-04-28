package com.dev.jobradar.util;

import com.dev.jobradar.model.JobDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class SSEParserUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATA_PREFIX = "data: ";
    private static final String EVENT_TYPE_COMPLETE = "COMPLETE";

    public static String stripDataPrefix(String sseEvent) {
        if (sseEvent == null || sseEvent.isBlank()) {
            return "";
        }
        return sseEvent.startsWith(DATA_PREFIX) ? sseEvent.substring(DATA_PREFIX.length()).trim() : sseEvent.trim();
    }

    public static boolean isCompleteEvent(String jsonData) {
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            String type = node.path("type").asText();
            return EVENT_TYPE_COMPLETE.equals(type);
        } catch (Exception e) {
            log.debug("Failed to parse event type: {}", e.getMessage());
            return false;
        }
    }

    public static List<JobDTO> extractJobs(String completeEventJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(completeEventJson);
            JsonNode resultNode = rootNode.path("result").path("result");

            if (resultNode.isMissingNode() || !resultNode.isArray()) {
                log.warn("No jobs found in COMPLETE event");
                return Collections.emptyList();
            }

            List<JobDTO> jobs = objectMapper.convertValue(resultNode, new TypeReference<List<JobDTO>>() {});
            log.info("Extracted {} jobs from COMPLETE event", jobs.size());
            return jobs;

        } catch (Exception e) {
            log.error("Failed to extract jobs from COMPLETE event: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
