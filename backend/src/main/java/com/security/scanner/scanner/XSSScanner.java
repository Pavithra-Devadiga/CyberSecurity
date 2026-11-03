package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.util.SafeHttpClientFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class XSSScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(XSSScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public XSSScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "XSS";
    }

    @Override
    public String getDisplayName() {
        return "Cross-Site Scripting (XSS) Assessment Scanner";
    }

    // Benign test probe containing HTML meta-characters for reflection and encoding analysis
    private static final String CANARY_TAG = "csAuditToken";
    private static final String BENIGN_TEST_TOKEN = "csAudit<" + CANARY_TAG + ">probe\"'";

    // DOM XSS Sink regex patterns for passive script analysis
    private static final Pattern DOM_XSS_PATTERN = Pattern.compile(
            "(?i)(document\\.write\\s*\\([^)]*(location|window\\.name|document\\.URL|document\\.referrer)[^)]*\\)|" +
            "\\.innerHTML\\s*=\\s*[^;]*(location\\.(search|hash|href)|document\\.referrer)|" +
            "eval\\s*\\([^)]*(location\\.(search|hash|href))[^)]*\\))"
    );

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        try {
            // 1. Fetch baseline HTML
            HttpRequest baseRequest = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> baseResponse = client.send(baseRequest, HttpResponse.BodyHandlers.ofString());
            String baseBody = baseResponse.body() != null ? baseResponse.body() : "";

            // 2. Passive DOM XSS source/sink pattern search
            Matcher domMatcher = DOM_XSS_PATTERN.matcher(baseBody);
            if (domMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Potential DOM-Based XSS Sink Detected in Client Script",
                        "The application's JavaScript contains dangerous DOM manipulation patterns writing untrusted location data directly into innerHTML or document.write without sanitization.",
                        Severity.HIGH,
                        VulnerabilityCategory.XSS,
                        "CWE-79",
                        "Avoid writing untrusted input to innerHTML, outerHTML, or document.write. Use textContent or DOMPurify.",
                        "Identified DOM sink pattern: " + domMatcher.group(),
                        targetUri.toString()
                ));
            }

            // 3. Extract parameters from URI and forms
            Map<String, String> testParams = new LinkedHashMap<>();
            String query = targetUri.getQuery();
            if (query != null && !query.isEmpty()) {
                for (String pair : query.split("&")) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        testParams.put(pair.substring(0, idx), pair.substring(idx + 1));
                    }
                }
            }

            // Also inspect HTML forms on page for input fields
            if (!baseBody.isEmpty()) {
                Document doc = Jsoup.parse(baseBody);
                Elements inputs = doc.select("form input[name], form textarea[name]");
                for (Element input : inputs) {
                    String name = input.attr("name");
                    if (!name.isEmpty() && !testParams.containsKey(name)) {
                        testParams.put(name, "test");
                    }
                }
            }

            if (testParams.isEmpty()) {
                testParams.put("q", "test");
                testParams.put("search", "test");
                testParams.put("query", "test");
            }

            // 4. Test reflection with benign canary token
            boolean xssFound = false;
            for (Map.Entry<String, String> entry : testParams.entrySet()) {
                if (xssFound) break;

                String paramName = entry.getKey();
                URI probeUri = buildProbeUri(targetUri, paramName, BENIGN_TEST_TOKEN);

                try {
                    HttpRequest probeRequest = HttpRequest.newBuilder()
                            .uri(probeUri)
                            .timeout(Duration.ofSeconds(6))
                            .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                            .GET()
                            .build();

                    HttpResponse<String> probeResponse = client.send(probeRequest, HttpResponse.BodyHandlers.ofString());
                    String probeBody = probeResponse.body() != null ? probeResponse.body() : "";

                    // Check if the canary was reflected verbatim without HTML encoding
                    if (probeBody.contains("<" + CANARY_TAG + ">")) {
                        result.addVulnerability(new Vulnerability(
                                scan,
                                "Reflected Cross-Site Scripting (XSS) in Parameter '" + paramName + "'",
                                "The application reflects user-supplied HTML tag characters ('<" + CANARY_TAG + ">') verbatim in the HTTP response without contextual output encoding or sanitization.",
                                Severity.HIGH,
                                VulnerabilityCategory.XSS,
                                "CWE-79",
                                "Apply context-aware HTML entity encoding on all user reflection points and configure a restrictive Content-Security-Policy.",
                                "Unencoded reflection verified for parameter '" + paramName + "' in response from " + probeUri,
                                probeUri.toString()
                        ));
                        xssFound = true;
                    } else if (probeBody.contains("&lt;" + CANARY_TAG + "&gt;")) {
                        // Canary is properly encoded! Add INFO note
                        result.addMetadata("xssSanitizationVerified", true);
                    }

                } catch (Exception e) {
                    log.debug("XSS probe to {} failed: {}", probeUri, e.getMessage());
                }
            }

            result.addMetadata("parametersTestedForXSS", testParams.size());

        } catch (Exception e) {
            log.error("XSS scan failed for {}: {}", targetUri, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("XSS scanner error: " + e.getMessage());
        }

        return result;
    }

    private URI buildProbeUri(URI baseUri, String paramKey, String probeValue) {
        String base = baseUri.getScheme() + "://" + baseUri.getAuthority() + baseUri.getPath();
        String query = baseUri.getQuery();
        Map<String, String> params = new LinkedHashMap<>();
        if (query != null && !query.isEmpty()) {
            for (String pair : query.split("&")) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    params.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        }
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
