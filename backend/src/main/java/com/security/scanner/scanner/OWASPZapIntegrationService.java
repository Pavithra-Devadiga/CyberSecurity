package com.security.scanner.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.util.SafeHttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OWASPZapIntegrationService implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(OWASPZapIntegrationService.class);

    @Value("${scanner.zap.enabled:false}")
    private boolean zapEnabled;

    @Value("${scanner.zap.base-url:http://localhost:8090}")
    private String zapBaseUrl;

    @Value("${scanner.zap.api-key:}")
    private String zapApiKey;

    private final SafeHttpClientFactory clientFactory;
    private final ObjectMapper objectMapper;

    public OWASPZapIntegrationService(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getModuleId() {
        return "ZAP";
    }

    @Override
    public String getDisplayName() {
        return "OWASP ZAP REST API Integration";
    }

    /**
     * Checks if the OWASP ZAP daemon is reachable via its REST API.
     */
    public boolean isZapAvailable() {
        if (!zapEnabled) return false;
        try {
            HttpClient client = clientFactory.createStandardClient();
            String url = zapBaseUrl + "/JSON/core/view/version/?apikey=" + URLEncoder.encode(zapApiKey, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return resp.statusCode() == 200;
        } catch (Exception e) {
            log.debug("OWASP ZAP is not reachable: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());

        if (!zapEnabled) {
            result.addMetadata("status", "DISABLED");
            result.addMetadata("message", "OWASP ZAP integration is disabled in configuration (scanner.zap.enabled=false).");
            return result;
        }

        if (!isZapAvailable()) {
            result.addMetadata("status", "UNAVAILABLE");
            result.addMetadata("message", "OWASP ZAP daemon is not responding at " + zapBaseUrl + ". Check that the ZAP container/service is running.");
            return result;
        }

        HttpClient client = clientFactory.createStandardClient();
        String targetStr = targetUri.toString();

        try {
            // 1. Trigger Spider
            String spiderUrl = zapBaseUrl + "/JSON/spider/action/scan/?url=" + URLEncoder.encode(targetStr, StandardCharsets.UTF_8)
                    + "&apikey=" + URLEncoder.encode(zapApiKey, StandardCharsets.UTF_8);

            HttpRequest spiderReq = HttpRequest.newBuilder().uri(URI.create(spiderUrl)).GET().build();
            HttpResponse<String> spiderResp = client.send(spiderReq, HttpResponse.BodyHandlers.ofString());

            JsonNode spiderNode = objectMapper.readTree(spiderResp.body());
            String spiderScanId = spiderNode.path("scan").asText();
            result.addMetadata("zapSpiderScanId", spiderScanId);

            // Wait a few seconds for spider or query existing alerts
            Thread.sleep(3000);

            // 2. Fetch ZAP Alerts
            String alertsUrl = zapBaseUrl + "/JSON/core/view/alerts/?baseurl=" + URLEncoder.encode(targetStr, StandardCharsets.UTF_8)
                    + "&apikey=" + URLEncoder.encode(zapApiKey, StandardCharsets.UTF_8);

            HttpRequest alertsReq = HttpRequest.newBuilder().uri(URI.create(alertsUrl)).GET().build();
            HttpResponse<String> alertsResp = client.send(alertsReq, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(alertsResp.body());
            JsonNode alerts = root.path("alerts");

            if (alerts.isArray()) {
                for (JsonNode alert : alerts) {
                    String alertTitle = alert.path("alert").asText("OWASP ZAP Finding");
                    String risk = alert.path("risk").asText("Low");
                    String description = alert.path("description").asText();
                    String solution = alert.path("solution").asText();
                    String evidence = alert.path("evidence").asText();
                    String cweId = alert.path("cweid").asText();
                    String url = alert.path("url").asText(targetStr);

                    Severity severity = mapZapRiskToSeverity(risk);

                    result.addVulnerability(new Vulnerability(
                            scan,
                            "[ZAP] " + alertTitle,
                            description,
                            severity,
                            VulnerabilityCategory.OWASP_ZAP,
                            cweId.isEmpty() ? "CWE-693" : "CWE-" + cweId,
                            solution,
                            "ZAP Alert Risk: " + risk + ". Evidence: " + evidence,
                            url
                    ));
                }
            }

            result.addMetadata("alertsRetrieved", alerts.size());

        } catch (Exception e) {
            log.error("OWASP ZAP integration failed for {}: {}", targetStr, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("OWASP ZAP REST API error: " + e.getMessage());
        }

        return result;
    }

    private Severity mapZapRiskToSeverity(String risk) {
        if (risk == null) return Severity.INFO;
        return switch (risk.toLowerCase()) {
            case "high" -> Severity.HIGH;
            case "medium" -> Severity.MEDIUM;
            case "low" -> Severity.LOW;
            default -> Severity.INFO;
        };
    }
}
