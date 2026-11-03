package com.security.scanner.dto.response;

import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import java.time.LocalDateTime;

public class RecentScanDto {

    private Long id;
    private String targetUrl;
    private ScanType scanType;
    private ScanStatus status;
    private Integer securityScore;
    private String letterGrade;
    private int vulnerabilityCount;
    private LocalDateTime createdAt;

    public RecentScanDto() {}

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

    public int getVulnerabilityCount() { return vulnerabilityCount; }
    public void setVulnerabilityCount(int vulnerabilityCount) { this.vulnerabilityCount = vulnerabilityCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
