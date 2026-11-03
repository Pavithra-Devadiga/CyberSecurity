package com.security.scanner.service;

import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeverityClassificationService {

    /**
     * Determines the estimated CVSS v3.1 base score based on severity and category.
     */
    public double getEstimatedCvssScore(Vulnerability v) {
        if (v == null || v.getSeverity() == null) return 0.0;

        return switch (v.getSeverity()) {
            case CRITICAL -> v.getCategory() == VulnerabilityCategory.SENSITIVE_DATA ? 9.8 : 9.1;
            case HIGH -> v.getCategory() == VulnerabilityCategory.SQL_INJECTION || v.getCategory() == VulnerabilityCategory.XSS ? 8.2 : 7.5;
            case MEDIUM -> 5.5;
            case LOW -> 3.2;
            case INFO -> 0.0;
        };
    }

    /**
     * Normalizes and adjusts severity if context requires escalation.
     */
    public Severity classify(VulnerabilityCategory category, String evidence, Severity proposed) {
        if (proposed != null) return proposed;

        if (category == null) return Severity.INFO;
        return switch (category) {
            case SQL_INJECTION -> Severity.HIGH;
            case XSS -> Severity.HIGH;
            case SENSITIVE_DATA -> evidence != null && evidence.contains("AKIA") ? Severity.CRITICAL : Severity.HIGH;
            case SSL_TLS -> Severity.MEDIUM;
            case SECURITY_HEADERS -> Severity.LOW;
            case ENDPOINT_EXPOSURE -> evidence != null && evidence.contains(".env") ? Severity.CRITICAL : Severity.MEDIUM;
            case TECHNOLOGY_DISCLOSURE -> Severity.INFO;
            case OWASP_ZAP -> Severity.MEDIUM;
        };
    }
}
