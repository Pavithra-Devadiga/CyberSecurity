package com.security.scanner.service;

import com.security.scanner.dto.request.ScanRequest;
import com.security.scanner.dto.response.ScanDetailResponse;
import com.security.scanner.dto.response.ScanResponse;
import com.security.scanner.dto.response.VulnerabilityDto;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.User;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.exception.ResourceNotFoundException;
import com.security.scanner.repository.ScanRepository;
import com.security.scanner.repository.UserRepository;
import com.security.scanner.util.SsrfProtectionValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScanService {

    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final ScanOrchestratorService orchestratorService;
    private final SsrfProtectionValidator ssrfValidator;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ScanService(
            ScanRepository scanRepository,
            UserRepository userRepository,
            ScanOrchestratorService orchestratorService,
            SsrfProtectionValidator ssrfValidator
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.orchestratorService = orchestratorService;
        this.ssrfValidator = ssrfValidator;
    }

    @Transactional
    public ScanResponse initiateScan(ScanRequest request) {
        // Validate target URL against SSRF policy immediately
        ssrfValidator.validateAndNormalize(request.getTargetUrl());

        User currentUser = getCurrentUser();
        Scan scan = new Scan(request.getTargetUrl(), request.getScanType(), currentUser);
        scan = scanRepository.save(scan);

        final Long scanId = scan.getId();
        final List<String> modules = request.getModules();

        // Launch execution asynchronously
        CompletableFuture.runAsync(() -> orchestratorService.executeScan(scanId, modules), executorService);

        return mapToScanResponse(scan);
    }

    @Transactional(readOnly = true)
    public Page<ScanResponse> getUserScans(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Scan> scans = scanRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        List<ScanResponse> responses = scans.stream().map(this::mapToScanResponse).toList();
        return new PageImpl<>(responses, pageable, scans.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ScanDetailResponse getScanDetails(Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found with id: " + scanId));

        return mapToScanDetailResponse(scan);
    }

    @Transactional
    public ScanResponse cancelScan(Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found with id: " + scanId));

        if (scan.getStatus() == ScanStatus.RUNNING || scan.getStatus() == ScanStatus.QUEUED) {
            scan.setStatus(ScanStatus.CANCELLED);
            scan = scanRepository.save(scan);
        }

        return mapToScanResponse(scan);
    }

    public ScanResponse mapToScanResponse(Scan scan) {
        ScanResponse res = new ScanResponse();
        res.setId(scan.getId());
        res.setTargetUrl(scan.getTargetUrl());
        res.setScanType(scan.getScanType());
        res.setStatus(scan.getStatus());
        res.setSecurityScore(scan.getSecurityScore());
        res.setLetterGrade(scan.getLetterGrade());
        res.setStartTime(scan.getStartTime());
        res.setEndTime(scan.getEndTime());
        res.setCreatedAt(scan.getCreatedAt());

        int total = scan.getVulnerabilities() != null ? scan.getVulnerabilities().size() : 0;
        int crit = 0, high = 0, med = 0, low = 0, info = 0;

        if (scan.getVulnerabilities() != null) {
            for (Vulnerability v : scan.getVulnerabilities()) {
                if (v.getSeverity() == Severity.CRITICAL) crit++;
                else if (v.getSeverity() == Severity.HIGH) high++;
                else if (v.getSeverity() == Severity.MEDIUM) med++;
                else if (v.getSeverity() == Severity.LOW) low++;
                else info++;
            }
        }

        res.setVulnerabilityCount(total);
        res.setCriticalCount(crit);
        res.setHighCount(high);
        res.setMediumCount(med);
        res.setLowCount(low);
        res.setInfoCount(info);

        return res;
    }

    public ScanDetailResponse mapToScanDetailResponse(Scan scan) {
        ScanDetailResponse res = new ScanDetailResponse();
        res.setId(scan.getId());
        res.setTargetUrl(scan.getTargetUrl());
        res.setScanType(scan.getScanType());
        res.setStatus(scan.getStatus());
        res.setSecurityScore(scan.getSecurityScore());
        res.setLetterGrade(scan.getLetterGrade());
        res.setStartTime(scan.getStartTime());
        res.setEndTime(scan.getEndTime());
        res.setHeadersAnalyzed(scan.getHeadersAnalyzed());
        res.setEndpointsTested(scan.getEndpointsTested());
        res.setSslValid(scan.getSslValid());
        res.setTlsVersion(scan.getTlsVersion());
        res.setTechnologiesFound(scan.getTechnologiesFound());
        res.setErrorMessage(scan.getErrorMessage());
        res.setCreatedAt(scan.getCreatedAt());

        if (scan.getVulnerabilities() != null) {
            List<VulnerabilityDto> vList = scan.getVulnerabilities().stream().map(v -> {
                VulnerabilityDto dto = new VulnerabilityDto();
                dto.setId(v.getId());
                dto.setScanId(scan.getId());
                dto.setTitle(v.getTitle());
                dto.setDescription(v.getDescription());
                dto.setSeverity(v.getSeverity());
                dto.setCategory(v.getCategory());
                dto.setCwe(v.getCwe());
                dto.setRemediation(v.getRemediation());
                dto.setEvidence(v.getEvidence());
                dto.setAffectedUrl(v.getAffectedUrl());
                dto.setParameter(v.getParameter());
                dto.setDetectedAt(v.getDetectedAt());
                return dto;
            }).toList();
            res.setVulnerabilities(vList);
        }

        return res;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
