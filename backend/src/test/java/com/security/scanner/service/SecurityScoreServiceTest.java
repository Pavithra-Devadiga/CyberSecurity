package com.security.scanner.service;

import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityScoreServiceTest {

    private SecurityScoreService scoreService;

    @BeforeEach
    void setUp() {
        scoreService = new SecurityScoreService();
    }

    @Test
    @DisplayName("Should return 100 and A+ for clean scan with no vulnerabilities")
    void testCleanScanScore() {
        SecurityScoreService.ScoreResult result = scoreService.calculateScore(List.of());
        assertEquals(100, result.score());
        assertEquals("A+", result.grade());
    }

    @Test
    @DisplayName("Should deduct 25 points and cap grade at D for critical vulnerability")
    void testCriticalVulnerabilityScore() {
        Vulnerability v = new Vulnerability(null, "RCE", "Remote code execution",
                Severity.CRITICAL, VulnerabilityCategory.SENSITIVE_DATA, "CWE-94", "Patch", "ev", "url");

        SecurityScoreService.ScoreResult result = scoreService.calculateScore(List.of(v));
        assertEquals(75, result.score());
        assertEquals("D", result.grade()); // Cap at D when critical present
    }

    @Test
    @DisplayName("Should clamp minimum score at 0 and return F")
    void testScoreFloorAtZero() {
        List<Vulnerability> vulns = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            vulns.add(new Vulnerability(null, "Crit " + i, "Desc",
                    Severity.CRITICAL, VulnerabilityCategory.SQL_INJECTION, "CWE-89", "Fix", "ev", "url"));
        }

        SecurityScoreService.ScoreResult result = scoreService.calculateScore(vulns);
        assertEquals(0, result.score());
        assertEquals("F", result.grade());
    }

    @Test
    @DisplayName("Should correctly compute score for mixed severity vulnerabilities")
    void testMixedSeverities() {
        List<Vulnerability> vulns = List.of(
                new Vulnerability(null, "High", "desc", Severity.HIGH, VulnerabilityCategory.XSS, "CWE-79", "fix", "ev", "url"),
                new Vulnerability(null, "Medium", "desc", Severity.MEDIUM, VulnerabilityCategory.SECURITY_HEADERS, "CWE-693", "fix", "ev", "url"),
                new Vulnerability(null, "Low", "desc", Severity.LOW, VulnerabilityCategory.SECURITY_HEADERS, "CWE-693", "fix", "ev", "url")
        );
        // 100 - 15 - 8 - 3 = 74 -> Grade C
        SecurityScoreService.ScoreResult result = scoreService.calculateScore(vulns);
        assertEquals(74, result.score());
        assertEquals("C", result.grade());
    }
}
