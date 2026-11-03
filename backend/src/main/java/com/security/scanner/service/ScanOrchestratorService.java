package com.security.scanner.service;

import com.security.scanner.entity.Notification;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import com.security.scanner.repository.NotificationRepository;
import com.security.scanner.repository.ScanRepository;
import com.security.scanner.scanner.ScannerModule;
import com.security.scanner.scanner.ScannerResult;
import com.security.scanner.util.SsrfProtectionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ScanOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(ScanOrchestratorService.class);

    private final List<ScannerModule> scannerModules;
    private final ScanRepository scanRepository;
    private final NotificationRepository notificationRepository;
    private final SsrfProtectionValidator ssrfValidator;
    private final SecurityScoreService scoreService;

    public ScanOrchestratorService(
            List<ScannerModule> scannerModules,
            ScanRepository scanRepository,
            NotificationRepository notificationRepository,
            SsrfProtectionValidator ssrfValidator,
            SecurityScoreService scoreService
    ) {
        this.scannerModules = scannerModules;
        this.scanRepository = scanRepository;
        this.notificationRepository = notificationRepository;
        this.ssrfValidator = ssrfValidator;
        this.scoreService = scoreService;
    }

    /**
     * Executes the scan workflow synchronously or via background task.
     */
    @Transactional
    public void executeScan(Long scanId, List<String> requestedModules) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new IllegalArgumentException("Scan not found: " + scanId));

        scan.setStatus(ScanStatus.RUNNING);
        scan.setStartTime(LocalDateTime.now());
        scanRepository.save(scan);

        try {
            // 1. SSRF Validation
            URI targetUri = ssrfValidator.validateAndNormalize(scan.getTargetUrl());

            // 2. Select Active Scanner Modules
            List<ScannerModule> activeModules = selectModules(scan.getScanType(), requestedModules);
            log.info("Executing scan #{} against {} with {} modules", scan.getId(), targetUri, activeModules.size());

            List<Vulnerability> allVulnerabilities = new ArrayList<>();
            Set<String> technologies = new LinkedHashSet<>();

            for (ScannerModule module : activeModules) {
                try {
                    log.debug("Running module '{}' for scan #{}", module.getModuleId(), scan.getId());
                    ScannerResult res = module.scan(targetUri, scan);

                    if (res != null && res.getVulnerabilities() != null) {
                        for (Vulnerability v : res.getVulnerabilities()) {
                            scan.addVulnerability(v);
                            allVulnerabilities.add(v);
                        }

                        // Collect metadata
                        if (res.getMetadata().containsKey("headersAnalyzedCount")) {
                            scan.setHeadersAnalyzed((Integer) res.getMetadata().get("headersAnalyzedCount"));
                        }
                        if (res.getMetadata().containsKey("endpointsTested")) {
                            scan.setEndpointsTested((Integer) res.getMetadata().get("endpointsTested"));
                        }
                        if (res.getMetadata().containsKey("sslValid")) {
                            scan.setSslValid((Boolean) res.getMetadata().get("sslValid"));
                        }
                        if (res.getMetadata().containsKey("tlsVersion")) {
                            scan.setTlsVersion((String) res.getMetadata().get("tlsVersion"));
                        }
                        if (res.getMetadata().containsKey("technologiesFound")) {
                            technologies.add((String) res.getMetadata().get("technologiesFound"));
                        }
                    }
                } catch (Exception ex) {
                    log.error("Scanner module {} failed on scan #{}: {}", module.getModuleId(), scan.getId(), ex.getMessage());
                }
            }

            if (!technologies.isEmpty()) {
                scan.setTechnologiesFound(String.join(", ", technologies));
            }

            // 3. Compute Security Score
            SecurityScoreService.ScoreResult scoreResult = scoreService.calculateScore(allVulnerabilities);
            scan.setSecurityScore(scoreResult.score());
            scan.setLetterGrade(scoreResult.grade());
            scan.setStatus(ScanStatus.COMPLETED);
            scan.setEndTime(LocalDateTime.now());
            scanRepository.save(scan);

            // 4. Create Notification
            if (scan.getUser() != null) {
                Notification notif = new Notification(
                        scan.getUser(),
                        scan.getId(),
                        "Scan Completed: " + scan.getTargetUrl(),
                        "Scan #" + scan.getId() + " finished with Security Score " + scoreResult.score()
                                + "/100 (Grade " + scoreResult.grade() + "). Found " + allVulnerabilities.size() + " findings.",
                        scoreResult.score() >= 75 ? "SUCCESS" : "WARNING"
                );
                notificationRepository.save(notif);
            }

            log.info("Scan #{} finished successfully with score {}", scan.getId(), scoreResult.score());

        } catch (Exception e) {
            log.error("Scan #{} failed: {}", scan.getId(), e.getMessage(), e);
            scan.setStatus(ScanStatus.FAILED);
            scan.setErrorMessage(e.getMessage());
            scan.setEndTime(LocalDateTime.now());
            scanRepository.save(scan);

            if (scan.getUser() != null) {
                Notification notif = new Notification(
                        scan.getUser(),
                        scan.getId(),
                        "Scan Failed: " + scan.getTargetUrl(),
                        "Scan #" + scan.getId() + " encountered an error: " + e.getMessage(),
                        "ERROR"
                );
                notificationRepository.save(notif);
            }
        }
    }

    private List<ScannerModule> selectModules(ScanType scanType, List<String> requestedModules) {
        if (scanType == ScanType.HEADERS_ONLY) {
            return scannerModules.stream().filter(m -> m.getModuleId().equals("HEADERS")).toList();
        }
        if (scanType == ScanType.SSL_ONLY) {
            return scannerModules.stream().filter(m -> m.getModuleId().equals("SSL")).toList();
        }
        if (scanType == ScanType.QUICK) {
            return scannerModules.stream().filter(m ->
                    m.getModuleId().equals("HEADERS") || m.getModuleId().equals("SSL") || m.getModuleId().equals("TECH")
            ).toList();
        }
        if (scanType == ScanType.CUSTOM && requestedModules != null && !requestedModules.isEmpty()) {
            Set<String> requested = new HashSet<>(requestedModules);
            return scannerModules.stream().filter(m -> requested.contains(m.getModuleId())).toList();
        }
        // FULL Scan: include all scanners
        return scannerModules;
    }
}
