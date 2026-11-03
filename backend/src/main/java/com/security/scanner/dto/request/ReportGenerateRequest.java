package com.security.scanner.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReportGenerateRequest {

    @NotNull(message = "Scan ID is required")
    private Long scanId;

    private String format = "PDF"; // "PDF", "JSON", "CSV"

    private boolean includeRemediation = true;

    public ReportGenerateRequest() {}

    public ReportGenerateRequest(Long scanId, String format) {
        this.scanId = scanId;
        this.format = format;
    }

    public Long getScanId() { return scanId; }
    public void setScanId(Long scanId) { this.scanId = scanId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public boolean isIncludeRemediation() { return includeRemediation; }
    public void setIncludeRemediation(boolean includeRemediation) { this.includeRemediation = includeRemediation; }
}
