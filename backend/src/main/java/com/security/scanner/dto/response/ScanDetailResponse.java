package com.security.scanner.dto.response;

import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScanDetailResponse {

    private Long id;
    private String targetUrl;
    private ScanType scanType;
    private ScanStatus status;
    private Integer securityScore;
    private String letterGrade;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer headersAnalyzed;
    private Integer endpointsTested;
    private Boolean sslValid;
    private String tlsVersion;
    private String technologiesFound;
    private String errorMessage;
    private List<VulnerabilityDto> vulnerabilities = new ArrayList<>();
    private LocalDateTime createdAt;

    public ScanDetailResponse() {}

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

    public Integer getHeadersAnalyzed() { return headersAnalyzed; }
    public void setHeadersAnalyzed(Integer headersAnalyzed) { this.headersAnalyzed = headersAnalyzed; }

    public Integer getEndpointsTested() { return endpointsTested; }
    public void setEndpointsTested(Integer endpointsTested) { this.endpointsTested = endpointsTested; }

    public Boolean getSslValid() { return sslValid; }
    public void setSslValid(Boolean sslValid) { this.sslValid = sslValid; }

    public String getTlsVersion() { return tlsVersion; }
    public void setTlsVersion(String tlsVersion) { this.tlsVersion = tlsVersion; }

    public String getTechnologiesFound() { return technologiesFound; }
    public void setTechnologiesFound(String technologiesFound) { this.technologiesFound = technologiesFound; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public List<VulnerabilityDto> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<VulnerabilityDto> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
