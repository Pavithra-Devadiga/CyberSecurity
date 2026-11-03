package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.util.SafeHttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class EndpointExposureScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(EndpointExposureScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public EndpointExposureScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "EXPOSURE";
    }

    @Override
    public String getDisplayName() {
        return "Endpoint & Information Exposure Scanner";
    }

    private record ProbeEndpoint(
            String path,
            String title,
            String description,
            Severity severity,
            String cwe,
            String remediation,
            String contentSignature
    ) {}

    private static final List<ProbeEndpoint> PROBES = List.of(
            new ProbeEndpoint(
                    "/.env",
                    "Exposed Environment Configuration File (.env)",
                    "A .env file was discovered publicly accessible. This file frequently contains database passwords, API credentials, encryption secrets, and private tokens.",
                    Severity.CRITICAL,
                    "CWE-200",
                    "Block access to dotfiles (.env, .git) in web server configuration and remove sensitive files from public webroot.",
                    "DB_PASSWORD="
            ),
            new ProbeEndpoint(
                    "/.git/HEAD",
                    "Exposed Git Version Control Repository (/.git/HEAD)",
                    "The Git repository metadata is accessible over HTTP. Attackers can download repository history, view source code, and retrieve hardcoded secrets.",
                    Severity.HIGH,
                    "CWE-538",
                    "Deny access to the /.git/ folder in Nginx (location ~ /\\.git { deny all; }) or Apache configuration.",
                    "ref: refs/"
            ),
            new ProbeEndpoint(
                    "/actuator/env",
                    "Exposed Spring Boot Actuator Environment (/actuator/env)",
                    "The Spring Boot Actuator /env endpoint is publicly exposed, allowing anyone to read environment variables, system properties, and application configuration.",
                    Severity.HIGH,
                    "CWE-200",
                    "Configure management.endpoints.web.exposure.include to exclude 'env', or protect actuator endpoints with Spring Security.",
                    "propertySources"
            ),
            new ProbeEndpoint(
                    "/actuator/health",
                    "Publicly Accessible Spring Boot Actuator Health Endpoint",
                    "The /actuator/health endpoint is reachable without authentication, providing operational details about database connections and disk space.",
                    Severity.INFO,
                    "CWE-200",
                    "Restrict management endpoint visibility or sanitize health indicator details for unauthenticated users.",
                    "\"status\":\"UP\""
            ),
            new ProbeEndpoint(
                    "/swagger-ui/index.html",
                    "Publicly Accessible Swagger / OpenAPI UI",
                    "The interactive API documentation UI is accessible without authentication, exposing internal endpoints, parameters, and schema designs.",
                    Severity.LOW,
                    "CWE-200",
                    "Restrict Swagger UI to internal development environments or require administrative authentication.",
                    "swagger-ui"
            ),
            new ProbeEndpoint(
                    "/v3/api-docs",
                    "Exposed OpenAPI Specification (/v3/api-docs)",
                    "The raw OpenAPI 3.0 JSON specification is publicly available, allowing automated mapping of all backend endpoints and request schemas.",
                    Severity.LOW,
                    "CWE-200",
                    "Restrict API documentation endpoints behind authentication in production.",
                    "\"openapi\":"
            ),
            new ProbeEndpoint(
                    "/robots.txt",
                    "Robots.txt File Disclosing Restricted Paths",
                    "The robots.txt file was found and discloses disallowed paths that may point to administrative or sensitive subdirectories.",
                    Severity.INFO,
                    "CWE-200",
                    "Review robots.txt to ensure it does not reveal sensitive private directories.",
                    "Disallow:"
            ),
            new ProbeEndpoint(
                    "/.DS_Store",
                    "Exposed macOS Directory Metadata (.DS_Store)",
                    "A .DS_Store file is exposed, which can disclose file names, directory structures, and deleted files on macOS-hosted servers.",
                    Severity.LOW,
                    "CWE-200",
                    "Remove .DS_Store files from web deployment pipelines and configure web server rules to deny access.",
                    null
            )
    );

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        String baseUrl = targetUri.getScheme() + "://" + targetUri.getAuthority();
        int testedCount = 0;

        for (ProbeEndpoint probe : PROBES) {
            testedCount++;
            String probeUrl = baseUrl + probe.path();

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(probeUrl))
                        .timeout(Duration.ofSeconds(4))
                        .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 && response.body() != null) {
                    String body = response.body();

                    // Check content signature if defined, or check that it's not a generic HTML 404 page returning 200
                    boolean verified = true;
                    if (probe.contentSignature() != null) {
                        verified = body.contains(probe.contentSignature());
                    } else {
                        // Avoid false positives for soft-404s
                        if (body.toLowerCase().contains("not found") || body.toLowerCase().contains("page not found")
                                || body.toLowerCase().contains("404")) {
                            verified = false;
                        }
                    }

                    if (verified && !body.trim().isEmpty()) {
                        String snippet = body.length() > 300 ? body.substring(0, 300) + "..." : body;
                        result.addVulnerability(new Vulnerability(
                                scan,
                                probe.title(),
                                probe.description(),
                                probe.severity(),
                                VulnerabilityCategory.ENDPOINT_EXPOSURE,
                                probe.cwe(),
                                probe.remediation(),
                                "HTTP 200 OK returned at " + probeUrl + ". Response preview:\n" + snippet,
                                probeUrl
                        ));
                    }
                }
            } catch (Exception e) {
                log.debug("Probe to {} timed out or failed: {}", probeUrl, e.getMessage());
            }
        }

        result.addMetadata("endpointsTested", testedCount);
        return result;
    }
}
