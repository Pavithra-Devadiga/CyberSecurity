package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.util.SafeHttpClientFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SensitiveInformationScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(SensitiveInformationScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public SensitiveInformationScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "SENSITIVE";
    }

    @Override
    public String getDisplayName() {
        return "Sensitive Information & Secrets Leak Scanner";
    }

    private static final Pattern AWS_KEY_PATTERN = Pattern.compile("AKIA[0-9A-Z]{16}");
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("-----BEGIN (RSA|EC|DSA|OPENSSH)? ?PRIVATE KEY-----");
    private static final Pattern GENERIC_SECRET_PATTERN = Pattern.compile("(?i)(api[_-]?key|secret[_-]?key|access[_-]?token|auth[_-]?token)\\s*[:=]\\s*['\"]([a-zA-Z0-9_\\-]{16,})['\"]");
    private static final Pattern DB_URI_PATTERN = Pattern.compile("(jdbc:[a-zA-Z0-9]+://|mongodb(\\+srv)?://|postgres(ql)?://)[^\\s'\"<>]+");
    private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("(?i)(java\\.lang\\.[a-zA-Z]+Exception|Traceback \\(most recent call last\\)|org\\.springframework\\.[a-zA-Z.]+|at [a-zA-Z0-9_.$]+\\([a-zA-Z0-9_.]+\\.java:\\d+\\))");
    private static final Pattern INTERNAL_IP_PATTERN = Pattern.compile("\\b(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3})\\b");

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (body == null || body.isEmpty()) {
                return result;
            }

            // 1. AWS Access Key Pattern
            Matcher awsMatcher = AWS_KEY_PATTERN.matcher(body);
            if (awsMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Exposed AWS Access Key ID Detected",
                        "An AWS Access Key ID was detected in plaintext in the response body or client-side assets. Attackers can leverage this key to access cloud resources.",
                        Severity.CRITICAL,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-798",
                        "Rotate the exposed AWS key immediately in IAM, revoke all active sessions, and remove credentials from frontend client assets.",
                        "Matched AWS Key Pattern: " + awsMatcher.group(),
                        targetUri.toString()
                ));
            }

            // 2. Private Key block
            Matcher keyMatcher = PRIVATE_KEY_PATTERN.matcher(body);
            if (keyMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Exposed Private Encryption Key in Response",
                        "A private cryptographic key header (e.g. RSA/OPENSSH PRIVATE KEY) was found in the HTTP response body.",
                        Severity.CRITICAL,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-312",
                        "Revoke and re-generate the compromised cryptographic key immediately and restrict file permissions on the server.",
                        "Found key header: " + keyMatcher.group(),
                        targetUri.toString()
                ));
            }

            // 3. API Key / Secret assignments
            Matcher secretMatcher = GENERIC_SECRET_PATTERN.matcher(body);
            if (secretMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Exposed API Key or Secret Token in Source",
                        "An API key or secret token definition was found embedded directly in HTML or client script sources.",
                        Severity.HIGH,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-798",
                        "Move secrets and authentication tokens to backend environment variables; never embed secret keys in frontend code.",
                        "Matched secret: " + secretMatcher.group(1) + "=****",
                        targetUri.toString()
                ));
            }

            // 4. Database Connection Strings
            Matcher dbMatcher = DB_URI_PATTERN.matcher(body);
            if (dbMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Database Connection String Exposed",
                        "A plaintext database connection URI was discovered in the HTTP response.",
                        Severity.CRITICAL,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-200",
                        "Secure database connection strings in server-side configuration vaults or environment variables.",
                        "Exposed URI: " + dbMatcher.group().replaceAll(":[^/@]+@", ":****@"),
                        targetUri.toString()
                ));
            }

            // 5. Stack Trace / Exception Leaks
            Matcher stackMatcher = STACK_TRACE_PATTERN.matcher(body);
            if (stackMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Detailed Application Stack Trace Disclosed",
                        "The application disclosed a detailed debugging stack trace or exception output, revealing framework versions, class names, and code structures.",
                        Severity.MEDIUM,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-209",
                        "Configure a custom global error handler (e.g., @RestControllerAdvice or custom error pages) to display generic error messages without stack traces.",
                        "Disclosed stack trace fragment: " + stackMatcher.group(),
                        targetUri.toString()
                ));
            }

            // 6. Internal IP Address Leak
            Matcher ipMatcher = INTERNAL_IP_PATTERN.matcher(body);
            if (ipMatcher.find()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Internal Private IP Address Disclosure",
                        "A private RFC 1918 IP address was disclosed in the response content, aiding network reconnaissance.",
                        Severity.LOW,
                        VulnerabilityCategory.SENSITIVE_DATA,
                        "CWE-200",
                        "Remove internal server IPs and network routing details from public web responses and client comments.",
                        "Internal IP found: " + ipMatcher.group(),
                        targetUri.toString()
                ));
            }

            // 7. Parse HTML comments with Jsoup for sensitive notes
            try {
                Document doc = Jsoup.parse(body);
                findComments(doc, scan, targetUri, result);
            } catch (Exception ignored) {}

        } catch (Exception e) {
            log.error("Sensitive information scan failed for {}: {}", targetUri, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("Sensitive information scan error: " + e.getMessage());
        }

        return result;
    }

    private void findComments(Node node, Scan scan, URI targetUri, ScannerResult result) {
        for (Node child : node.childNodes()) {
            if (child instanceof Comment comment) {
                String data = comment.getData().toLowerCase();
                if (data.contains("password") || data.contains("secret") || data.contains("todo: remove") || data.contains("admin:")) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Sensitive Developer Comments in HTML Source",
                            "HTML comments containing sensitive keywords (password, secret, admin) were discovered in the page source.",
                            Severity.INFO,
                            VulnerabilityCategory.SENSITIVE_DATA,
                            "CWE-615",
                            "Strip developer comments and debugging annotations during production build steps.",
                            "Comment: <!-- " + comment.getData().trim() + " -->",
                            targetUri.toString()
                    ));
                    break;
                }
            }
            findComments(child, scan, targetUri, result);
        }
    }
}
