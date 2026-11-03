package com.security.scanner.service;

import com.security.scanner.dto.request.ReportGenerateRequest;
import com.security.scanner.dto.response.ReportResponseDto;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.User;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Role;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.repository.ScanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ScanRepository scanRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("Should generate valid OpenPDF document bytes starting with %PDF- header")
    void testGeneratePdfReport() {
        User user = new User("auditor", "auditor@example.com", "pass", Role.ROLE_USER);
        Scan scan = new Scan("https://juice-shop.herokuapp.com", ScanType.FULL, user);
        scan.setId(101L);
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setSecurityScore(45);
        scan.setLetterGrade("D");
        scan.setStartTime(LocalDateTime.now().minusMinutes(5));
        scan.setEndTime(LocalDateTime.now());
        scan.setTlsVersion("TLSv1.3");
        scan.setTechnologiesFound("Node.js / Express, Angular Framework");

        scan.addVulnerability(new Vulnerability(
                scan,
                "SQL Injection in Search Endpoint",
                "Input parameter q is concatenated directly into SQLite query.",
                Severity.CRITICAL,
                VulnerabilityCategory.SQL_INJECTION,
                "CWE-89",
                "Use parameterized query bindings.",
                "SQLite3::SQLException: unrecognized token",
                "https://juice-shop.herokuapp.com/rest/products/search?q='"
        ));

        scan.addVulnerability(new Vulnerability(
                scan,
                "Reflected Cross-Site Scripting (XSS)",
                "Search query reflects unencoded input in DOM.",
                Severity.HIGH,
                VulnerabilityCategory.XSS,
                "CWE-79",
                "Context-aware output encoding.",
                "Unencoded probe reflected in response.",
                "https://juice-shop.herokuapp.com/#/search"
        ));

        Mockito.when(scanRepository.findById(101L)).thenReturn(Optional.of(scan));

        byte[] pdfBytes = reportService.generatePdfReport(101L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF size should be substantial");

        // Verify PDF Magic Bytes: %PDF-
        String header = new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII);
        assertEquals("%PDF-", header, "File must start with standard PDF header signature");

        // Verify report summary DTO
        ReportGenerateRequest req = new ReportGenerateRequest(101L, "PDF");
        ReportResponseDto summary = reportService.generateReportSummary(req);
        assertEquals(101L, summary.getScanId());
        assertEquals(45, summary.getSecurityScore());
        assertEquals("D", summary.getLetterGrade());
        assertEquals(2, summary.getTotalVulnerabilities());
    }
}
