package com.security.scanner.controller;

import com.security.scanner.dto.response.ApiResponse;
import com.security.scanner.dto.response.DashboardMetricsDto;
import com.security.scanner.dto.response.RecentScanDto;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.repository.ScanRepository;
import com.security.scanner.repository.VulnerabilityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ScanRepository scanRepository;
    private final VulnerabilityRepository vulnerabilityRepository;

    public DashboardController(ScanRepository scanRepository, VulnerabilityRepository vulnerabilityRepository) {
        this.scanRepository = scanRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<DashboardMetricsDto>> getMetrics() {
        DashboardMetricsDto dto = new DashboardMetricsDto();

        long totalScans = scanRepository.count();
        long activeScans = scanRepository.countByStatus(ScanStatus.RUNNING) + scanRepository.countByStatus(ScanStatus.QUEUED);
        long completedScans = scanRepository.countByStatus(ScanStatus.COMPLETED);
        long failedScans = scanRepository.countByStatus(ScanStatus.FAILED);
        Double avgScore = scanRepository.calculateAverageSecurityScore();
        long totalVulns = vulnerabilityRepository.count();

        dto.setTotalScans(totalScans);
        dto.setActiveScans(activeScans);
        dto.setCompletedScans(completedScans);
        dto.setFailedScans(failedScans);
        dto.setAverageSecurityScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 100.0);
        dto.setTotalVulnerabilities(totalVulns);

        // Group by severity
        Map<String, Long> severityCounts = new HashMap<>();
        List<Object[]> sevList = vulnerabilityRepository.countVulnerabilitiesBySeverity();
        for (Object[] row : sevList) {
            severityCounts.put(row[0].toString(), (Long) row[1]);
        }
        dto.setSeverityCounts(severityCounts);

        // Group by category
        Map<String, Long> categoryCounts = new HashMap<>();
        List<Object[]> catList = vulnerabilityRepository.countVulnerabilitiesByCategory();
        for (Object[] row : catList) {
            categoryCounts.put(row[0].toString(), (Long) row[1]);
        }
        dto.setCategoryCounts(categoryCounts);

        // Recent Scans
        List<Scan> recent = scanRepository.findTop10RecentScans(PageRequest.of(0, 5));
        List<RecentScanDto> recentDtos = recent.stream().map(s -> {
            RecentScanDto r = new RecentScanDto();
            r.setId(s.getId());
            r.setTargetUrl(s.getTargetUrl());
            r.setScanType(s.getScanType());
            r.setStatus(s.getStatus());
            r.setSecurityScore(s.getSecurityScore());
            r.setLetterGrade(s.getLetterGrade());
            r.setVulnerabilityCount(s.getVulnerabilities() != null ? s.getVulnerabilities().size() : 0);
            r.setCreatedAt(s.getCreatedAt());
            return r;
        }).toList();
        dto.setRecentScans(recentDtos);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
