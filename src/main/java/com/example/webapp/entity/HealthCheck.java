package com.example.webapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_checks")
public class HealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private long checkId;

    @Column(name="check_datetime", nullable = false)
    private LocalDateTime checkDatetime;

    public HealthCheck() {}

    public HealthCheck(LocalDateTime checkDatetime) {
        this.checkDatetime = checkDatetime;
    }

    public long getCheckId() {
        return checkId;
    }
    public void setCheckId(long checkId) {
        this.checkId = checkId;
    }

    public LocalDateTime getCheckDatetime() {
        return checkDatetime;
    }

    public void setCheckDatetime(LocalDateTime checkDatetime) {
        this.checkDatetime = checkDatetime;
    }

    @Override
    public String toString() {
        return "HealthCheck{" +
                "checkId=" + checkId +
                ", checkDatetime=" + checkDatetime +
                '}';
    }
}
