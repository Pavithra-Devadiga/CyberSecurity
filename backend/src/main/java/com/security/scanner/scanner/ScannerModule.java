package com.security.scanner.scanner;

import com.security.scanner.entity.Scan;
import java.net.URI;

public interface ScannerModule {

    /**
     * Unique identifier for this scanner module.
     */
    String getModuleId();

    /**
     * Human-readable display name.
     */
    String getDisplayName();

    /**
     * Executes the security scan against the target URI for the given Scan entity.
     * @param targetUri validated safe target URI
     * @param scan the current scan entity
     * @return ScannerResult containing detected vulnerabilities and scan metadata
     */
    ScannerResult scan(URI targetUri, Scan scan);
}
