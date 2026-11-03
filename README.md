# CyberShield - Enterprise Vulnerability Scanner & Security Audit Platform

A full-stack, enterprise-grade web application vulnerability scanner and security assessment platform built with **Java 21**, **Spring Boot 3.4**, **PostgreSQL**, and **React (Vite + Tailwind CSS)**.

> **Zero Python Policy**: This project is built entirely in Java 21 and Spring Boot 3 on the backend and modern React on the frontend. No Python, Flask, Django, or FastAPI dependencies exist anywhere in the codebase.

---

## Architecture Overview

```
cybersecurity/
├── backend/                               # Spring Boot 3.4.1 (Java 21) REST Backend
│   ├── src/main/java/com/security/scanner/
│   │   ├── config/                        # SecurityConfig, JwtAuthenticationFilter, DataInitializer
│   │   ├── controller/                    # Auth, Dashboard, Scan, Vulnerability, Report, Notification
│   │   ├── dto/                           # Request and Response Data Transfer Objects
│   │   ├── entity/                        # JPA Entities: User, Scan, Vulnerability, Notification
│   │   ├── exception/                     # @RestControllerAdvice, custom security exceptions
│   │   ├── repository/                    # Spring Data JPA Repositories
│   │   ├── scanner/                       # 8 Modular Vulnerability & Exposure Scanners
│   │   │   ├── HttpHeaderScanner.java
│   │   │   ├── SSLTLSScanner.java
│   │   │   ├── EndpointExposureScanner.java
│   │   │   ├── SensitiveInformationScanner.java
│   │   │   ├── WebsiteTechnologyScanner.java
│   │   │   ├── SQLInjectionScanner.java
│   │   │   ├── XSSScanner.java
│   │   │   └── OWASPZapIntegrationService.java
│   │   ├── service/                       # Business logic, Orchestrator, Scoring, OpenPDF Reports
│   │   └── util/                          # SsrfProtectionValidator, SafeHttpClientFactory
│   ├── src/test/java/                     # JUnit 5 & Mockito test suites (15 tests, 100% passing)
│   ├── pom.xml                            # Maven dependencies & build lifecycle
│   └── Dockerfile                         # Multi-stage JDK 21 build & unprivileged Alpine runtime
│
├── frontend/                              # Modern React 18 + Vite + Tailwind CSS SPA
│   ├── src/
│   │   ├── api/                           # Axios client with JWT interceptor & auto-logout
│   │   ├── components/common/             # Navbar, Sidebar, ScoreGauge, SeverityBadge, StatusBadge
│   │   ├── context/                       # AuthContext with token & user session management
│   │   └── pages/                         # Dashboard, NewScan, Scans, ScanDetail, Vulnerabilities, Reports, Notifications, Login, Register
│   ├── package.json                       # Dependencies: React 18, Vite 5, Tailwind 3, Recharts, Lucide React
│   ├── nginx.conf                         # Reverse proxy configuration routing /api to backend
│   └── Dockerfile                         # Multi-stage Node build & Nginx serving
│
└── docker-compose.yml                     # Full orchestration: PostgreSQL 16 + Backend + Frontend + ZAP
```

---

## Mandatory Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Backend Runtime** | Java 21 (Temurin LTS) | Modern LTS Java runtime with virtual threads support |
| **Backend Framework** | Spring Boot 3.4.1 | Spring Web, Spring Security, Spring Data JPA, Spring Validation |
| **Database** | PostgreSQL 16 / H2 | PostgreSQL for production/docker; embedded H2 for instant local development |
| **Authentication** | Spring Security + JWT | Stateless JWT authentication, BCrypt password hashing |
| **HTTP Client** | Java 11+ `java.net.http.HttpClient` | Native Java HTTP client with connect/read timeouts |
| **HTML Parsing** | Jsoup 1.17.2 | DOM and reflection analysis |
| **SSL/TLS APIs** | Java `javax.net.ssl` | Low-level SSLSocket & X509Certificate TLS inspection |
| **PDF Generation** | OpenPDF 1.3.39 | Professional executive security audit reports |
| **Testing** | JUnit 5 & Mockito | Unit tests for SSRF, scoring, JWT, and MockMvc integration tests |
| **Frontend Runtime** | React 18 + Vite | Lightning-fast build and reactive frontend SPA |
| **Styling** | Tailwind CSS | Sleek dark-mode security operations center theme |
| **Charts & Visuals** | Recharts & Lucide React | Posture gauge, severity bar charts, category breakdown pie charts |
| **Containerization** | Docker & Docker Compose | Multi-container automated deployment |

---

## Scanning Engine Modules

Each scanner operates as an independent, modular component implementing `ScannerModule`:

1. **`HttpHeaderScanner`**:
   - Inspects `Strict-Transport-Security` (HSTS), `Content-Security-Policy` (CSP), `X-Frame-Options` (Clickjacking), `X-Content-Type-Options: nosniff`, `Referrer-Policy`, and `Permissions-Policy`.
   - Identifies information disclosures in `Server` and `X-Powered-By` headers.
   - Maps findings to CWE-693, CWE-1021, and CWE-200.

2. **`SSLTLSScanner`**:
   - Analyzes negotiated TLS protocols (flags deprecated TLSv1.0, TLSv1.1, SSLv3).
   - Identifies weak cipher suites (RC4, 3DES, DES, EXPORT, NULL).
   - Inspects X.509 certificate expiration dates, validity windows, issuer trust chains, self-signed status, and RSA key size (< 2048 bits).

3. **`EndpointExposureScanner`**:
   - Safely probes for exposed configuration files (`/.env`, `/.git/HEAD`, `/.git/config`, `/.DS_Store`).
   - Detects public monitoring endpoints (`/actuator/env`, `/actuator/health`).
   - Identifies public API documentation (`/swagger-ui/index.html`, `/v3/api-docs`) and parses `robots.txt` disallowed paths.

4. **`SensitiveInformationScanner`**:
   - Inspects response bodies and HTML comments for leaked AWS access keys (`AKIA[0-9A-Z]{16}`), RSA/SSH private key headers, generic API tokens, database connection strings, stack traces, and internal RFC 1918 IP addresses.

5. **`WebsiteTechnologyScanner`**:
   - Fingerprints web servers (Nginx, Apache, IIS, Envoy), backend runtimes (Spring, PHP, Node.js/Express, ASP.NET), CMS platforms (WordPress), and frontend frameworks (React, Next.js, Vue, Angular).

6. **`SQLInjectionScanner`**:
   - Safe heuristic assessment checking parameter reflection and database syntax error signatures (MySQL, PostgreSQL, Oracle, SQL Server, SQLite) using benign syntax canary probes without destructive payloads.

7. **`XSSScanner`**:
   - Assesses Reflected Cross-Site Scripting by submitting harmless canary tags (`csAudit<csAuditToken>probe"'`) into input contexts and verifying whether HTML meta-characters are encoded (`&lt;` vs unencoded `<`).
   - Analyzes client JavaScript for dangerous DOM sinks (`document.write(location.search)`, `element.innerHTML = location.hash`).

8. **`OWASPZapIntegrationService`**:
   - Connects to an external OWASP ZAP daemon via its native REST API (`/JSON/spider/`, `/JSON/core/view/alerts/`).
   - Translates ZAP alerts into unified `Vulnerability` models with CWE numbers and remediation advisories.

---

## SSRF Protection & Security Rules

To prevent Server-Side Request Forgery (SSRF) and scanning abuse, the `SsrfProtectionValidator` enforces strict pre-flight validation on all submitted target URLs:

- **Protocol Restrictions**: Only `http://` and `https://` schemes are permitted. All others (`file://`, `ftp://`, `gopher://`, `ldap://`, etc.) are rejected immediately.
- **Loopback & Private Network Blocking**: DNS resolution inspects all resolved IP addresses:
  - `127.0.0.0/8` and `::1` (Loopback)
  - `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16` (RFC-1918 Private Subnets)
  - `169.254.0.0/16` (Link-Local & Cloud Metadata endpoint `169.254.169.254`)
  - Wildcard / Any local addresses (`0.0.0.0`)
- **Port Whitelisting**: Restricted to standard web ports (80, 443, 8080, 8443, etc.).
- **Safe Timeouts**: Connection timeouts (7s) and read timeouts (10s) prevent slowloris or denial-of-service against target servers.

---

## Quickstart Guide

### Option 1: Running with Docker Compose (Recommended for Production)

```bash
# Build and start PostgreSQL, Spring Boot Backend, and React Frontend:
docker-compose up -d --build

# To include OWASP ZAP in the compose stack:
docker-compose --profile with-zap up -d --build
```

- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL**: localhost:5432

---

### Option 2: Running Locally (Development Mode)

#### 1. Start the Backend
The backend includes an automatic development profile using an in-memory database, so you do not need PostgreSQL installed locally to test it:

```bash
cd backend
..\mvn.bat spring-boot:run "-Dspring-boot.run.profiles=dev"
```
The backend starts at `http://localhost:8080`.

#### 2. Start the Frontend
```bash
cd frontend
npm run dev
```
Open your browser to `http://localhost:5173`.

---

## Pre-Seeded Demo Accounts

The application automatically seeds demonstration accounts on first run:

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` |
| **Security Analyst** | `demo_analyst` | `password123` |

Clicking **"Admin"** or **"Analyst"** on the login page automatically fills in the credentials for instant access.

---

## Running Automated Tests

Run the complete JUnit 5 and Mockito test suite:

```bash
cd backend
..\mvn.bat test "-Dspring.profiles.active=dev"
```

All 15 tests pass with 0 errors and 0 failures:
- `SsrfProtectionValidatorTest`: Validates blocking of localhost, 127.0.0.1, 169.254.169.254, private RFC-1918 IPs, and malformed schemes.
- `SecurityScoreServiceTest`: Validates 100/A+ clean scans, critical vulnerability deductions, grade caps, and score clamping.
- `JwtServiceTest`: Validates token signing, claim extraction, and expiration validation.
- `AuthControllerTest`: MockMvc integration tests for user registration and authentication.

---

## REST API Specification

### Authentication
- `POST /api/auth/register`: Create user account, returns JWT.
- `POST /api/auth/login`: Authenticate with username and password, returns JWT.
- `GET /api/auth/me`: Retrieve current user profile.

### Dashboard
- `GET /api/dashboard/metrics`: High-level metrics, severity counts, category counts, and recent audits.

### Scans
- `POST /api/scans`: Launch new scan (validates SSRF, starts asynchronous scanner orchestrator).
- `GET /api/scans`: Paginated list of scans for current user.
- `GET /api/scans/{id}`: Detailed scan metrics, diagnostics, and findings.
- `POST /api/scans/{id}/cancel`: Abort an active or queued scan.

### Vulnerabilities
- `GET /api/vulnerabilities`: Filter findings by `severity`, `category`, `search`, with pagination.
- `GET /api/vulnerabilities/{id}`: Single vulnerability details with evidence and remediation.
- `GET /api/vulnerabilities/scan/{scanId}`: All vulnerabilities belonging to a scan.

### Reports
- `POST /api/reports/generate`: Generates report metadata summary.
- `GET /api/reports/download/{scanId}`: Generates and downloads the professional PDF audit report built with OpenPDF.

### Notifications
- `GET /api/notifications`: Paginated alerts.
- `GET /api/notifications/unread-count`: Unread notifications count badge.
- `PATCH /api/notifications/{id}/read`: Mark notification as read.
- `POST /api/notifications/mark-all-read`: Mark all notifications as read.
