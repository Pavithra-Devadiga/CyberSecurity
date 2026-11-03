package com.security.scanner.entity;

import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_url", nullable = false, length = 1000)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false, length = 30)
    private ScanType scanType = ScanType.QUICK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScanStatus status = ScanStatus.QUEUED;

    @Column(name = "security_score")
    private Integer securityScore;

    @Column(name = "letter_grade", length = 5)
    private String letterGrade;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vulnerability> vulnerabilities = new ArrayList<>();

    @Column(name = "headers_analyzed")
    private Integer headersAnalyzed = 0;

    @Column(name = "endpoints_tested")
    private Integer endpointsTested = 0;

    @Column(name = "ssl_valid")
    private Boolean sslValid;

    @Column(name = "tls_version", length = 50)
    private String tlsVersion;

    @Column(name = "technologies_found", length = 2000)
    private String technologiesFound;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Scan() {}

    public Scan(String targetUrl, ScanType scanType, User user) {
        this.targetUrl = targetUrl;
        this.scanType = scanType != null ? scanType : ScanType.QUICK;
        this.user = user;
        this.status = ScanStatus.QUEUED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addVulnerability(Vulnerability v) {
        vulnerabilities.add(v);
        v.setScan(this);
    }

    public void removeVulnerability(Vulnerability v) {
        vulnerabilities.remove(v);
        v.setScan(null);
    }

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Vulnerability> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<Vulnerability> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scan scan)) return false;
        return Objects.equals(id, scan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
