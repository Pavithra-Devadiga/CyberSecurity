package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import com.security.scanner.entity.Vulnerability;
import com.security.scanner.entity.enums.Severity;
import com.security.scanner.entity.enums.VulnerabilityCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class SSLTLSScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(SSLTLSScanner.class);

    @Override
    public String getModuleId() {
        return "SSL";
    }

    @Override
    public String getDisplayName() {
        return "SSL/TLS & Certificate Analysis Scanner";
    }

    @Override
    public ScannerResult scan(URI targetUri, Scan scan) {
        ScannerResult result = new ScannerResult(getModuleId());

        String host = targetUri.getHost();
        int port = targetUri.getPort() != -1 ? targetUri.getPort() : (targetUri.getScheme().equalsIgnoreCase("https") ? 443 : 80);

        if (!targetUri.getScheme().equalsIgnoreCase("https") && port == 80) {
            result.addVulnerability(new Vulnerability(
                    scan,
                    "Insecure Plaintext HTTP Protocol Used",
                    "The target application is accessible via unencrypted plain HTTP (port 80). Transmitted data, including login credentials and session tokens, can be intercepted in transit.",
                    Severity.HIGH,
                    VulnerabilityCategory.SSL_TLS,
                    "CWE-319",
                    "Enforce HTTPS across the entire domain, obtain a valid TLS certificate, and redirect all HTTP traffic to HTTPS.",
                    "Scheme: http://" + host,
                    targetUri.toString()
            ));
            result.addMetadata("sslValid", false);
            return result;
        }

        try {
            // Establish an SSL handshake and inspect session
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());
            SSLSocketFactory factory = sc.getSocketFactory();

            try (SSLSocket socket = (SSLSocket) factory.createSocket()) {
                socket.connect(new InetSocketAddress(host, port), 6000);
                socket.setSoTimeout(6000);
                socket.startHandshake();

                SSLSession session = socket.getSession();
                String protocol = session.getProtocol();
                String cipherSuite = session.getCipherSuite();
                Certificate[] certs = session.getPeerCertificates();

                result.addMetadata("tlsVersion", protocol);
                result.addMetadata("cipherSuite", cipherSuite);
                result.addMetadata("sslValid", true);

                // 1. Check TLS Protocol Version
                if ("TLSv1".equalsIgnoreCase(protocol) || "TLSv1.1".equalsIgnoreCase(protocol) || "SSLv3".equalsIgnoreCase(protocol)) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Deprecated TLS Protocol Version Negotiated (" + protocol + ")",
                            "The server negotiated " + protocol + ", which has known cryptographic weaknesses (POODLE, BEAST) and has been deprecated by RFC 8996.",
                            Severity.HIGH,
                            VulnerabilityCategory.SSL_TLS,
                            "CWE-326",
                            "Disable SSLv3, TLS 1.0, and TLS 1.1 on the server. Enforce TLS 1.2 and TLS 1.3.",
                            "Negotiated protocol: " + protocol + ", Cipher: " + cipherSuite,
                            targetUri.toString()
                    ));
                }

                // 2. Check Weak Cipher Suites
                String lowerCipher = cipherSuite.toLowerCase();
                if (lowerCipher.contains("rc4") || lowerCipher.contains("des") || lowerCipher.contains("3des")
                        || lowerCipher.contains("md5") || lowerCipher.contains("export") || lowerCipher.contains("null")) {
                    result.addVulnerability(new Vulnerability(
                            scan,
                            "Weak Cipher Suite Supported (" + cipherSuite + ")",
                            "The server negotiated a cipher suite considered weak or broken, enabling theoretical or practical decryption of captured traffic.",
                            Severity.HIGH,
                            VulnerabilityCategory.SSL_TLS,
                            "CWE-327",
                            "Reconfigure the TLS cipher suite order to prioritize strong AEAD ciphers (e.g., AES-GCM, CHACHA20-POLY1305).",
                            "Active cipher suite: " + cipherSuite,
                            targetUri.toString()
                    ));
                }

                // 3. Inspect X509 Certificates
                if (certs != null && certs.length > 0 && certs[0] instanceof X509Certificate x509) {
                    Date notAfter = x509.getNotAfter();
                    Date notBefore = x509.getNotBefore();
                    String subjectDN = x509.getSubjectX500Principal().getName();
                    String issuerDN = x509.getIssuerX500Principal().getName();

                    result.addMetadata("subject", subjectDN);
                    result.addMetadata("issuer", issuerDN);
                    result.addMetadata("expiresAt", notAfter.toString());

                    // Check expiration
                    try {
                        x509.checkValidity();
                    } catch (CertificateExpiredException e) {
                        result.addVulnerability(new Vulnerability(
                                scan,
                                "SSL/TLS Certificate Expired",
                                "The TLS certificate for " + host + " has expired on " + notAfter + ", causing browser security warnings and broken trust chains.",
                                Severity.CRITICAL,
                                VulnerabilityCategory.SSL_TLS,
                                "CWE-295",
                                "Renew the SSL/TLS certificate immediately with a trusted Certificate Authority.",
                                "Expired on: " + notAfter,
                                targetUri.toString()
                        ));
                        result.addMetadata("sslValid", false);
                    } catch (CertificateNotYetValidException e) {
                        result.addVulnerability(new Vulnerability(
                                scan,
                                "SSL/TLS Certificate Not Yet Valid",
                                "The TLS certificate is not yet valid (valid from " + notBefore + ").",
                                Severity.HIGH,
                                VulnerabilityCategory.SSL_TLS,
                                "CWE-295",
                                "Verify server clock synchronization and ensure certificate issuance start date is valid.",
                                "Valid from: " + notBefore,
                                targetUri.toString()
                        ));
                    }

                    // Expiration warning (< 30 days remaining)
                    long daysUntilExpiry = ChronoUnit.DAYS.between(Instant.now(), notAfter.toInstant());
                    result.addMetadata("daysUntilExpiry", daysUntilExpiry);
                    if (daysUntilExpiry > 0 && daysUntilExpiry <= 30) {
                        result.addVulnerability(new Vulnerability(
                                scan,
                                "SSL/TLS Certificate Expiring Soon (" + daysUntilExpiry + " days remaining)",
                                "The TLS certificate will expire in " + daysUntilExpiry + " days. Unattended expiration will cause service interruption.",
                                Severity.LOW,
                                VulnerabilityCategory.SSL_TLS,
                                "CWE-295",
                                "Renew the certificate before " + notAfter + ".",
                                "Days remaining: " + daysUntilExpiry + ", Expiration date: " + notAfter,
                                targetUri.toString()
                        ));
                    }

                    // Check self-signed certificate
                    if (subjectDN.equals(issuerDN)) {
                        result.addVulnerability(new Vulnerability(
                                scan,
                                "Self-Signed SSL/TLS Certificate Detected",
                                "The certificate is self-signed and not issued by a recognized public Certificate Authority, leading to browser untrusted alerts.",
                                Severity.HIGH,
                                VulnerabilityCategory.SSL_TLS,
                                "CWE-295",
                                "Replace the self-signed certificate with one issued by a trusted Certificate Authority (such as Let's Encrypt).",
                                "Subject matches Issuer: " + subjectDN,
                                targetUri.toString()
                        ));
                    }

                    // Check RSA Key Size
                    if (x509.getPublicKey() instanceof RSAKey rsaKey) {
                        int keyLength = rsaKey.getModulus().bitLength();
                        result.addMetadata("rsaKeyLength", keyLength);
                        if (keyLength < 2048) {
                            result.addVulnerability(new Vulnerability(
                                    scan,
                                    "Insecure RSA Key Length (" + keyLength + " bits)",
                                    "The RSA public key size is " + keyLength + " bits, which is below the industry standard minimum of 2048 bits.",
                                    Severity.HIGH,
                                    VulnerabilityCategory.SSL_TLS,
                                    "CWE-326",
                                    "Reissue the certificate with an RSA key of at least 2048 bits or an ECDSA key (e.g. prime256v1).",
                                    "Key length: " + keyLength + " bits",
                                    targetUri.toString()
                            ));
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("SSL/TLS scan failed for {}:{}: {}", host, port, e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("SSL handshake failed: " + e.getMessage());
            result.addMetadata("sslValid", false);
        }

        return result;
    }
}
