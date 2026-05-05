package com.dev.jobradar.repository;

import com.dev.jobradar.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    
    boolean existsByJobUrl(String jobUrl);
}
