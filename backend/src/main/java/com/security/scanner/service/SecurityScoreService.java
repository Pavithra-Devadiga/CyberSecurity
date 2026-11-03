package com.security.scanner.service;

import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityScoreService {

    public record ScoreResult(int score, String grade) {}

    /**
     * Calculates the security posture score (0-100) and corresponding letter grade.
     */
    public ScoreResult calculateScore(List<Vulnerability> vulnerabilities) {
        if (vulnerabilities == null || vulnerabilities.isEmpty()) {
            return new ScoreResult(100, "A+");
        }

        int score = 100;
        int criticalCount = 0;
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;

        for (Vulnerability v : vulnerabilities) {
            if (v.getSeverity() == Severity.CRITICAL) {
                criticalCount++;
                score -= 25;
            } else if (v.getSeverity() == Severity.HIGH) {
                highCount++;
                score -= 15;
            } else if (v.getSeverity() == Severity.MEDIUM) {
                mediumCount++;
                score -= 8;
            } else if (v.getSeverity() == Severity.LOW) {
                lowCount++;
                score -= 3;
            }
        }

        // Clamp between 0 and 100
        score = Math.max(0, Math.min(100, score));

        // If any critical vulnerability exists, cap maximum grade at D
        String grade = determineGrade(score, criticalCount, highCount);
        return new ScoreResult(score, grade);
    }

    private String determineGrade(int score, int criticalCount, int highCount) {
        if (criticalCount > 0) {
            return score < 45 ? "F" : "D";
        }
        if (score >= 95) return "A+";
        if (score >= 85) return "A";
        if (score >= 75) return "B";
        if (score >= 60) return "C";
        if (score >= 45) return "D";
        return "F";
    }
}
