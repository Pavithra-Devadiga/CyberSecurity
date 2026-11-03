package com.security.scanner.dto.request;

import com.security.scanner.entity.enums.ScanType;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class ScanRequest {

    @NotBlank(message = "Target URL is required")
    private String targetUrl;

    private ScanType scanType = ScanType.QUICK;

    // Optional list of enabled modules: "HEADERS", "SSL", "EXPOSURE", "SENSITIVE", "TECH", "SQLI", "XSS", "ZAP"
    private List<String> modules;

    public ScanRequest() {}

    public ScanRequest(String targetUrl, ScanType scanType) {
        this.targetUrl = targetUrl;
        this.scanType = scanType;
    }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public ScanType getScanType() { return scanType; }
    public void setScanType(ScanType scanType) { this.scanType = scanType; }

    public List<String> getModules() { return modules; }
    public void setModules(List<String> modules) { this.modules = modules; }
}
