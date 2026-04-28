package com.dev.jobradar.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("location")
    private String location;

    @JsonProperty("experience_required")
    private String experienceRequired;

    @JsonProperty("salary")
    private String salary;

    @JsonProperty("skills")
    private List<String> skills;

    @JsonProperty("job_url")
    private String jobUrl;

    @JsonProperty("source")
    private String source;
}
