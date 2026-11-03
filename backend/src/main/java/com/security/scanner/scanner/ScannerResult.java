package com.security.scanner.scanner;

import com.security.scanner.entity.Vulnerability;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerResult {

    private String moduleName;
    private boolean success = true;
    private String errorMessage;
    private List<Vulnerability> vulnerabilities = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();

    public ScannerResult() {}

    public ScannerResult(String moduleName) {
        this.moduleName = moduleName;
    }

    public void addVulnerability(Vulnerability vulnerability) {
        this.vulnerabilities.add(vulnerability);
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public List<Vulnerability> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<Vulnerability> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
