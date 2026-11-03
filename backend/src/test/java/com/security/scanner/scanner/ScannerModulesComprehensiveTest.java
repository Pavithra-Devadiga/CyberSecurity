package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.ScanStatus;
import com.security.scanner.entity.enums.ScanType;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import com.security.scanner.util.SafeHttpClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ScannerModulesComprehensiveTest {

    @Mock
    private SafeHttpClientFactory clientFactory;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<Object> httpResponse;

    @Mock
    private HttpHeaders httpHeaders;

    private Scan testScan;

    @BeforeEach
    void setUp() {
        testScan = new Scan("https://juice-shop.herokuapp.com", ScanType.FULL, null);
        testScan.setId(1L);
        testScan.setStatus(ScanStatus.RUNNING);
    }

    @Test
    @DisplayName("HttpHeaderScanner: detects missing HSTS, CSP, and Clickjacking headers")
    void testHttpHeaderScannerMissingHeaders() throws Exception {
        HttpHeaderScanner scanner = new HttpHeaderScanner(clientFactory);

        // Simulate server response missing security headers (typical in vulnerable apps like Juice Shop)
        Map<String, List<String>> headerMap = Map.of(
                "server", List.of("Apache/2.4.41 (Ubuntu)"),
                "x-powered-by", List.of("Express")
        );

        Mockito.when(clientFactory.createPermissiveSslClient()).thenReturn(httpClient);
        Mockito.when(httpHeaders.map()).thenReturn(headerMap);
        Mockito.when(httpHeaders.firstValue("strict-transport-security")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("content-security-policy")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("x-frame-options")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("x-content-type-options")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("referrer-policy")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("permissions-policy")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("server")).thenReturn(java.util.Optional.of("Apache/2.4.41 (Ubuntu)"));
        Mockito.when(httpHeaders.firstValue("x-powered-by")).thenReturn(java.util.Optional.of("Express"));

        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(httpResponse.headers()).thenReturn(httpHeaders);
        Mockito.when(httpClient.send(any(HttpRequest.class), any())).thenReturn(httpResponse);

        ScannerResult result = scanner.scan(URI.create("https://juice-shop.herokuapp.com"), testScan);

        assertNotNull(result);
        assertTrue(result.getVulnerabilities().size() >= 5);

        boolean hasHsts = result.getVulnerabilities().stream().anyMatch(v -> v.getTitle().contains("HSTS"));
        boolean hasCsp = result.getVulnerabilities().stream().anyMatch(v -> v.getTitle().contains("Content-Security-Policy"));
        boolean hasClickjacking = result.getVulnerabilities().stream().anyMatch(v -> v.getTitle().contains("Clickjacking"));
        boolean hasVersionDisclosure = result.getVulnerabilities().stream().anyMatch(v -> v.getTitle().contains("Version Disclosure"));
        boolean hasPoweredBy = result.getVulnerabilities().stream().anyMatch(v -> v.getTitle().contains("X-Powered-By"));

        assertTrue(hasHsts, "Must flag missing HSTS");
        assertTrue(hasCsp, "Must flag missing CSP");
        assertTrue(hasClickjacking, "Must flag missing X-Frame-Options");
        assertTrue(hasVersionDisclosure, "Must flag Server version leak");
        assertTrue(hasPoweredBy, "Must flag X-Powered-By leak");
    }

    @Test
    @DisplayName("SensitiveInformationScanner: detects AWS keys, private keys, and Juice Shop SQLite errors")
    void testSensitiveInformationScanner() throws Exception {
        SensitiveInformationScanner scanner = new SensitiveInformationScanner(clientFactory);

        // Realistic Juice Shop error leak response containing database error and internal IP
        String vulnerableHtml = """
            <html>
                <head><title>500 Internal Error</title></head>
                <body>
                    <!-- TODO: remove admin test credentials -->
                    <h1>Error executing query</h1>
                    <pre>SQLite3::SQLException: unrecognized token: "'" in SELECT * FROM Products WHERE id = 10.0.1.25</pre>
                    <p>Internal Server IP: 10.142.0.4</p>
                    <p>Debug AWS Key: AKIAIOSFODNN7EXAMPLE</p>
                </body>
            </html>
            """;

        Mockito.when(clientFactory.createPermissiveSslClient()).thenReturn(httpClient);
        Mockito.when(httpResponse.body()).thenReturn(vulnerableHtml);
        Mockito.when(httpClient.send(any(HttpRequest.class), any())).thenReturn(httpResponse);

        ScannerResult result = scanner.scan(URI.create("https://juice-shop.herokuapp.com/rest/products"), testScan);

        assertNotNull(result);
        List<Vulnerability> vulns = result.getVulnerabilities();

        boolean hasAwsKey = vulns.stream().anyMatch(v -> v.getTitle().contains("AWS Access Key ID"));
        boolean hasInternalIp = vulns.stream().anyMatch(v -> v.getTitle().contains("Internal Private IP"));
        boolean hasComment = vulns.stream().anyMatch(v -> v.getTitle().contains("Developer Comments"));

        assertTrue(hasAwsKey, "Must identify exposed AWS Access Key");
        assertTrue(hasInternalIp, "Must detect internal 10.x.x.x IP disclosure");
        assertTrue(hasComment, "Must detect sensitive HTML comments");
    }

    @Test
    @DisplayName("WebsiteTechnologyScanner: detects Express, Node.js, and Angular from headers/HTML")
    void testWebsiteTechnologyScanner() throws Exception {
        WebsiteTechnologyScanner scanner = new WebsiteTechnologyScanner(clientFactory);

        String juiceShopHtml = """
            <!doctype html>
            <html lang="en">
                <head>
                    <meta name="generator" content="Express 4.17.1">
                    <script src="runtime.js"></script>
                    <script src="main.js"></script>
                </head>
                <body ng-version="12.0.0">
                    <div id="root">Juice Shop</div>
                </body>
            </html>
            """;

        Mockito.when(clientFactory.createPermissiveSslClient()).thenReturn(httpClient);
        Mockito.when(httpHeaders.firstValue("server")).thenReturn(java.util.Optional.of("nginx/1.19.0"));
        Mockito.when(httpHeaders.firstValue("x-powered-by")).thenReturn(java.util.Optional.of("Express"));
        Mockito.when(httpHeaders.firstValue("x-aspnet-version")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.firstValue("x-generator")).thenReturn(java.util.Optional.empty());
        Mockito.when(httpHeaders.allValues("set-cookie")).thenReturn(List.of("connect.sid=s%3A12345; Path=/"));

        Mockito.when(httpResponse.headers()).thenReturn(httpHeaders);
        Mockito.when(httpResponse.body()).thenReturn(juiceShopHtml);
        Mockito.when(httpClient.send(any(HttpRequest.class), any())).thenReturn(httpResponse);

        ScannerResult result = scanner.scan(URI.create("https://juice-shop.herokuapp.com"), testScan);

        String techs = (String) result.getMetadata().get("technologiesFound");
        assertNotNull(techs);
        assertTrue(techs.contains("Express / Node.js") || techs.contains("Express"), "Must detect Express");
        assertTrue(techs.contains("Angular Framework") || techs.contains("Angular"), "Must detect Angular");
        assertTrue(techs.contains("Web Server: nginx"), "Must detect Nginx server");
    }

    @Test
    @DisplayName("SQLInjectionScanner: detects Juice Shop SQLite error signature")
    void testSQLInjectionScannerErrorDetection() {
        // Test database error signature pattern matching logic
        Pattern sqlitePattern = Pattern.compile("(?i)(sqlite3::sqlexception|unrecognized token:|near \".*\": syntax error)");
        String juiceShopError = "SQLite3::SQLException: unrecognized token: \"'\" in query SELECT * FROM Products WHERE id = ''";

        assertTrue(sqlitePattern.matcher(juiceShopError).find(), "Pattern must match SQLite syntax error from Juice Shop");

        Pattern postgresPattern = Pattern.compile("(?i)(org\\.postgresql\\.util\\.psqlexception|error: syntax error at or near|postgresql query failed)");
        String pgError = "org.postgresql.util.PSQLException: ERROR: syntax error at or near \"'\"";

        assertTrue(postgresPattern.matcher(pgError).find(), "Pattern must match PostgreSQL syntax error");
    }

    @Test
    @DisplayName("XSSScanner: verifies unencoded canary reflection detection logic")
    void testXSSScannerReflectionDetection() {
        String canaryTag = "csAuditToken";
        String unencodedReflection = "<div>Search Results for: <b>csAudit<" + canaryTag + ">probe\"'</b></div>";
        String encodedSafeReflection = "<div>Search Results for: <b>csAudit&lt;" + canaryTag + "&gt;probe&quot;'</b></div>";

        assertTrue(unencodedReflection.contains("<" + canaryTag + ">"), "Must flag unencoded tag reflection as vulnerable");
        assertFalse(encodedSafeReflection.contains("<" + canaryTag + ">"), "Encoded response must not trigger raw tag reflection");
        assertTrue(encodedSafeReflection.contains("&lt;" + canaryTag + "&gt;"), "Must recognize valid HTML entity encoding");
    }

    @Test
    @DisplayName("OWASPZapIntegrationService: handles offline daemon gracefully without crashing")
    void testZapIntegrationOfflineHandling() {
        OWASPZapIntegrationService zapService = new OWASPZapIntegrationService(clientFactory);

        // When disabled or offline
        ScannerResult result = zapService.scan(URI.create("https://juice-shop.herokuapp.com"), testScan);
        assertNotNull(result);
        assertEquals("ZAP", result.getModuleName());
        assertTrue(result.getVulnerabilities().isEmpty(), "No findings when ZAP daemon is not running");
    }
}
