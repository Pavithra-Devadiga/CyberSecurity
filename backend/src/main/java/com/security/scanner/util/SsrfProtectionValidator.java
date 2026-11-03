package com.security.scanner.util;

import com.security.scanner.exception.SsrfValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class SsrfProtectionValidator {

    @Value("${scanner.allow-private-ips:false}")
    private boolean allowPrivateIps;

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final Set<Integer> ALLOWED_PORTS = Set.of(80, 443, 8000, 8080, 8081, 8443, 8888, 9000, 9090);

    private static final List<String> BLOCKED_HOSTS = List.of(
            "localhost",
            "127.0.0.1",
            "0.0.0.0",
            "::1",
            "metadata.google.internal",
            "169.254.169.254",
            "instance-data"
    );

    /**
     * Validates that the provided URL target is safe to scan and does not violate SSRF constraints.
     * @param targetUrl the target URL string
     * @return the normalized valid URI
     */
    public URI validateAndNormalize(String targetUrl) {
        if (targetUrl == null || targetUrl.trim().isEmpty()) {
            throw new SsrfValidationException("Target URL cannot be empty");
        }

        String trimmed = targetUrl.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://" + trimmed;
        }

        URI uri;
        try {
            uri = URI.create(trimmed).normalize();
        } catch (Exception e) {
            throw new SsrfValidationException("Malformed URL: " + targetUrl);
        }

        String scheme = uri.getScheme();
        if (scheme == null || !ALLOWED_SCHEMES.contains(scheme.toLowerCase())) {
            throw new SsrfValidationException("Invalid scheme: " + scheme + ". Only HTTP and HTTPS are permitted.");
        }

        String host = uri.getHost();
        if (host == null || host.trim().isEmpty()) {
            throw new SsrfValidationException("Target URL host cannot be resolved or empty");
        }

        String lowerHost = host.toLowerCase();

        // Check against blocked hostnames
        for (String blocked : BLOCKED_HOSTS) {
            if (lowerHost.equals(blocked) || lowerHost.endsWith("." + blocked)) {
                if (!allowPrivateIps) {
                    throw new SsrfValidationException("Access to host '" + host + "' is blocked by SSRF protection policies.");
                }
            }
        }

        // Validate port
        int port = uri.getPort();
        if (port != -1 && !ALLOWED_PORTS.contains(port)) {
            throw new SsrfValidationException("Port " + port + " is not an allowed standard web port for scanning.");
        }

        // Resolve DNS and inspect all IP addresses
        if (!allowPrivateIps) {
            validateResolvedIps(lowerHost);
        }

        return uri;
    }

    private void validateResolvedIps(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                if (addr.isLoopbackAddress()) {
                    throw new SsrfValidationException("Target resolves to loopback IP (" + addr.getHostAddress() + ") which is blocked.");
                }
                if (addr.isSiteLocalAddress()) {
                    throw new SsrfValidationException("Target resolves to RFC-1918 private network IP (" + addr.getHostAddress() + ") which is blocked.");
                }
                if (addr.isLinkLocalAddress()) {
                    throw new SsrfValidationException("Target resolves to link-local IP (" + addr.getHostAddress() + ") which is blocked.");
                }
                if (addr.isAnyLocalAddress()) {
                    throw new SsrfValidationException("Target resolves to wildcard address (" + addr.getHostAddress() + ") which is blocked.");
                }
                if (addr.isMulticastAddress()) {
                    throw new SsrfValidationException("Target resolves to multicast address (" + addr.getHostAddress() + ") which is blocked.");
                }

                // Check cloud metadata address: 169.254.169.254
                byte[] bytes = addr.getAddress();
                if (bytes.length == 4) {
                    int b0 = bytes[0] & 0xFF;
                    int b1 = bytes[1] & 0xFF;
                    if (b0 == 169 && b1 == 254) {
                        throw new SsrfValidationException("Target resolves to cloud metadata/link-local address range: " + addr.getHostAddress());
                    }
                    if (b0 == 127) {
                        throw new SsrfValidationException("Target resolves to 127.0.0.0/8 loopback address range: " + addr.getHostAddress());
                    }
                    if (b0 == 10) {
                        throw new SsrfValidationException("Target resolves to 10.0.0.0/8 private network: " + addr.getHostAddress());
                    }
                    if (b0 == 172 && (b1 >= 16 && b1 <= 31)) {
                        throw new SsrfValidationException("Target resolves to 172.16.0.0/12 private network: " + addr.getHostAddress());
                    }
                    if (b0 == 192 && b1 == 168) {
                        throw new SsrfValidationException("Target resolves to 192.168.0.0/16 private network: " + addr.getHostAddress());
                    }
                }
            }
        } catch (UnknownHostException e) {
            throw new SsrfValidationException("Unable to resolve DNS for host: " + host);
        }
    }

    public void setAllowPrivateIps(boolean allowPrivateIps) {
        this.allowPrivateIps = allowPrivateIps;
    }
}
