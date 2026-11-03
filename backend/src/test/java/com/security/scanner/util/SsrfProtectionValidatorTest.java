package com.security.scanner.util;

import com.security.scanner.exception.SsrfValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class SsrfProtectionValidatorTest {

    private SsrfProtectionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SsrfProtectionValidator();
        validator.setAllowPrivateIps(false);
    }

    @Test
    @DisplayName("Should block localhost hostname")
    void testBlockLocalhost() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("http://localhost:8080"));
    }

    @Test
    @DisplayName("Should block 127.0.0.1 loopback IP")
    void testBlockLoopbackIp() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("http://127.0.0.1"));
    }

    @Test
    @DisplayName("Should block cloud metadata service IP 169.254.169.254")
    void testBlockCloudMetadata() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("http://169.254.169.254/latest/meta-data/"));
    }

    @Test
    @DisplayName("Should block metadata.google.internal")
    void testBlockGoogleMetadata() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("http://metadata.google.internal"));
    }

    @Test
    @DisplayName("Should block non-http schemes (file, ftp, gopher)")
    void testBlockInvalidSchemes() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("file:///etc/passwd"));
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("ftp://ftp.example.com"));
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize("gopher://example.com"));
    }

    @Test
    @DisplayName("Should normalize URL without scheme to HTTPS")
    void testNormalizeUrlWithoutScheme() {
        URI uri = validator.validateAndNormalize("example.com");
        assertEquals("https", uri.getScheme());
        assertEquals("example.com", uri.getHost());
    }

    @Test
    @DisplayName("Should reject empty or null target URL")
    void testRejectEmptyUrl() {
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize(""));
        assertThrows(SsrfValidationException.class, () -> validator.validateAndNormalize(null));
    }
}
