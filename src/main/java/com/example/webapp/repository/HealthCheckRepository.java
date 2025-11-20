package com.example.webapp.repository;

import com.example.webapp.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
}
