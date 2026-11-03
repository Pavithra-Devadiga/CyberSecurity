package com.security.scanner.dto.response;

import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import java.time.LocalDateTime;

public class ScanResponse {

    private Long id;
    private String targetUrl;
    private ScanType scanType;
    private ScanStatus status;
    private Integer securityScore;
    private String letterGrade;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer vulnerabilityCount;
    private Integer criticalCount;
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;
    private Integer infoCount;
    private LocalDateTime createdAt;

    public ScanResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public ScanType getScanType() { return scanType; }
    public void setScanType(ScanType scanType) { this.scanType = scanType; }

    public ScanStatus getStatus() { return status; }
    public void setStatus(ScanStatus status) { this.status = status; }

    public Integer getSecurityScore() { return securityScore; }
    public void setSecurityScore(Integer securityScore) { this.securityScore = securityScore; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getVulnerabilityCount() { return vulnerabilityCount; }
    public void setVulnerabilityCount(Integer vulnerabilityCount) { this.vulnerabilityCount = vulnerabilityCount; }

    public Integer getCriticalCount() { return criticalCount; }
    public void setCriticalCount(Integer criticalCount) { this.criticalCount = criticalCount; }

    public Integer getHighCount() { return highCount; }
    public void setHighCount(Integer highCount) { this.highCount = highCount; }

    public Integer getMediumCount() { return mediumCount; }
    public void setMediumCount(Integer mediumCount) { this.mediumCount = mediumCount; }

    public Integer getLowCount() { return lowCount; }
    public void setLowCount(Integer lowCount) { this.lowCount = lowCount; }

    public Integer getInfoCount() { return infoCount; }
    public void setInfoCount(Integer infoCount) { this.infoCount = infoCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
