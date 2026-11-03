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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class SQLInjectionScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(SQLInjectionScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public SQLInjectionScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "SQLI";
    }

    @Override
    public String getDisplayName() {
        return "SQL Injection Assessment Scanner";
    }

    // Database error signatures for passive and benign active detection
    private static final Map<String, Pattern> SQL_ERROR_PATTERNS = Map.of(
            "MySQL", Pattern.compile("(?i)(you have an error in your sql syntax|check the manual that corresponds to your (mysql|mariadb) server version|mysql_fetch_array\\(\\))"),
            "PostgreSQL", Pattern.compile("(?i)(org\\.postgresql\\.util\\.psqlexception|error: syntax error at or near|postgresql query failed)"),
            "Oracle", Pattern.compile("(?i)(ora-01756: quoted string not properly terminated|ora-00933: sql command not properly ended|oracle error)"),
            "Microsoft SQL Server", Pattern.compile("(?i)(unclosed quotation mark after the character string|microsoft ole db provider for sql server|syntax error in string in query expression)"),
            "SQLite", Pattern.compile("(?i)(sqlite3::sqlexception|unrecognized token:|near \".*\": syntax error)")
    );

    // Benign syntax anomaly probes (safe canary characters, not destructive payloads)
    private static final List<String> BENIGN_PROBES = List.of("'", "''", "1'");

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        try {
            // 1. First analyze baseline response
            HttpRequest baseRequest = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> baseResponse = client.send(baseRequest, HttpResponse.BodyHandlers.ofString());
            String baseBody = baseResponse.body() != null ? baseResponse.body() : "";

            // Check if baseline already leaks database errors (passive detection)
            for (Map.Entry<String, Pattern> entry : SQL_ERROR_PATTERNS.entrySet()) {
                if (entry.getValue().matcher(baseBody).find()) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Database Error Message Leaked in Baseline Response (" + entry.getKey() + ")",
                            "The web application exposes raw database error messages in normal responses without handling exceptions. This reveals underlying database technology and query structure.",
                            Severity.MEDIUM,
                            VulnerabilityCategory.SQL_INJECTION,
                            "CWE-209",
                            "Implement centralized database exception handling and never expose internal SQL errors to end users.",
                            "Detected error pattern for: " + entry.getKey(),
                            targetUri.toString()
                    ));
                    break;
                }
            }

            // 2. Identify parameters to test
            Map<String, String> queryParams = extractQueryParams(targetUri);
            if (queryParams.isEmpty()) {
                // If no query parameters, test with a benign test parameter to evaluate parameter handling
                queryParams.put("id", "1");
                queryParams.put("search", "test");
            }

            // Test parameters with benign syntax probes
            boolean vulnFound = false;
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                if (vulnFound) break;

                for (String probe : BENIGN_PROBES) {
                    URI testUri = buildProbeUri(targetUri, param.getKey(), param.getValue() + probe);

                    try {
                        HttpRequest probeRequest = HttpRequest.newBuilder()
                                .uri(testUri)
                                .timeout(Duration.ofSeconds(6))
                                .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                                .GET()
                                .build();

                        HttpResponse<String> probeResponse = client.send(probeRequest, HttpResponse.BodyHandlers.ofString());
                        String probeBody = probeResponse.body() != null ? probeResponse.body() : "";

                        // Inspect response for database errors that were NOT present in baseline
                        for (Map.Entry<String, Pattern> errEntry : SQL_ERROR_PATTERNS.entrySet()) {
                            if (errEntry.getValue().matcher(probeBody).find() && !errEntry.getValue().matcher(baseBody).find()) {
                                result.addVulnerability(new Vulnerability(
                                        scan,
                                        "Potential SQL Injection Vulnerability Detected in Parameter '" + param.getKey() + "'",
                                        "Submitting benign syntax test input into parameter '" + param.getKey() + "' triggered a database syntax error (" + errEntry.getKey() + "), indicating dynamic SQL concatenation without parameterized queries.",
                                        Severity.HIGH,
                                        VulnerabilityCategory.SQL_INJECTION,
                                        "CWE-89",
                                        "Use parameterized queries (PreparedStatements in Java, parameterized queries in ORMs) and avoid dynamic string concatenation in SQL commands.",
                                        "Database engine: " + errEntry.getKey() + ". Probe URL: " + testUri,
                                        testUri.toString()
                                ));
                                vulnFound = true;
                                break;
                            }
                        }

                        // Also check for 500 Internal Server Error difference
                        if (!vulnFound && baseResponse.statusCode() == 200 && probeResponse.statusCode() == 500) {
                            if (probeBody.toLowerCase().contains("sql") || probeBody.toLowerCase().contains("database") || probeBody.toLowerCase().contains("syntax")) {
                                result.addVulnerability(new Vulnerability(
                                        scan,
                                        "Unhandled Database Exception Triggered in Parameter '" + param.getKey() + "'",
                                        "The application returned an HTTP 500 Internal Server Error with database-related terms upon receiving quote character test inputs in parameter '" + param.getKey() + "'.",
                                        Severity.MEDIUM,
                                        VulnerabilityCategory.SQL_INJECTION,
                                        "CWE-89",
                                        "Validate and sanitize input parameters and ensure all database queries use bound parameters.",
                                        "HTTP 500 received at " + testUri,
                                        testUri.toString()
                                ));
                                vulnFound = true;
                                break;
                            }
                        }

                    } catch (Exception e) {
                        log.debug("Probe to {} timed out: {}", testUri, e.getMessage());
                    }
                }
            }

            result.addMetadata("parametersTested", queryParams.size());

        } catch (Exception e) {
            log.error("SQL Injection scan failed for {}: {}", targetUri, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("SQL Injection scanner error: " + e.getMessage());
        }

        return result;
    }

    private Map<String, String> extractQueryParams(URI uri) {
        Map<String, String> map = new LinkedHashMap<>();
        String query = uri.getQuery();
        if (query != null && !query.isEmpty()) {
            for (String pair : query.split("&")) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    map.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        }
        return map;
    }

    private URI buildProbeUri(URI baseUri, String paramKey, String probeValue) {
        String base = baseUri.getScheme() + "://" + baseUri.getAuthority() + baseUri.getPath();
        Map<String, String> params = extractQueryParams(baseUri);
        params.put(paramKey, probeValue);

        StringBuilder sb = new StringBuilder(base).append("?");
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (i++ > 0) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
              .append("=")
              .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return URI.create(sb.toString());
    }
}
