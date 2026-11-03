package com.security.scanner.config;

import com.security.scanner.entity.Notification;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.User;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Role;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.repository.NotificationRepository;
import com.security.scanner.repository.ScanRepository;
import com.security.scanner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ScanRepository scanRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            ScanRepository scanRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.scanRepository = scanRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing default users and demo security scans...");

            // Create Admin
            User admin = new User(
                    "admin",
                    "admin@cybersecurity.local",
                    passwordEncoder.encode("admin123"),
                    Role.ROLE_ADMIN
            );
            admin = userRepository.save(admin);

            // Create Standard User
            User user = new User(
                    "demo_analyst",
                    "analyst@cybersecurity.local",
                    passwordEncoder.encode("password123"),
                    Role.ROLE_USER
            );
            user = userRepository.save(user);

            // Sample Scan 1: E-commerce portal
            Scan scan1 = new Scan("https://demo-ecommerce.internal.example", ScanType.FULL, admin);
            scan1.setStatus(ScanStatus.COMPLETED);
            scan1.setSecurityScore(68);
            scan1.setLetterGrade("C");
            scan1.setStartTime(LocalDateTime.now().minusHours(2));
            scan1.setEndTime(LocalDateTime.now().minusHours(2).plusMinutes(3));
            scan1.setHeadersAnalyzed(18);
            scan1.setEndpointsTested(12);
            scan1.setSslValid(true);
            scan1.setTlsVersion("TLSv1.3");
            scan1.setTechnologiesFound("Web Server: nginx/1.24.0, React UI, Express / Node.js");

            scan1.addVulnerability(new Vulnerability(
                    scan1,
                    "Missing Content-Security-Policy (CSP) Header",
                    "The application lacks a Content-Security-Policy header, significantly increasing susceptibility to Cross-Site Scripting (XSS).",
                    Severity.HIGH,
                    VulnerabilityCategory.SECURITY_HEADERS,
                    "CWE-693",
                    "Configure a robust Content-Security-Policy header restricting script-src and default-src directives.",
                    "CSP header missing in response.",
                    "https://demo-ecommerce.internal.example"
            ));

            scan1.addVulnerability(new Vulnerability(
                    scan1,
                    "Missing Clickjacking Protection (X-Frame-Options)",
                    "The web application does not set X-Frame-Options or frame-ancestors, allowing malicious framing in iframes.",
                    Severity.MEDIUM,
                    VulnerabilityCategory.SECURITY_HEADERS,
                    "CWE-1021",
                    "Set X-Frame-Options: SAMEORIGIN or use CSP frame-ancestors.",
                    "X-Frame-Options header not present.",
                    "https://demo-ecommerce.internal.example"
            ));

            scan1.addVulnerability(new Vulnerability(
                    scan1,
                    "Exposed OpenAPI Specification (/v3/api-docs)",
                    "The raw OpenAPI 3.0 JSON specification is publicly available without authentication.",
                    Severity.LOW,
                    VulnerabilityCategory.ENDPOINT_EXPOSURE,
                    "CWE-200",
                    "Restrict API documentation endpoints behind authentication in production.",
                    "HTTP 200 returned at /v3/api-docs",
                    "https://demo-ecommerce.internal.example/v3/api-docs"
            ));

            scanRepository.save(scan1);

            // Sample Scan 2: Banking API gateway
            Scan scan2 = new Scan("https://api.banking-secure.example", ScanType.HEADERS_ONLY, admin);
            scan2.setStatus(ScanStatus.COMPLETED);
            scan2.setSecurityScore(95);
            scan2.setLetterGrade("A+");
            scan2.setStartTime(LocalDateTime.now().minusDays(1));
            scan2.setEndTime(LocalDateTime.now().minusDays(1).plusMinutes(1));
            scan2.setHeadersAnalyzed(22);
            scan2.setEndpointsTested(5);
            scan2.setSslValid(true);
            scan2.setTlsVersion("TLSv1.3");
            scan2.setTechnologiesFound("Web Server: Envoy Proxy, Spring Boot 3.4");

            scan2.addVulnerability(new Vulnerability(
                    scan2,
                    "Missing Permissions-Policy Header",
                    "The application does not declare browser feature permissions in HTTP headers.",
                    Severity.INFO,
                    VulnerabilityCategory.SECURITY_HEADERS,
                    "CWE-693",
                    "Define a Permissions-Policy header restricting geolocation and camera access.",
                    "Permissions-Policy header was absent.",
                    "https://api.banking-secure.example"
            ));

            scanRepository.save(scan2);

            // Create initial notifications
            notificationRepository.save(new Notification(
                    admin,
                    scan1.getId(),
                    "Scan Completed: demo-ecommerce.internal.example",
                    "Scan #1 finished with Security Score 68/100 (Grade C). Found 3 vulnerabilities.",
                    "WARNING"
            ));

            notificationRepository.save(new Notification(
                    admin,
                    scan2.getId(),
                    "Scan Completed: api.banking-secure.example",
                    "Scan #2 finished with Security Score 95/100 (Grade A+). Found 1 informational item.",
                    "SUCCESS"
            ));

            log.info("Default user 'admin' created with password 'admin123'");
        }
    }
}
