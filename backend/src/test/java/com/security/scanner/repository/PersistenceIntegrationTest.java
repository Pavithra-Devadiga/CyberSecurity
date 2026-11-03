package com.security.scanner.repository;

import com.security.scanner.entity.Notification;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.User;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Role;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("dev")
class PersistenceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private VulnerabilityRepository vulnerabilityRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("Should persist and cascade User, Scan, Vulnerability, and Notification entities")
    void testFullPersistenceLifecycle() {
        // 1. Create and persist User
        User user = new User("pentester", "pentester@security.local", "hashedpassword", Role.ROLE_ADMIN);
        user = userRepository.save(user);
        assertNotNull(user.getId());
        assertTrue(userRepository.existsByUsername("pentester"));

        // 2. Create and persist Scan
        Scan scan = new Scan("https://juice-shop.herokuapp.com", ScanType.FULL, user);
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setSecurityScore(52);
        scan.setLetterGrade("D");
        scan.setStartTime(LocalDateTime.now().minusMinutes(10));
        scan.setEndTime(LocalDateTime.now().minusMinutes(2));
        scan.setHeadersAnalyzed(15);
        scan.setEndpointsTested(10);
        scan.setSslValid(true);
        scan.setTlsVersion("TLSv1.3");
        scan.setTechnologiesFound("Node.js / Express, Angular Framework");

        // 3. Add Vulnerabilities to Scan (Testing Cascade)
        Vulnerability sqli = new Vulnerability(
                scan,
                "SQL Injection in Search Endpoint",
                "Concatenation of input into SQLite statement without sanitization.",
                Severity.CRITICAL,
                VulnerabilityCategory.SQL_INJECTION,
                "CWE-89",
                "Use prepared statements.",
                "SQLite3::SQLException: unrecognized token",
                "https://juice-shop.herokuapp.com/rest/products/search?q='"
        );

        Vulnerability xss = new Vulnerability(
                scan,
                "Reflected Cross-Site Scripting (XSS)",
                "Search term reflected unencoded in client page.",
                Severity.HIGH,
                VulnerabilityCategory.XSS,
                "CWE-79",
                "HTML encode all outputs.",
                "<csAuditToken> reflected in DOM",
                "https://juice-shop.herokuapp.com/#/search"
        );

        scan.addVulnerability(sqli);
        scan.addVulnerability(xss);

        Scan savedScan = scanRepository.save(scan);
        assertNotNull(savedScan.getId());

        // 4. Verify cascade persistence of vulnerabilities
        List<Vulnerability> vulns = vulnerabilityRepository.findByScanId(savedScan.getId());
        assertEquals(2, vulns.size(), "Both vulnerabilities should be persisted via cascade");

        // 5. Create Notification linked to User and Scan
        Notification notification = new Notification(
                user,
                savedScan.getId(),
                "Scan Completed: " + savedScan.getTargetUrl(),
                "Scan #" + savedScan.getId() + " finished with score 52/100",
                "WARNING"
        );
        notification = notificationRepository.save(notification);
        assertNotNull(notification.getId());
        assertFalse(notification.isRead());

        // 6. Test Query Methods
        long unread = notificationRepository.countByUserAndIsReadFalse(user);
        assertEquals(1, unread);

        // Test average security score query
        Double avgScore = scanRepository.calculateAverageSecurityScore();
        assertNotNull(avgScore);
        assertEquals(52.0, avgScore);

        // Test vulnerability filter query
        Page<Vulnerability> filtered = vulnerabilityRepository.filterVulnerabilities(
                Severity.CRITICAL, null, "SQL", PageRequest.of(0, 10)
        );
        assertEquals(1, filtered.getTotalElements());
        assertEquals("SQL Injection in Search Endpoint", filtered.getContent().get(0).getTitle());

        // 7. Test Notification Read Update
        notification.setRead(true);
        notificationRepository.save(notification);
        assertEquals(0, notificationRepository.countByUserAndIsReadFalse(user));
    }
}
