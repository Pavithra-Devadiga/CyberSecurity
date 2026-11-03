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
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Component
public class WebsiteTechnologyScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(WebsiteTechnologyScanner.class);
    private final SafeHttpClientFactory clientFactory;

    public WebsiteTechnologyScanner(SafeHttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public String getModuleId() {
        return "TECH";
    }

    @Override
    public String getDisplayName() {
        return "Website Technology & Fingerprinting Scanner";
    }

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());
        HttpClient client = clientFactory.createPermissiveSslClient();

        Set<String> detectedTechnologies = new LinkedHashSet<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", SafeHttpClientFactory.DEFAULT_USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpHeaders headers = response.headers();
            String body = response.body() != null ? response.body() : "";

            // 1. Analyze Server & Custom Headers
            headers.firstValue("server").ifPresent(s -> {
                detectedTechnologies.add("Web Server: " + s);
            });
            headers.firstValue("x-powered-by").ifPresent(p -> {
                detectedTechnologies.add("Powered-By: " + p);
            });
            headers.firstValue("x-aspnet-version").ifPresent(v -> {
                detectedTechnologies.add("ASP.NET Version: " + v);
            });
            headers.firstValue("x-generator").ifPresent(g -> {
                detectedTechnologies.add("Generator: " + g);
            });

            // 2. Cookie Fingerprints
            List<String> cookies = headers.allValues("set-cookie");
            for (String cookie : cookies) {
                String lower = cookie.toLowerCase();
                if (lower.contains("jsessionid")) detectedTechnologies.add("Java Servlet / Spring / Tomcat");
                if (lower.contains("phpsessid")) detectedTechnologies.add("PHP");
                if (lower.contains("csrftoken") || lower.contains("django")) detectedTechnologies.add("Django (Python)");
                if (lower.contains("connect.sid")) detectedTechnologies.add("Express / Node.js");
                if (lower.contains("laravel_session") || lower.contains("xsrf-token")) detectedTechnologies.add("Laravel / PHP");
                if (lower.contains("wp-settings")) detectedTechnologies.add("WordPress CMS");
                if (lower.contains("asp.net_sessionid")) detectedTechnologies.add("ASP.NET / Microsoft IIS");
            }

            // 3. Jsoup HTML Analysis
            if (!body.isEmpty()) {
                Document doc = Jsoup.parse(body);

                // Meta generator tag
                Element metaGen = doc.selectFirst("meta[name=generator]");
                if (metaGen != null && metaGen.hasAttr("content")) {
                    String generator = metaGen.attr("content");
                    detectedTechnologies.add("CMS / Generator: " + generator);

                    result.addVulnerability(new Vulnerability(
                            scan,
                            "CMS / Generator Disclosed in Meta Tag (" + generator + ")",
                            "The HTML source includes a <meta name='generator'> tag disclosing the exact software and version generating the site.",
                            Severity.INFO,
                            VulnerabilityCategory.TECHNOLOGY_DISCLOSURE,
                            "CWE-200",
                            "Remove the generator meta tag from page templates to prevent reconnaissance.",
                            "<meta name=\"generator\" content=\"" + generator + "\">",
                            targetUri.toString()
                    ));
                }

                // Script tags / Framework detection
                Elements scripts = doc.select("script[src]");
                for (Element script : scripts) {
                    String src = script.attr("src").toLowerCase();
                    if (src.contains("react") || src.contains("/_next/")) detectedTechnologies.add("React / Next.js");
                    if (src.contains("vue") || src.contains("/_nuxt/")) detectedTechnologies.add("Vue.js / Nuxt.js");
                    if (src.contains("angular")) detectedTechnologies.add("Angular");
                    if (src.contains("jquery")) detectedTechnologies.add("jQuery Library");
                    if (src.contains("bootstrap")) detectedTechnologies.add("Bootstrap Framework");
                }

                // HTML structure indicators
                if (body.contains("__NEXT_DATA__")) detectedTechnologies.add("Next.js Framework");
                if (body.contains("__NUXT__")) detectedTechnologies.add("Nuxt.js Framework");
                if (body.contains("ng-version")) detectedTechnologies.add("Angular Framework");
                if (body.contains("data-reactroot")) detectedTechnologies.add("React UI");
            }

            String techSummary = String.join(", ", detectedTechnologies);
            result.addMetadata("technologiesFound", techSummary);
            result.addMetadata("technologyCount", detectedTechnologies.size());

            if (!detectedTechnologies.isEmpty()) {
                result.addVulnerability(new Vulnerability(
                        scan,
                        "Website Technology Stack Fingerprinted",
                        "The scanner identified " + detectedTechnologies.size() + " web technologies running on the target application: " + techSummary,
                        Severity.INFO,
                        VulnerabilityCategory.TECHNOLOGY_DISCLOSURE,
                        "CWE-200",
                        "Suppress banner headers and remove framework-specific tokens to minimize attack surface footprint.",
                        "Identified components: " + techSummary,
                        targetUri.toString()
                ));
            }

        } catch (Exception e) {
            log.error("Technology scanner failed for {}: {}", targetUri, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("Technology scanner error: " + e.getMessage());
        }

        return result;
    }
}
