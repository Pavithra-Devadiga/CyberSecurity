package com.security.scanner.dto.response;

import java.util.List;
import java.util.Map;

public class DashboardMetricsDto {

    private long totalScans;
    private long activeScans;
    private long completedScans;
    private long failedScans;
    private Double averageSecurityScore;
    private long totalVulnerabilities;
    private Map<String, Long> severityCounts;
    private Map<String, Long> categoryCounts;
    private List<RecentScanDto> recentScans;

    public DashboardMetricsDto() {}

    public long getTotalScans() { return totalScans; }
    public void setTotalScans(long totalScans) { this.totalScans = totalScans; }

    public long getActiveScans() { return activeScans; }
    public void setActiveScans(long activeScans) { this.activeScans = activeScans; }

    public long getCompletedScans() { return completedScans; }
    public void setCompletedScans(long completedScans) { this.completedScans = completedScans; }

    public long getFailedScans() { return failedScans; }
    public void setFailedScans(long failedScans) { this.failedScans = failedScans; }

    public Double getAverageSecurityScore() { return averageSecurityScore; }
    public void setAverageSecurityScore(Double averageSecurityScore) { this.averageSecurityScore = averageSecurityScore; }

    public long getTotalVulnerabilities() { return totalVulnerabilities; }
    public void setTotalVulnerabilities(long totalVulnerabilities) { this.totalVulnerabilities = totalVulnerabilities; }

    public Map<String, Long> getSeverityCounts() { return severityCounts; }
    public void setSeverityCounts(Map<String, Long> severityCounts) { this.severityCounts = severityCounts; }

    public Map<String, Long> getCategoryCounts() { return categoryCounts; }
    public void setCategoryCounts(Map<String, Long> categoryCounts) { this.categoryCounts = categoryCounts; }

    public List<RecentScanDto> getRecentScans() { return recentScans; }
    public void setRecentScans(List<RecentScanDto> recentScans) { this.recentScans = recentScans; }
}
