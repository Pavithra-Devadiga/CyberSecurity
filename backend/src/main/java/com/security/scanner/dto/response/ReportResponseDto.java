package com.security.scanner.dto.response;

import java.time.LocalDateTime;

public class ReportResponseDto {

    private Long scanId;
    private String targetUrl;
    private String format;
    private Integer securityScore;
    private String letterGrade;
    private int totalVulnerabilities;
    private String downloadUrl;
    private LocalDateTime generatedAt;

    public ReportResponseDto() {}

    public Long getScanId() { return scanId; }
    public void setScanId(Long scanId) { this.scanId = scanId; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Integer getSecurityScore() { return securityScore; }
    public void setSecurityScore(Integer securityScore) { this.securityScore = securityScore; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public int getTotalVulnerabilities() { return totalVulnerabilities; }
    public void setTotalVulnerabilities(int totalVulnerabilities) { this.totalVulnerabilities = totalVulnerabilities; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
