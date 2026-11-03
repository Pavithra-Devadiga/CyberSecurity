package com.security.scanner.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Component
public class SafeHttpClientFactory {

    @Value("${scanner.connection.timeout-seconds:7}")
    private int connectTimeoutSeconds;

    public static final String DEFAULT_USER_AGENT = "CyberSecurityAuditPlatform/1.0 (Security Vulnerability Scanner)";

    /**
     * Builds a standard secure Java HttpClient with timeouts and normal redirect policy.
     */
    public HttpClient createStandardClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                .build();
    }

    /**
     * Builds an HttpClient that allows inspecting targets with self-signed or expired certs
     * specifically for SSL/TLS assessment without crashing on SSL handshake.
     */
    public HttpClient createPermissiveSslClient() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new SecureRandom());

            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .sslContext(sc)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                    .build();
        } catch (Exception e) {
            return createStandardClient();
        }
    }
}
