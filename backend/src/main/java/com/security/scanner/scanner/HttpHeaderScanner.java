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
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class HttpHeaderScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(HttpHeaderScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public HttpHeaderScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "HEADERS";
    }

    @Override
    public String getDisplayName() {
        return "HTTP Security Headers Scanner";
    }

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            HttpHeaders headers = response.headers();
            Map<String, List<String>> headerMap = headers.map();

            result.addMetadata("statusCode", response.statusCode());
            result.addMetadata("headersAnalyzedCount", headerMap.size());

            // 1. Check Strict-Transport-Security (HSTS)
            Optional<String> hsts = headers.firstValue("strict-transport-security");
            if (targetUri.getScheme().equalsIgnoreCase("https")) {
                if (hsts.isEmpty()) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Missing HTTP Strict Transport Security (HSTS) Header",
                            "The web application does not enforce HSTS, which exposes users to man-in-the-middle (MITM) downgrade attacks and cookie hijacking.",
                            Severity.MEDIUM,
                            VulnerabilityCategory.SECURITY_HEADERS,
                            "CWE-319",
                            "Add 'Strict-Transport-Security: max-age=31536000; includeSubDomains; preload' to all HTTPS responses.",
                            "Strict-Transport-Security header was not present in the HTTP response.",
                            targetUri.toString()
                    ));
                } else if (!hsts.get().toLowerCase().contains("includesubdomains")) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "HSTS Header Missing 'includeSubDomains' Directive",
                            "The HSTS header does not specify includeSubDomains, leaving subdomains vulnerable to insecure HTTP communication.",
                            Severity.LOW,
                            VulnerabilityCategory.SECURITY_HEADERS,
                            "CWE-319",
                            "Include 'includeSubDomains' in the Strict-Transport-Security header value.",
                            "Strict-Transport-Security: " + hsts.get(),
                            targetUri.toString()
                    ));
                }
            }

            // 2. Content-Security-Policy (CSP)
            Optional<String> csp = headers.firstValue("content-security-policy");
            if (csp.isEmpty()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Missing Content-Security-Policy (CSP) Header",
                        "The application lacks a Content-Security-Policy header, significantly increasing susceptibility to Cross-Site Scripting (XSS), data injection, and clickjacking attacks.",
                        Severity.HIGH,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-693",
                        "Configure a robust Content-Security-Policy header restricting script-src, style-src, default-src, and object-src directives.",
                        "Content-Security-Policy header was absent in response.",
                        targetUri.toString()
                ));
            } else {
                String cspValue = csp.get().toLowerCase();
                if (cspValue.contains("unsafe-inline") || cspValue.contains("unsafe-eval")) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Insecure Directives in Content-Security-Policy (unsafe-inline / unsafe-eval)",
                            "The CSP header allows 'unsafe-inline' or 'unsafe-eval', weakening protections against Cross-Site Scripting (XSS) attacks.",
                            Severity.MEDIUM,
                            VulnerabilityCategory.SECURITY_HEADERS,
                            "CWE-693",
                            "Refactor inline scripts to external files and use cryptographic nonces or hashes instead of 'unsafe-inline'. Avoid 'unsafe-eval'.",
                            "Content-Security-Policy: " + csp.get(),
                            targetUri.toString()
                    ));
                }
            }

            // 3. X-Frame-Options (Clickjacking)
            Optional<String> xfo = headers.firstValue("x-frame-options");
            if (xfo.isEmpty() && (csp.isEmpty() || !csp.get().toLowerCase().contains("frame-ancestors"))) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Missing Clickjacking Protection (X-Frame-Options)",
                        "The web application does not set X-Frame-Options or frame-ancestors in CSP, allowing malicious sites to render this page inside an iframe and perform clickjacking.",
                        Severity.MEDIUM,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-1021",
                        "Set 'X-Frame-Options: DENY' or 'X-Frame-Options: SAMEORIGIN', or use CSP 'frame-ancestors 'self''.",
                        "Neither X-Frame-Options nor CSP frame-ancestors was provided.",
                        targetUri.toString()
                ));
            }

            // 4. X-Content-Type-Options (MIME Sniffing)
            Optional<String> xcto = headers.firstValue("x-content-type-options");
            if (xcto.isEmpty() || !xcto.get().equalsIgnoreCase("nosniff")) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Missing X-Content-Type-Options: nosniff Header",
                        "Browsers may attempt MIME sniffing on responses with missing or invalid content type declarations, potentially leading to script execution.",
                        Severity.LOW,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-79",
                        "Set 'X-Content-Type-Options: nosniff' on all web server responses.",
                        "X-Content-Type-Options header: " + xcto.orElse("None"),
                        targetUri.toString()
                ));
            }

            // 5. Referrer-Policy
            Optional<String> referrer = headers.firstValue("referrer-policy");
            if (referrer.isEmpty()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Missing Referrer-Policy Header",
                        "Without a Referrer-Policy, user navigating away may leak sensitive query parameters or URL path information in the Referer header.",
                        Severity.LOW,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-200",
                        "Set 'Referrer-Policy: strict-origin-when-cross-origin' or 'no-referrer'.",
                        "Referrer-Policy header was not set.",
                        targetUri.toString()
                ));
            }

            // 6. Permissions-Policy
            Optional<String> permissions = headers.firstValue("permissions-policy");
            if (permissions.isEmpty()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Missing Permissions-Policy Header",
                        "The application does not declare browser feature permissions (e.g. camera, microphone, geolocation), allowing embedded third-party frames to request sensitive device APIs.",
                        Severity.INFO,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-693",
                        "Define a Permissions-Policy header, e.g. 'Permissions-Policy: camera=(), microphone=(), geolocation=()'.",
                        "Permissions-Policy header was absent.",
                        targetUri.toString()
                ));
            }

            // 7. Server Header Version Disclosure
            Optional<String> server = headers.firstValue("server");
            if (server.isPresent() && server.get().matches(".*[0-9].*")) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Web Server Version Disclosure in 'Server' Header",
                        "The web server exposes detailed software and version information, assisting attackers in targeting known CVE vulnerabilities for that specific release.",
                        Severity.LOW,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-200",
                        "Configure the web server to suppress or obfuscate version details (e.g., ServerTokens Prod in Apache, server_tokens off in Nginx).",
                        "Server: " + server.get(),
                        targetUri.toString()
                ));
            }

            // 8. X-Powered-By Disclosure
            Optional<String> poweredBy = headers.firstValue("x-powered-by");
            if (poweredBy.isPresent()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Technology Stack Information Disclosure in 'X-Powered-By'",
                        "The application discloses backend framework or language details (e.g., Express, PHP, ASP.NET) via the X-Powered-By header.",
                        Severity.LOW,
                        VulnerabilityCategory.SECURITY_HEADERS,
                        "CWE-200",
                        "Disable or remove the X-Powered-By header in application or framework configuration.",
                        "X-Powered-By: " + poweredBy.get(),
                        targetUri.toString()
                ));
            }

        } catch (Exception e) {
            log.error("Error executing HttpHeaderScanner on {}: {}", targetUri, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("HTTP Header check failed: " + e.getMessage());
        }

        return result;
    }
}
