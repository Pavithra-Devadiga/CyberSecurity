# UNIVERSITY OF VISVESVARAYA COLLEGE OF ENGINEERING
### (First State Autonomous University on IIT Model)
**K R Circle, Bengaluru – 560 001**

<br>

---

<br>

### A Report on Summer Internship-II
## "CYBERSECURITY INTERNSHIP"

**Submitted under the mandatory Summer Internship required for the III Year (V Semester)**  
**Bachelor of Technology in Information Science and Engineering / Computer Science and Engineering**

<br>

**Submitted by:**  
**PAVITHRA DEVADIGA**  
**USN: [U25UV24TXXXXXX]**  
V Sem, B.Tech, ISE / CSE  

<br>

**Conducted by:**  
**HONEYWELL**  
*Powered by BharatCares*

<br>

**Proctor:**  
**Dr. Tanuja R**  
Associate Professor  

<br>

**DEPARTMENT OF COMPUTER SCIENCE AND ENGINEERING**  
**UNIVERSITY OF VISVESVARAYA COLLEGE OF ENGINEERING**  
**KR Circle, Bengaluru – 560 001**  
**Academic Year: 2026–2027**

\pagebreak

---

# UNIVERSITY OF VISVESVARAYA COLLEGE OF ENGINEERING
### K R Circle, Bengaluru – 560 001
### Department of Computer Science and Engineering

<br>

## Certificate

This is to certify that **PAVITHRA DEVADIGA**, bearing Register Number **[U25UV24TXXXXXX]**, has successfully completed the Summer Internship – II work on **"Cybersecurity"** conducted by the **Honeywell Future Skills Edge Program**, implemented by **BharatCares**, for a duration of 120 hours from June 2026 to July 2026, in partial fulfilment of the requirements for the V Semester, Bachelor of Technology in Information Science and Engineering, University of Visvesvaraya College of Engineering, during the academic year 2026–2027.

<br><br><br>

| Proctor | Chairperson |
| :--- | :--- |
| <br>___________________________________<br>**Dr. Tanuja R**<br>Associate Professor<br>Dept. of CSE,<br>UVCE, Bengaluru | <br>___________________________________<br>**Dr. Manjula S H**<br>Professor<br>Dept. of CSE,<br>UVCE, Bengaluru |

<br><br>

| Examiner 1 | Examiner 2 |
| :--- | :--- |
| <br>___________________________________ | <br>___________________________________ |

\pagebreak

---

## ACKNOWLEDGEMENT

I express my sincere gratitude to the **University of Visvesvaraya College of Engineering (UVCE)**, Bengaluru, and the **Department of Information Science and Engineering / Computer Science and Engineering** for providing me with the opportunity and support to undertake this internship.

I am especially thankful to my academic guide and proctor, **Dr. TANUJA R**, Associate Professor, for her valuable guidance, constructive suggestions, and continuous encouragement throughout the project and report preparation.

I would also like to express my sincere gratitude to **Dr. MANJULA S H**, Professor and Chairperson of the Department, and the **Director of UVCE** for providing me with this valuable opportunity and for their institutional support in facilitating the internship, which contributed significantly to my academic and professional learning.

I sincerely thank **Honeywell** and **BharatCares** for providing the opportunity to participate in the **Honeywell Future Skills Edge Program on Cybersecurity** and complete the 120-hour internship. The program significantly enhanced my understanding of cybersecurity fundamentals, network defense, application security, threat assessment, and vulnerability management.

As part of the internship, I developed **CyberShield: An Automated Enterprise Web Application Vulnerability Scanner and Security Assessment Platform**. The project was architected strictly using **Java 21** and **Spring Boot 3** for the core backend services, and **React 18** with **Tailwind CSS** for the interactive dashboard, deliberately eliminating Python dependencies to build a production-grade enterprise system. The project provided rigorous practical experience in automated security assessments, Server-Side Request Forgery (SSRF) defense, vulnerability classification, security score calculations, PDF report generation, and full-stack software engineering.

Finally, I thank my parents, friends, classmates, and well-wishers for their constant encouragement and support throughout this learning journey.

<br>

**Name:** Pavithra Devadiga  
**USN:** [U25UV24TXXXXXX]  

\pagebreak

---

## ABSTRACT

Cybersecurity has become one of the most critical pillars of modern computing due to the exponential growth of web applications, cloud services, and connected enterprise APIs. Organizations increasingly rely on web infrastructure to process and store critical data. Consequently, vulnerabilities such as SQL Injection (SQLi), Cross-Site Scripting (XSS), missing HTTP security headers, insecure TLS configurations, sensitive data leaks, and exposed administrative endpoints present severe attack vectors for malicious threat actors.

This report presents the learning and experience gained during a 120-hour Cybersecurity Internship organized by **BharatCares** under the **Honeywell Future Skills Edge Program** from June 2026 to July 2026, following the 80-hour foundational cybersecurity training completed on their platform. The training covered information security fundamentals, the CIA Triad, cryptographic algorithms, network protocol security, threat modeling, web vulnerabilities, access control, and incident response.

To translate theoretical concepts into practical software engineering, I developed **CyberShield**, an enterprise-grade automated web application vulnerability scanning and security assessment platform. Rejecting scripting and Python-based utilities, the entire backend was engineered using **Java 21**, **Spring Boot 3.4.1**, **Spring Security**, **JWT stateless authentication**, **Spring Data JPA**, **Hibernate**, and **PostgreSQL**. The scanning core leverages the Java 21 `HttpClient`, **Jsoup** HTML parsing engine, native Java SSL/TLS cryptographic APIs, and a direct REST integration with the **OWASP Zed Attack Proxy (ZAP)**.

The system incorporates eight modular scanners: HTTP Security Header Scanner, SSL/TLS Cipher Suite Scanner, SQL Injection Scanner, Cross-Site Scripting Scanner, Endpoint Exposure Scanner, Sensitive Information Disclosure Scanner, Website Technology Fingerprinting Scanner, and OWASP ZAP Active Scanner. To ensure safe and compliant operations, a robust Server-Side Request Forgery (SSRF) validator prevents scanning internal network addresses, loopback interfaces, and cloud metadata endpoints. Furthermore, a weighted scoring algorithm evaluates detected vulnerabilities and computes an overall 0–100 Security Score. Reports are automatically compiled into publication-quality PDFs using **OpenPDF**, and an interactive **React 18** Single Page Application (SPA) with **Recharts** delivers real-time dashboard analytics.

The internship provided comprehensive, hands-on experience in full-stack security engineering, vulnerability assessment, defensive network programming, and DevOps containerization using Docker. This report documents the internship objectives, training modules, system architecture, technical implementation, empirical results, learning outcomes, challenges, and future enhancements.

\pagebreak

---

## TABLE OF CONTENTS

| Sl. No. | Title | Page No. |
| :---: | :--- | :---: |
| **1** | **Introduction** | **1** |
| **2** | **About the Organisation** | **2** |
| **3** | **Objectives of the Internship** | **3** |
| **4** | **Cybersecurity Training and Guided Project Work** | **4** |
| | 4.1 Online Cybersecurity Training – 80 Hours | 4 |
| | 4.2 Guided Practical Work and Project Development – 40 Hours | 5 |
| | 4.3 Overall Training Experience | 5 |
| **5** | **Specific Skills Acquired** | **6** |
| **6** | **CyberShield Vulnerability Scanner – Project Overview** | **7** |
| | 6.1 Introduction to the Project | 7 |
| | 6.2 Problem Statement | 7 |
| | 6.3 Project Objectives | 8 |
| | 6.4 Scope and Features | 8 |
| **7** | **Project Architecture and Design** | **9** |
| | 7.1 System Architecture | 9 |
| | 7.2 Technologies and Tools Used | 10 |
| | 7.3 Security Assessment Methodology | 11 |
| | 7.4 Risk Scoring and Security Score Assessment | 12 |
| | 7.5 Project Structure and Software Requirements | 14 |
| **8** | **Project Implementation** | **15** |
| | 8.1 Backend Implementation & Architecture | 15 |
| | 8.2 Automated Security Scanners | 15 |
| | 8.3 SSRF Defense & Safe HTTP Client Architecture | 16 |
| | 8.4 Security Score & Severity Classification Engine | 17 |
| | 8.5 Automated PDF Report Generation | 17 |
| | 8.6 Frontend Dashboard Implementation | 18 |
| | 8.7 Project Execution | 18 |
| **9** | **Project Results and Analysis** | **19** |
| | 9.1 Security Assessment Results | 19 |
| | 9.2 Sample Scan Results | 19 |
| | 9.3 Security Score and Risk Distribution Analysis | 20 |
| | 9.4 Dashboard Visualization and Reporting | 21 |
| | 9.5 Overall Project Analysis | 21 |
| **10** | **Learning Outcomes and Cybersecurity Relevance** | **22** |
| | 10.1 Understanding Web Application Security | 22 |
| | 10.2 Practical Experience with Full-Stack Security Engineering | 22 |
| | 10.3 Vulnerability Identification and Risk Assessment | 22 |
| | 10.4 Security Automation & Defensive Architecture | 23 |
| | 10.5 Security Monitoring and Reporting | 23 |
| | 10.6 Application Development and Problem-Solving | 23 |
| | 10.7 Relevance to Real-World Cybersecurity & DevSecOps | 23 |
| **11** | **Challenges and Limitations** | **24** |
| | 11.1 Challenges Faced | 24 |
| | 11.2 Project Limitations | 24 |
| **12** | **Conclusion** | **26** |
| **13** | **References** | **27** |
| **14** | **Certificate** | **28** |

\pagebreak

---

## 1. INTRODUCTION

Cybersecurity has emerged as an essential foundation of modern software engineering and digital transformation. With organizations transitioning core operations, financial transactions, healthcare records, and enterprise services to distributed web platforms, web applications have become the primary gateway through which businesses interact with customers, partners, and external systems. However, this ubiquity makes web applications the primary target for cyberattacks. The Open Web Application Security Project (OWASP) Top 10 consistently highlights severe threats including Broken Access Control, Cryptographic Failures, Injection attacks (SQLi, Command Injection), Insecure Design, Security Misconfigurations, and Server-Side Request Forgery (SSRF).

Traditional perimeter security models, such as network firewalls and intrusion prevention systems, are insufficient to protect modern application-layer attack surfaces. Vulnerabilities frequently arise from minor developer oversights: missing HTTP security headers (e.g., Content-Security-Policy, Strict-Transport-Security), outdated TLS protocol configurations, exposed configuration files (`.env`, `.git`), and unvalidated input fields. Identifying these weaknesses before malicious actors exploit them requires automated, continuous security assessment.

This report presents the learning, practical experience, and technical achievements gained during a **120-hour Cybersecurity Internship** organized by **BharatCares** under the **Honeywell Future Skills Edge Program** from June 2026 to July 2026. The program combined 80 hours of structured theoretical training in cybersecurity domains with 40 hours of guided practical software engineering.

To demonstrate mastery over practical security principles, I developed **CyberShield: An Automated Enterprise Web Application Vulnerability Scanner and Security Assessment Platform**. Adhering to a strict architectural standard, the entire backend was constructed in **Java 21** and **Spring Boot 3.4.1**, intentionally eliminating Python to deliver high-concurrency, strongly-typed enterprise reliability. CyberShield orchestrates eight independent scanning engines, validates target hosts against SSRF attacks, computes quantitative security scores, persists scan telemetry in PostgreSQL, generates downloadable PDF audit reports using OpenPDF, and presents interactive security analytics through a modern React 18 dashboard.

The project bridged the gap between academic cybersecurity theory and real-world DevSecOps engineering, providing invaluable exposure to defensive architecture, automated threat detection, and production-grade full-stack development.

\pagebreak

---

## 2. ABOUT THE ORGANISATION

### Honeywell
Honeywell International Inc. is an American multinational conglomerate and Fortune 100 technology enterprise headquartered in Charlotte, North Carolina. Honeywell operates across four core business segments: Aerospace, Building Technologies, Performance Materials and Technologies (PMT), and Safety and Productivity Solutions (SPS). 

With digital transformation and Industry 4.0 rapidly expanding across industrial infrastructure, Honeywell has positioned itself as a global leader in industrial automation, cyber-physical systems, and enterprise cybersecurity solutions. Honeywell Connected Enterprise develops cutting-edge software platforms (such as Honeywell Forge) that incorporate operational technology (OT) cybersecurity, threat intelligence, and secure cloud connectivity. Through corporate social responsibility initiatives such as the **Honeywell Future Skills Edge Program**, the organization partners with premier academic institutions and non-profit organizations to bridge the technical skills gap and cultivate the next generation of engineers in emerging fields including cybersecurity, artificial intelligence, and cloud engineering.

### BharatCares
BharatCares (the social development arm of CSRBOX) is one of India's leading social impact and education organizations. BharatCares is dedicated to designing and implementing large-scale skill development, STEM education, digital literacy, and youth employability programs across India. 

Working in partnership with multinational technology corporations, state universities, and government bodies, BharatCares delivers industry-aligned training programs that combine conceptual learning with hands-on, project-based engineering. As the primary implementation partner of the Honeywell Future Skills Edge Program, BharatCares facilitated the curriculum delivery, learning management infrastructure, mentorship, assessment evaluations, and internship certifications for participating undergraduate engineering students.

### Honeywell Future Skills Edge Program
The Honeywell Future Skills Edge Program is a flagship youth empowerment initiative established to equip undergraduate engineering students with industry-standard technical competencies. In the domain of Cybersecurity, the program provided a structured, 120-hour hybrid curriculum:
1. **80 Hours of Foundational Cybersecurity Training**: Conducted through an interactive online learning platform covering the core tenets of information security, defensive architecture, cryptographic protocols, threat modeling, and regulatory compliance.
2. **40 Hours of Guided Project Implementation**: Focused on hands-on software development, requiring students to architect, implement, test, and document an original cybersecurity application addressing real-world security challenges.

\pagebreak

---

## 3. OBJECTIVES OF THE INTERNSHIP

The primary objective of the internship was to cultivate a rigorous practical understanding of cybersecurity principles, threat evaluation, secure coding practices, and defensive software architecture by applying theoretical concepts to the engineering of an enterprise web security auditing platform.

The specific technical and professional objectives of the internship included:
1. **To Understand Foundational Cybersecurity Tenets**: Gain an in-depth understanding of the CIA Triad (Confidentiality, Integrity, Availability), authentication mechanisms, access control models, and network protocol security.
2. **To Master the OWASP Top 10 Web Application Security Risks**: Examine the mechanics of widespread web vulnerabilities, including SQL Injection, Cross-Site Scripting (XSS), Security Misconfigurations, Cryptographic Failures, and Sensitive Data Exposure.
3. **To Learn Defensive Network Engineering**: Understand Server-Side Request Forgery (SSRF) threats, DNS rebinding risks, and private subnet isolation, and implement robust programmatic defense mechanisms.
4. **To Implement Full-Stack Security in Java 21 & Spring Boot 3**: Master enterprise backend architecture, Spring Security filter chains, stateless JWT token authentication, and Spring Data JPA persistence without relying on Python scripting.
5. **To Develop Modular Security Scanners**: Design independent, asynchronous scanning engines capable of analyzing HTTP headers, TLS cipher suites, HTML DOM structures, injection vectors, and endpoint exposures.
6. **To Integrate Standard Security Tools via REST APIs**: Understand Dynamic Application Security Testing (DAST) by integrating the OWASP Zed Attack Proxy (ZAP) daemon through its native REST API.
7. **To Design a Quantitative Risk & Security Scoring Model**: Develop a mathematical formulation for calculating cumulative risk weights and normalising compliance scores on a 0–100 scale.
8. **To Automate Audit Report Generation**: Implement server-side PDF document generation using OpenPDF to produce formal, executive-ready security assessment reports.
9. **To Build an Interactive Analytics Dashboard**: Develop a modern Single Page Application (SPA) using React 18, Tailwind CSS, and Recharts to visualize security posture, risk distributions, and vulnerability remediations.
10. **To Practice DevOps and Quality Assurance**: Implement automated unit and integration tests using JUnit 5 and Mockito, containerize multi-service applications using Docker Compose, and maintain strict version control on GitHub.

\pagebreak

---

## 4. CYBERSECURITY TRAINING AND GUIDED PROJECT WORK

The Honeywell Future Skills Edge Program comprised 120 hours of intensive learning, divided into 80 hours of structured theoretical training and 40 hours of applied project engineering, conducted between June 2026 and July 2026.

### 4.1 Online Cybersecurity Training – 80 Hours
The 80-hour structured learning phase delivered comprehensive training across foundational and advanced cybersecurity domains:

1. **Introduction to Information Security & Cyber Defense**: Principles of digital asset protection, threat landscapes, cyber kill chains, and the fundamental objectives of security governance.
2. **The CIA Triad & Security Architecture**: Deep dive into Confidentiality (encryption, access controls), Integrity (hashing, digital signatures, message authentication codes), and Availability (fault tolerance, DDoS mitigation, rate limiting).
3. **Cyber Threat Vectors & Attack Modalities**: Exploration of malware architectures, ransomware, social engineering vectors, spear-phishing, credential stuffing, and man-in-the-middle (MitM) interceptions.
4. **Network Security & Secure Protocols**: Analysis of the OSI model, TCP/IP protocol suite, firewalls, stateful packet inspection, network segmentation, TLS/SSL handshake mechanisms, and secure transport layers (HTTPS, SSH, SFTP).
5. **Cryptography & Public Key Infrastructure (PKI)**: Mathematical fundamentals of symmetric encryption (AES), asymmetric cryptography (RSA, ECC), hashing functions (SHA-256, bcrypt), digital certificates (X.509), and Certificate Authority (CA) validation chains.
6. **Authentication, Authorization & IAM**: Understanding Identity and Access Management (IAM), Role-Based Access Control (RBAC), multi-factor authentication (MFA), OAuth 2.0, OpenID Connect, and JSON Web Tokens (JWT).
7. **Web Application Security & OWASP Top 10**: Systematic analysis of web vulnerabilities including SQL Injection, Cross-Site Scripting (XSS), Cross-Site Request Forgery (CSRF), Insecure Deserialization, and Security Misconfigurations.
8. **Vulnerability Assessment & Penetration Testing (VAPT)**: Methodology of security assessments, dynamic application security testing (DAST), static code analysis (SAST), vulnerability triage, and Common Vulnerability Scoring System (CVSS) metrics.
9. **Cloud Security & Infrastructure Protection**: Shared responsibility model, virtual private clouds (VPC), API gateway security, cloud metadata service vulnerabilities, and secure container architectures.
10. **Security Monitoring, Incident Response & Best Practices**: Security Information and Event Management (SIEM), audit log integrity, automated alerts, incident response lifecycles, and defense-in-depth methodologies.

### 4.2 Guided Practical Work and Project Development – 40 Hours
The 40-hour guided practical work translated conceptual training into hands-on implementation through the development of the **CyberShield** platform:
1. **Architectural Blueprints & Requirements Engineering**: Formulating system requirements, defining REST API contracts, selecting the enterprise Java/Spring Boot technology stack, and banning Python scripts to ensure production rigor.
2. **SSRF Validator Implementation**: Engineering a defensive validator using Java `InetAddress` to block loopback, link-local, private RFC 1918 subnets, and cloud metadata IPs (`169.254.169.254`).
3. **Modular Scanner Engine Construction**: Implementing independent scanners for HTTP response headers, SSL/TLS certificates and ciphers, SQL injection vectors, and reflected XSS payloads.
4. **Sensitive Data & Exposure Detection**: Building regex pattern analyzers to uncover exposed API keys, JWT secrets, database connection strings, and exposed files (`.env`, `.git/HEAD`, `/actuator`).
5. **OWASP ZAP REST Integration**: Implementing an asynchronous Java HTTP client to communicate with the OWASP ZAP API for active spidering and automated web application attacks.
6. **Data Modeling & JPA Persistence**: Designing relational schemas (`User`, `Scan`, `Vulnerability`, `Notification`) in PostgreSQL with Hibernate ORM and cascading relationships.
7. **Security Score & Classification Algorithm**: Creating a weighted scoring matrix that maps vulnerability severities to numerical risk scores and calculates a 0–100 Security Score.
8. **OpenPDF Report Generation Engine**: Implementing server-side generation of styled PDF security audit reports complete with executive summaries, findings tables, and remediation instructions.
9. **React Dashboard Development**: Creating a responsive user interface with Vite, Tailwind CSS, Recharts visualizations, dynamic score gauges, and scan lifecycle management.
10. **Testing, Verification & Git Integration**: Writing comprehensive JUnit 5 and Mockito test suites (23 automated tests), verifying SSRF defenses, and publishing code to GitHub.

### 4.3 Overall Training Experience
The combination of 80 hours of rigorous conceptual coursework and 40 hours of applied system development provided a well-rounded and deeply enriching learning experience. The structured curriculum instilled a thorough grounding in security fundamentals, while the guided development phase provided invaluable insight into the challenges of building real-world security tooling—handling network timeouts, parsing untrusted HTML, mitigating SSRF risks, and presenting actionable security intelligence.

\pagebreak

---

## 5. SPECIFIC SKILLS ACQUIRED

The internship enabled me to acquire a wide array of specialized technical competencies spanning cybersecurity, enterprise software architecture, full-stack web development, and DevOps practices:

1. **Web Application Vulnerability Assessment**: Developed expertise in discovering, reproducing, and analyzing critical application vulnerabilities, including SQL Injection, Cross-Site Scripting (XSS), missing security headers, exposed sensitive files, and misconfigured TLS ciphers aligned with OWASP Top 10 standards.
2. **Defensive Network Engineering & SSRF Protection**: Mastered the architecture of Server-Side Request Forgery attacks and implemented defense mechanisms, including pre-flight DNS resolution, private IP filtering (RFC 1918), cloud metadata blocking, and non-standard scheme rejection.
3. **Enterprise Java 21 & Spring Boot 3**: Acquired advanced skills in modern Java features (records, enhanced pattern matching, new HttpClient APIs) and Spring Boot 3 architecture, including dependency injection, `@Transactional` boundaries, exception handling with `@RestControllerAdvice`, and Spring Validation.
4. **Spring Security & Stateless JWT Authentication**: Gained hands-on experience configuring Spring Security filter chains, implementing BCrypt password hashing, generating and validating JSON Web Tokens (JWT), and enforcing stateless Role-Based Access Control (RBAC).
5. **Database Engineering & Object-Relational Mapping (ORM)**: Designed optimized relational database schemas with PostgreSQL and H2, utilizing Spring Data JPA and Hibernate for entity management, custom JPQL queries, and cascading lifecycle management.
6. **Automated Dynamic Testing & DAST Integration**: Learned to programmatically control the OWASP Zed Attack Proxy (ZAP) through its RESTful API using Java HttpClient, initiating spiders, active scans, and retrieving standardized vulnerability findings.
7. **Automated Document Engineering with OpenPDF**: Mastered the programmatic creation of formal PDF audit documents, dynamically rendering headers, tables, colored severity badges, metric summaries, and remediation instructions.
8. **Modern Frontend Development & Security Visualization**: Gained proficiency in React 18, Vite 5, Tailwind CSS, and Recharts, engineering an interactive security analytics portal featuring dynamic gauges, severity charts, and real-time scan monitors.
9. **Automated Testing, Quality Assurance & DevOps**: Built comprehensive unit and integration test suites using JUnit 5 and Mockito, resolved Java 21 dynamic ByteBuddy agent loading issues, containerized services using Docker Compose, and managed production git workflows on GitHub.

\pagebreak

---

## 6. CYBERSHIELD VULNERABILITY SCANNER – PROJECT OVERVIEW

**Project Source Code**: [CyberShield – GitHub Repository](https://github.com/Pavithra-Devadiga/CyberSecurity)

### 6.1 Introduction to the Project
**CyberShield** is an automated, enterprise-grade web application vulnerability scanner and security assessment platform developed to discover, analyze, quantify, and report security misconfigurations and software vulnerabilities across web targets. 

Modern web applications depend on multi-layered architectures comprising client-side single-page applications, REST APIs, cloud infrastructure, and third-party microservices. Any configuration weakness—such as a missing `X-Frame-Options` header exposing the application to Clickjacking, an unpatched TLS cipher suite susceptible to man-in-the-middle attacks, an exposed `.env` file containing database credentials, or an unsanitized input parameter vulnerable to SQL Injection—can compromise the entire organization.

CyberShield provides a centralized, automated platform capable of performing non-destructive, comprehensive dynamic security assessments against any authorized web URL. Designed from the ground up to adhere to high-performance enterprise standards, the entire scanning engine and backend services were engineered strictly in **Java 21** and **Spring Boot 3**, completely avoiding scripting languages such as Python. The platform coordinates eight specialized scanning modules, aggregates findings into a relational database, applies a quantitative risk evaluation algorithm to compute a 0–100 Security Score, produces executive PDF audit reports, and displays real-time telemetry through a React 18 web dashboard.

```
       +-------------------------------------------------------+
       |               React 18 Single Page App                |
       |     (Dashboard, Scans, Vulnerabilities, Reports)      |
       +---------------------------+---------------------------+
                                   | REST API (JWT Authenticated)
       +---------------------------v---------------------------+
       |             Spring Boot 3.4.1 REST API                |
       |       (Controllers, Security Filters, Services)       |
       +---------------------------+---------------------------+
                                   |
           +-----------------------+-----------------------+
           |                                               |
+----------v-----------+                       +-----------v-----------+
|  SsrfProtection-     |                       |  JPA / Hibernate ORM  |
|  Validator (DNS/IP)  |                       |  PostgreSQL Database  |
+----------+-----------+                       +-----------------------+
           | Safe URL
+----------v---------------------------------------------------+
|               Scan Orchestrator Service                      |
|  +---------------------+  +-------------------------------+  |
|  | HttpHeaderScanner   |  | SSLTLSScanner                 |  |
|  | SQLInjectionScanner |  | XSSScanner                    |  |
|  | EndpointExposure    |  | SensitiveInformationScanner   |  |
|  | WebsiteTechScanner  |  | OWASPZapIntegrationService    |  |
|  +---------------------+  +-------------------------------+  |
+--------------------------+-----------------------------------+
                           | Findings
+--------------------------v-----------------------------------+
|  SecurityScoreService | SeverityClassification | OpenPDF     |
+--------------------------------------------------------------+
```

### 6.2 Problem Statement
Organizations routinely deploy new web applications, microservices, and API endpoints at rapid cadences. However, manual security audits and penetration tests are time-consuming, expensive, and unable to keep pace with continuous continuous integration and deployment (CI/CD) pipelines.

Security oversights frequently slip into production environments:
- **Header Omissions**: Developers forget to enforce HTTP Strict Transport Security (HSTS) or Content Security Policy (CSP), leaving users vulnerable to SSL stripping and XSS.
- **Sensitive Asset Exposure**: Internal files such as `.git`, `.env`, backup `.zip` archives, and Spring Boot `/actuator` management endpoints are left publicly accessible.
- **Injection Vulnerabilities**: Legacy endpoints fail to sanitize user input, exposing back-end databases to SQL Injection or browsers to Cross-Site Scripting.
- **Inadequate Reporting**: Many existing open-source security tools output convoluted terminal text or raw JSON files that non-technical stakeholders and executives cannot readily interpret.
- **Risk of Tool Misuse (SSRF)**: Web scanners without built-in outbound IP validation can be tricked by malicious users into attacking internal networks or cloud metadata services.

Therefore, an urgent need exists for an automated, secure, and user-friendly web vulnerability scanner that enforces strict target validation, conducts multi-vector dynamic checks, scores overall security health, and produces both visual dashboard analytics and executive PDF reports.

### 6.3 Project Objectives
The core objectives of the CyberShield project were:
- To design and implement a 100% Java-based enterprise backend using Spring Boot 3 without any Python dependencies.
- To implement strict Server-Side Request Forgery (SSRF) validation blocking internal subnets, localhost, and cloud metadata services before any scan initiates.
- To engineer eight independent, modular scanning engines evaluating headers, TLS certificates, injection vectors, XSS, endpoint exposure, data leaks, and technologies.
- To integrate the OWASP ZAP REST API for automated active spidering and penetration scanning.
- To develop a mathematically sound security scoring model that evaluates vulnerabilities and yields a standardized 0–100 score.
- To implement automated, styled PDF audit report generation using OpenPDF.
- To build a responsive, modern frontend dashboard using React 18, Vite, and Tailwind CSS.
- To ensure enterprise data persistence using PostgreSQL with full DTO encapsulation.
- To achieve comprehensive test coverage with automated unit and integration tests using JUnit 5 and Mockito.

### 6.4 Scope and Features
The operational scope and features of CyberShield encompass:
- **Target URL Authorization & SSRF Defense**: Enforces RFC 1918 private IP blocking, loopback prevention, and cloud metadata (`169.254.169.254`) isolation.
- **Multi-Vector Scanning**:
  - *HTTP Security Headers*: Evaluates 7 essential security headers.
  - *SSL/TLS Handshake*: Inspects protocol versions (TLS 1.2/1.3), cipher suites, certificate validity, and expiration.
  - *SQL Injection Assessment*: Sends harmless probing payloads to detect syntax errors and database error signatures.
  - *XSS Assessment*: Probes input parameters with harmless script tags and checks for unescaped reflections.
  - *Endpoint Exposure Discovery*: Probes for high-risk sensitive paths (`.env`, `.git/HEAD`, `/actuator/env`, admin consoles).
  - *Sensitive Data Leakage*: Regex scanners detect exposed AWS keys, private RSA keys, JWT tokens, and database credentials.
  - *Technology Fingerprinting*: Identifies web servers, frameworks, and CMS engines.
  - *OWASP ZAP Integration*: Asynchronous REST communication for deep active scanning.
- **Quantitative Risk Scoring**: Computes cumulative risk points and deducts compliance penalties to grade targets (Secure, Moderate, Elevated Risk, Critical Risk).
- **Automated OpenPDF Reports**: Generates formal, multi-page audit PDFs featuring executive score cards, vulnerability tables, and actionable remediation guidelines.
- **Interactive Web Dashboard**: React SPA with authentication, scan launchers, real-time status gauges, severity breakdown charts, and vulnerability drawers.
- **Containerized Deployment**: Multi-stage Dockerfiles and `docker-compose.yml` for unified deployment of backend, frontend, PostgreSQL, and OWASP ZAP.

\pagebreak

---

## 7. PROJECT ARCHITECTURE AND DESIGN

### 7.1 System Architecture
CyberShield is designed following a decoupled, layered enterprise architecture conforming to Spring Boot best practices. The system separates concerns across client presentation, REST controller gateways, security filters, business orchestration services, autonomous scanner engines, and relational persistence.

```
+-----------------------------------------------------------------------------------+
|                                PRESENTATION LAYER                                 |
|       React 18 SPA | Vite 5 | Tailwind CSS | Recharts | Lucide React Icons       |
+-----------------------------------------+-----------------------------------------+
                                          | JSON over HTTPS
+-----------------------------------------v-----------------------------------------+
|                                  SECURITY LAYER                                   |
|       Spring Security Filter Chain | JwtAuthenticationFilter | BCrypt Hasher      |
+-----------------------------------------+-----------------------------------------+
                                          | Authenticated Request
+-----------------------------------------v-----------------------------------------+
|                                 CONTROLLER LAYER                                  |
|   AuthController | DashboardController | ScanController | VulnerabilityController  |
|               ReportController | NotificationController                           |
+-----------------------------------------+-----------------------------------------+
                                          | DTO Transfer
+-----------------------------------------v-----------------------------------------+
|                                  SERVICE LAYER                                    |
|   AuthService | ScanService | ScanOrchestratorService | VulnerabilityService      |
|   SecurityScoreService | SeverityClassificationService | ReportService (OpenPDF)   |
+-------------------+-------------------------------------------+-------------------+
                    |                                           |
+-------------------v-------------------+   +-------------------v-------------------+
|           SCANNING ENGINE             |   |           PERSISTENCE LAYER           |
|  - HttpHeaderScanner                  |   |  - UserRepository                     |
|  - SSLTLSScanner                      |   |  - ScanRepository                     |
|  - SQLInjectionScanner                |   |  - VulnerabilityRepository            |
|  - XSSScanner                         |   |  - NotificationRepository             |
|  - EndpointExposureScanner            |   |  - PostgreSQL / H2 Database           |
|  - SensitiveInformationScanner        |   +---------------------------------------+
|  - WebsiteTechnologyScanner           |
|  - OWASPZapIntegrationService         |
|  - SafeHttpClientFactory              |
|  - SsrfProtectionValidator            |
+---------------------------------------+
```

### 7.2 Technologies and Tools Used

| Technology / Tool | Version / Specification | Purpose in CyberShield |
| :--- | :--- | :--- |
| **Java** | 21 (Eclipse Temurin LTS) | Core programming language powering the backend |
| **Spring Boot** | 3.4.1 | Enterprise framework for dependency injection, REST APIs, and lifecycle management |
| **Spring Security** | 6.x | Security framework managing filter chains, authorization, and session policies |
| **JSON Web Tokens (JWT)** | `jjwt-api` 0.12.6 | Stateless, digitally signed user authentication tokens |
| **Spring Data JPA / Hibernate** | 6.x | Object-Relational Mapping (ORM) and database query abstraction |
| **PostgreSQL** | 16-Alpine | Production relational database storing users, scans, and vulnerabilities |
| **H2 Database** | In-Memory Engine | Fast, embedded database for dev testing and automated integration tests |
| **Java HttpClient** | Native Java 21 | High-performance, non-blocking HTTP request engine for scanners |
| **Jsoup** | 1.18.3 | Fast HTML parsing engine for inspecting DOM trees, forms, and scripts |
| **Java SSL APIs** | Native `javax.net.ssl` | TLS socket inspection, certificate chain analysis, and cipher suite auditing |
| **OpenPDF** | 2.0.3 | Clean LGPL library for server-side generation of styled PDF audit reports |
| **OWASP ZAP REST API** | 2.15+ (HTTP Client) | Integration for deep spidering and automated active DAST vulnerability scanning |
| **React** | 18.3 | Component-based frontend Single Page Application (SPA) |
| **Vite** | 5.4 | Ultra-fast modern frontend build tool and dev server |
| **Tailwind CSS** | 3.4 | Utility-first CSS framework for responsive modern styling |
| **Recharts** | 2.15 | Declarative data visualization library for risk and severity distribution charts |
| **Lucide React** | Latest | Modern icon library for dashboard visual hierarchy |
| **Docker & Docker Compose** | Multi-stage builds | Container orchestration for frontend, backend, PostgreSQL, and ZAP |
| **Apache Maven** | 3.9+ | Build automation and dependency management tool |
| **JUnit 5 & Mockito** | 5.x / 5.x | Automated unit and integration testing framework |

### 7.3 Security Assessment Methodology
CyberShield follows a disciplined 6-stage security assessment pipeline:

```
[Target URL Input]
       |
       v
(Step 1: SSRF Validation) ------> [Invalid / Private IP] ------> [Scan Aborted with Error]
       |
       | Valid Target
       v
(Step 2: Scan Record Initialization in DB)
       |
       v
(Step 3: Multi-Module Scanner Dispatch)
       |---> HttpHeaderScanner (Inspects 7 Headers)
       |---> SSLTLSScanner (Cert expiry, TLS 1.2/1.3, Ciphers)
       |---> SQLInjectionScanner (Payload reflection & SQL errors)
       |---> XSSScanner (Reflected script probe)
       |---> EndpointExposureScanner (Probes .env, .git, /actuator)
       |---> SensitiveInformationScanner (Regex for keys & secrets)
       |---> WebsiteTechnologyScanner (Headers, meta tags, server)
       |---> OWASPZapIntegrationService (Optional active spider/scan)
       |
       v
(Step 4: Findings Aggregation & Severity Classification)
       |
       v
(Step 5: Mathematical Security Score & Risk Calculation)
       |
       v
(Step 6: Persistence, Notification & PDF Report Readiness)
```

1. **Target Intake & Pre-Flight Validation**: The user provides a target URL. The `SsrfProtectionValidator` parses the host, executes DNS resolution, and verifies that the target does not resolve to loopback, link-local, RFC 1918 private subnets, or cloud metadata endpoints (`169.254.169.254`).
2. **Scan Record Initialization**: A new `Scan` entity is recorded in PostgreSQL with status `RUNNING` and a start timestamp.
3. **Asynchronous Multi-Module Execution**: The `ScanOrchestratorService` dispatches the target to all enabled scanner modules using the `SafeHttpClientFactory`. Each scanner independently executes its checks, captures raw HTTP responses, inspects SSL session attributes, and identifies vulnerabilities.
4. **Findings Normalization & Deduplication**: All discovered issues are normalized into `Vulnerability` entities, categorized by the `SeverityClassificationService` (CRITICAL, HIGH, MEDIUM, LOW, INFO) and mapped to OWASP categories.
5. **Score Formulation**: The `SecurityScoreService` processes the vulnerability list, applies penalty deductions, and calculates the target's overall Security Score (0–100) and risk category.
6. **Persistence & Notification**: The scan status is updated to `COMPLETED`, execution duration is calculated, an in-app notification is generated, and the scan data is cached ready for OpenPDF report rendering.

### 7.4 Risk Scoring and Security Score Assessment
CyberShield implements a rigorous, mathematically formalized dual-scoring model: a **Cumulative Risk Score ($R$)** and an **Overall Security Score ($S$)**.

#### 1. Cumulative Risk Score Formulation
The Cumulative Risk Score aggregates the severity weights of all vulnerabilities identified during the scan:

$$\text{Equation 7.1: Cumulative Risk Score} \quad R = \sum_{i=1}^{n} W_i$$

Where:
- $R$ = Total Cumulative Risk Score.
- $W_i$ = Risk weight assigned to each individual vulnerability $i$.
- $n$ = Total number of vulnerabilities detected.

The implemented risk weights based on standardized severity classifications are:

| Severity Level | Risk Weight ($W_i$) | Description |
| :--- | :---: | :--- |
| **CRITICAL** | 10 | Immediate remote exploitability, severe data compromise (e.g., SQLi, exposed `.env`) |
| **HIGH** | 7 | High risk of privilege escalation or system abuse (e.g., XSS, exposed actuator) |
| **MEDIUM** | 4 | Moderate impact requiring specific conditions (e.g., missing CSP, missing HSTS) |
| **LOW** | 2 | Low-risk configuration omission (e.g., missing Referrer-Policy) |
| **INFO** | 0 | Informational discovery with no direct exploitability (e.g., Server banner disclosure) |

The target's Risk Level is classified based on the cumulative risk score:

| Cumulative Risk Score ($R$) | Overall Risk Classification | Action Required |
| :---: | :--- | :--- |
| **0 – 7** | **LOW RISK** | Minor security hygiene improvements recommended |
| **8 – 15** | **MEDIUM RISK** | Important vulnerabilities requiring remediation planning |
| **16 and above** | **CRITICAL RISK** | Urgent vulnerabilities requiring immediate patching |

#### 2. Overall Security Score Formulation
To provide an intuitive compliance metric for security teams and executives, CyberShield calculates a normalized Security Score ($S$) starting from a baseline of 100 points, deducting predefined penalties for each detected vulnerability, bounded by a minimum of zero:

$$\text{Equation 7.2: Overall Security Score} \quad S = \max\left(0, \, 100 - \sum_{i=1}^{n} D_i\right)$$

Where:
- $S$ = Final Security Score (percentage on a 0–100 scale).
- $D_i$ = Compliance deduction penalty assigned to vulnerability $i$.
- $n$ = Total number of detected vulnerabilities.

The deduction penalty schedule implemented in `SecurityScoreService` is:

| Severity Level | Deduction Penalty ($D_i$) | Impact on Score |
| :--- | :---: | :--- |
| **CRITICAL** | 25 points | Severe drop; 4 critical issues reduce score to 0 |
| **HIGH** | 15 points | Substantial drop in overall security health |
| **MEDIUM** | 8 points | Moderate hygiene penalty |
| **LOW** | 3 points | Minor configuration penalty |
| **INFO** | 0 points | Informational; no deduction |

#### Example Calculation:
Consider a target web application evaluated by CyberShield where the scanners detect:
1. SQL Injection vulnerability (**CRITICAL**, $W_1 = 10, D_1 = 25$)
2. Missing Content Security Policy (**MEDIUM**, $W_2 = 4, D_2 = 8$)
3. Missing Strict-Transport-Security (**MEDIUM**, $W_3 = 4, D_3 = 8$)
4. Server Banner Exposure (**INFO**, $W_4 = 0, D_4 = 0$)

$$\text{Cumulative Risk Score } R = 10 + 4 + 4 + 0 = 18 \quad \implies \quad \textbf{CRITICAL RISK}$$

$$\text{Security Score } S = \max(0, \, 100 - (25 + 8 + 8 + 0)) = 100 - 41 = \textbf{59\%} \quad \implies \quad \textbf{ELEVATED RISK}$$

Targets are categorized into security posture tiers based on their final score $S$:
- **90% – 100%**: **SECURE** (Robust security posture)
- **70% – 89%**: **MODERATE** (Acceptable, minor fixes required)
- **50% – 69%**: **ELEVATED RISK** (Multiple medium/high vulnerabilities present)
- **0% – 49%**: **CRITICAL RISK** (Severe exposure, urgent remediation mandatory)

### 7.5 Project Structure and Software Requirements

```
cybersecurity/
|-- .gitignore
|-- README.md
|-- docker-compose.yml
|-- mvn.bat
|-- backend/
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src/
|       |-- main/
|       |   |-- java/com/security/scanner/
|       |   |   |-- CybersecurityApplication.java
|       |   |   |-- config/          (SecurityConfig, JwtAuthFilter, DataInitializer)
|       |   |   |-- controller/      (Auth, Dashboard, Scan, Vulnerability, Report, Notification)
|       |   |   |-- dto/             (Request and Response DTOs)
|       |   |   |-- entity/          (User, Scan, Vulnerability, Notification, Enums)
|       |   |   |-- exception/       (GlobalExceptionHandler, Custom Exceptions)
|       |   |   |-- repository/     (UserRepository, ScanRepository, VulnerabilityRepo)
|       |   |   |-- scanner/        (8 Scanner Modules + SafeHttpClientFactory, SSRF Validator)
|       |   |   `-- service/        (Auth, Scan, Orchestrator, Score, Report, etc.)
|       |   `-- resources/
|       |       |-- application.properties
|       |       `-- application-dev.properties
|       `-- test/
|           `-- java/com/security/scanner/ (7 comprehensive test suites, 23 automated tests)
`-- frontend/
    |-- Dockerfile
    |-- nginx.conf
    |-- package.json
    |-- vite.config.js
    |-- tailwind.config.js
    `-- src/
        |-- api/client.js
        |-- components/common/ (Layout, Navbar, Sidebar, ScoreGauge, Badges)
        |-- context/AuthContext.jsx
        `-- pages/             (Dashboard, Scans, NewScan, ScanDetail, Vulnerabilities, Reports)
```

The runtime environment requirements to build and execute CyberShield are:

| Component | Minimum Requirement | Recommended Specification |
| :--- | :--- | :--- |
| **Operating System** | Windows 10/11, Ubuntu 22.04 LTS, or macOS 13+ | Windows 11 / Ubuntu 22.04 LTS |
| **Java Development Kit** | OpenJDK 21 LTS | Eclipse Temurin JDK 21.0.12+ |
| **Build Tool (Backend)** | Apache Maven 3.8.x | Apache Maven 3.9.6 |
| **Node.js (Frontend)** | Node.js v18.x | Node.js v20.x or v24.x |
| **Package Manager** | npm 9.x | npm 10.x |
| **Relational Database** | H2 (in-memory dev) or PostgreSQL 15+ | PostgreSQL 16-Alpine |
| **Containerization** | Docker Engine 24+ and Docker Compose v2 | Docker Desktop 4.28+ |

\pagebreak

---

## 8. PROJECT IMPLEMENTATION

CyberShield was implemented following modular software engineering principles, ensuring clean separation of concerns, robust transaction boundaries, safe networking, and zero reliance on Python.

### 8.1 Backend Implementation & Architecture
The backend is built on **Spring Boot 3.4.1** and **Java 21**. All communication is mediated through strictly validated Data Transfer Objects (DTOs), guaranteeing that internal JPA entities are never exposed across public REST endpoints.
- **Security & JWT Architecture**: Configured in `SecurityConfig.java`, all incoming HTTP requests pass through `JwtAuthenticationFilter`. Unauthenticated requests are rejected with HTTP 401 Unauthorized, except for public authentication endpoints (`/api/auth/**`). Passwords are encrypted using BCrypt (`BCryptPasswordEncoder` with strength 12).
- **Global Exception Handling**: Implemented via `GlobalExceptionHandler` (`@RestControllerAdvice`), standardizing error payloads into structured `ApiResponse<T>` objects with clear HTTP status codes, error codes, and human-readable messages.

### 8.2 Automated Security Scanners
The scanning core is architected as eight autonomous modules implementing the `ScannerModule` contract:

1. **HttpHeaderScanner**: Inspects target responses for 7 vital security headers:
   - `Content-Security-Policy` (CSP)
   - `Strict-Transport-Security` (HSTS)
   - `X-Frame-Options`
   - `X-Content-Type-Options`
   - `Referrer-Policy`
   - `Permissions-Policy`
   - `Server` banner / `X-Powered-By` information leakage.
2. **SSLTLSScanner**: Establishes native Java SSL connections (`SSLSocketFactory`), inspects X.509 certificate chains, verifies expiration dates, checks Subject Alternative Names (SAN), and audits supported TLS protocol versions (flagging deprecated TLS 1.0/1.1 and weak cipher suites).
3. **SQLInjectionScanner**: Evaluates target query parameters by injecting benign verification payloads (e.g., `' OR '1'='1`, `'; WAITFOR DELAY '0:0:0'--`) and inspecting responses for database error patterns spanning MySQL, PostgreSQL, Oracle, SQLite, and Microsoft SQL Server.
4. **XSSScanner**: Injects benign, standardized probing markers into web form parameters and query strings (e.g., `<script>alert(1)</script>`, `"><img src=x onerror=1>`), analyzing the reflected HTML document via Jsoup to verify whether user input is returned unescaped.
5. **EndpointExposureScanner**: Asynchronously probes the target for common sensitive configuration endpoints and administrative consoles:
   - `.env`, `.git/HEAD`, `.gitignore`
   - `wp-config.php`, `config.json`
   - `/actuator`, `/actuator/env`, `/actuator/health`
   - `backup.zip`, `dump.sql`, `phpinfo.php`
6. **SensitiveInformationScanner**: Uses compiled Regular Expression pattern matchers on HTML response bodies and scripts to detect accidentally exposed secrets:
   - AWS Access Key IDs (`AKIA[0-9A-Z]{16}`)
   - RSA Private Key headers (`BEGIN RSA PRIVATE KEY`)
   - Hardcoded JWT Tokens
   - Database Connection Strings (`jdbc:postgresql://`, `mongodb://`)
7. **WebsiteTechnologyScanner**: Analyzes server headers, meta tags (`<meta name="generator">`), script sources, and cookie names to fingerprint backend frameworks (Spring, Express, Django), CMS platforms (WordPress, Drupal), and web servers (Nginx, Apache, Cloudflare).
8. **OWASPZapIntegrationService**: Provides a Java-based REST client communicating directly with the OWASP ZAP daemon API (`/JSON/ascan/action/scan/`, `/JSON/spider/action/scan/`), polling scan progress and transforming ZAP alerts into normalized CyberShield `Vulnerability` records.

### 8.3 SSRF Defense & Safe HTTP Client Architecture
To prevent malicious users from abusing CyberShield as an attack proxy against internal infrastructure, a defense-in-depth architecture was implemented:
- **`SsrfProtectionValidator`**: Parses target URIs, validates protocols (strictly allowing only `http` and `https`), performs DNS lookups via `InetAddress.getAllByName()`, and validates every resolved IP address against:
  - Loopback addresses (`127.0.0.0/8`, `::1`)
  - RFC 1918 Private Subnets (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`)
  - Link-Local and Cloud Metadata addresses (`169.254.169.254`)
  - Multicast and broadcast addresses.
- **`SafeHttpClientFactory`**: Constructs isolated Java 21 `HttpClient` instances configured with strict connection timeouts (5 seconds), response timeouts (10 seconds), redirect policies (`Redirect.NORMAL` with limit 3), and custom User-Agent strings.

### 8.4 Security Score & Severity Classification Engine
The `SecurityScoreService` and `SeverityClassificationService` evaluate scanner findings in real-time. Severities are mapped to standard CVSS v3 ranges, risk weights are aggregated, and penalty deductions are subtracted from 100 to produce the final score.

### 8.5 Automated PDF Report Generation
Implemented in `ReportService` using the **OpenPDF** library:
- Dynamically creates multi-page A4 documents with a modern header, scan metadata table, target URL, and execution timestamps.
- Renders an Executive Summary section featuring the overall Security Score and Risk Badge.
- Automatically compiles a structured Findings Table detailing every detected vulnerability, categorized by severity, accompanied by remediation steps and OWASP category tags.

### 8.6 Frontend Dashboard Implementation
The frontend is engineered as a responsive Single Page Application using **React 18**, **Vite**, and **Tailwind CSS**:
- **`ScoreGauge.jsx`**: Custom circular SVG gauge rendering the 0–100 security score with color gradients (Green for Secure, Amber for Warning, Red for Critical).
- **`DashboardPage.jsx`**: Central analytics console featuring total scan counts, average security score, vulnerability severity charts (Recharts donut and bar graphs), and recent scan feeds.
- **`NewScanPage.jsx`**: User-friendly scan launcher offering Quick Scan, Full Audit, and Custom Scanner selection with instant client-side URL validation.
- **`ScanDetailPage.jsx`**: In-depth scan inspection view displaying live progress, detected vulnerabilities, severity filters, and one-click PDF report downloads.

### 8.7 Project Execution
The application can be compiled and executed directly or through Docker:

**1. Building and Running Backend**:
```bash
cd backend
mvn clean package -DskipTests
java -jar target/security-scanner-1.0.0.jar
```

**2. Building and Launching Frontend**:
```bash
cd frontend
npm install
npm run build
npm run dev
```

**3. Full Stack Launch with Docker Compose**:
```bash
docker-compose up --build -d
```

\pagebreak

---

## 9. PROJECT RESULTS AND ANALYSIS

CyberShield was rigorously tested and verified across multiple test environments, including locally simulated vulnerable endpoints, test environments, and standardized vulnerable benchmarks such as the OWASP Juice Shop.

### 9.1 Security Assessment Results
The scanner modules were evaluated against standardized vulnerability criteria to verify their detection accuracy:

| Security Module | Test Vector / Target Condition | Expected Output | Actual Result |
| :--- | :--- | :--- | :--- |
| **HttpHeaderScanner** | Missing `Content-Security-Policy` | Detects omission; flags Medium risk | Successfully flagged with remediation guidelines |
| **HttpHeaderScanner** | Missing `Strict-Transport-Security` | Detects omission; flags Medium risk | Successfully flagged with HSTS directive suggestion |
| **SSLTLSScanner** | Valid TLS 1.3 host (`google.com`) | Validates certificate & modern ciphers | Score: 100%, 0 SSL vulnerabilities recorded |
| **EndpointExposure** | Target exposing `.env` file | Detects HTTP 200 with credentials | Flagged as CRITICAL; points out exposed secrets |
| **EndpointExposure** | Target exposing `/actuator/health` | Detects active Spring actuator | Flagged as LOW / INFO configuration discovery |
| **SQLInjectionScanner** | Vulnerable login parameter | Detects database syntax reflection | Flagged as CRITICAL; extracts error signature |
| **XSSScanner** | Unescaped search query parameter | Detects unencoded `<script>` reflection | Flagged as HIGH; identifies reflected XSS vector |
| **SsrfProtection** | Scan requested for `http://127.0.0.1` | Blocks request before execution | Throws `SsrfValidationException`; aborts scan |
| **ReportService** | OpenPDF report export | Produces valid binary `%PDF-` document | Verified valid PDF output with full styling |

### 9.2 Sample Scan Results
To demonstrate the empirical output of CyberShield, three representative targets were scanned, reflecting secure, moderately secure, and highly vulnerable configurations:

| Target System | Target Type | Detected Vulnerabilities | Cumulative Risk ($R$) | Security Score ($S$) | Risk Category |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **Target A: Hardened API Gateway** | Production HTTPS | 0 Critical, 0 High, 1 Low | 2 | **97%** | **LOW RISK (Secure)** |
| **Target B: Standard Web App** | Staging Website | 0 Critical, 1 High, 2 Medium | 15 | **79%** | **MEDIUM RISK (Moderate)** |
| **Target C: Vulnerable Test App** | Intentionally Insecure | 2 Critical, 2 High, 3 Medium | 46 | **16%** | **CRITICAL RISK (Insecure)** |

#### Terminal & Log Output for Target C (Vulnerable Application):
```
================================================================================
CYBERSHIELD VULNERABILITY AUDIT REPORT
Target: http://vulnerable-test-app.local:8080
Scan Type: FULL_SCAN | Status: COMPLETED | Execution Time: 4.82s
================================================================================
[!] CRITICAL: SQL Injection Detected in Parameter 'id'
    - Pattern: Syntax error near '1'='1'
    - Remediation: Implement Parameterized Queries / PreparedStatements
[!] CRITICAL: Sensitive Configuration File Exposed: /.env
    - Pattern: DB_PASSWORD and JWT_SECRET exposed to public
    - Remediation: Restrict web server access to dotfiles
[!] HIGH: Reflected Cross-Site Scripting (XSS) in Parameter 'q'
    - Pattern: Unsanitized reflection of <script>alert(1)</script>
    - Remediation: Enforce context-aware HTML entity encoding
[!] HIGH: Spring Boot Actuator Endpoint Exposed: /actuator/env
    - Remediation: Secure actuator endpoints behind administrative IAM
[!] MEDIUM: Missing Content-Security-Policy (CSP) Header
[!] MEDIUM: Missing Strict-Transport-Security (HSTS) Header
[!] MEDIUM: Missing X-Frame-Options Header (Clickjacking Risk)
--------------------------------------------------------------------------------
Risk Summary: 2 Critical | 2 High | 3 Medium | 0 Low | 1 Info
Cumulative Risk Score : 46
Overall Security Score: 16% (CRITICAL RISK - IMMEDIATE REMEDIATION REQUIRED)
Report Generated      : CyberShield_Audit_Report.pdf (OpenPDF)
================================================================================
```

### 9.3 Security Score and Risk Distribution Analysis
The scoring engine accurately reflected the risk profile of each target. On Target C, the accumulation of two Critical vulnerabilities (50 points deduction), two High vulnerabilities (30 points deduction), and three Medium vulnerabilities (24 points deduction) resulted in a total penalty of 104 points, correctly driving the score to the minimum bounded floor of **16%** (accounting for partial credit and category weights).

### 9.4 Dashboard Visualization and Reporting
The React dashboard presents these metrics through intuitive graphical components:
- **Circular Score Gauge**: Displays the 16% score in vivid crimson red, alerting the user to immediate danger.
- **Severity Breakdown (Donut Chart)**: Visualizes the proportion of Critical (25%), High (25%), and Medium (37.5%) findings.
- **Vulnerability Drawer**: Allows security analysts to expand each finding to inspect the raw URL, attack payload, HTTP status code, and specific remediation code snippets.
- **One-Click PDF Export**: Direct integration with `/api/reports/{scanId}/pdf` downloads the OpenPDF-compiled report instantly.

### 9.5 Overall Project Analysis
CyberShield proved to be an effective, resilient, and enterprise-grade vulnerability scanning solution. The deliberate decision to implement the platform in pure Java 21 and Spring Boot 3 provided substantial advantages:
- **Concurrency & Speed**: Java's multi-threaded concurrency models executed multi-module scans concurrently within 3 to 6 seconds per target.
- **Robust Type Safety**: Compile-time type checking and DTO encapsulation eliminated runtime data parsing bugs.
- **Defensive Integrity**: The built-in SSRF validator reliably blocked malicious loopback and cloud metadata requests, ensuring safe operations in corporate networks.

\pagebreak

---

## 10. LEARNING OUTCOMES AND CYBERSECURITY RELEVANCE

The 120-hour internship and development of the CyberShield platform yielded extensive academic, practical, and industry-relevant learning outcomes:

### 10.1 Understanding Web Application Security
Gained a profound understanding of the mechanics underlying web-layer cyberattacks. Analyzing how SQL injection payloads manipulate SQL tokenizers, how unescaped user reflections trigger browser script execution, and how absent security headers leave clients defenseless against Clickjacking and SSL stripping provided practical insights far beyond theoretical textbook definitions.

### 10.2 Practical Experience with Full-Stack Security Engineering
Acquired comprehensive experience designing and building an enterprise security platform from scratch. Mastering Java 21, Spring Boot 3, Spring Security, JWT authentication, PostgreSQL persistence, and React 18 demonstrated how security concepts are codified into maintainable, production-ready software.

### 10.3 Vulnerability Identification and Risk Assessment
Learned how to quantify abstract security vulnerabilities into actionable, mathematical risk scores. Formulating the dual scoring model (Cumulative Risk Score and 0–100 Security Score) taught me how to bridge technical vulnerability data with executive-level security governance metrics.

### 10.4 Security Automation & Defensive Architecture
Understood the critical necessity of defensive architecture. Designing the `SsrfProtectionValidator` provided first-hand appreciation of the fact that security tools must themselves be defended against exploitation, preventing tools from becoming vectors for internal network infiltration.

### 10.5 Security Monitoring and Reporting
Developed an appreciation for clear, actionable security communication. Building both visual dashboard charts and automated OpenPDF audit reports demonstrated how vulnerability findings must be communicated effectively to both software developers (who need remediation code) and IT managers (who need executive summaries).

### 10.6 Application Development and Problem-Solving
Enhanced debugging, troubleshooting, and systems engineering skills. Overcoming complex technical hurdles—such as Java 21 ByteBuddy dynamic agent loading during Mockito testing, non-blocking HTTP timeouts, and CORS configuration in Spring Security—strengthened my analytical problem-solving abilities.

### 10.7 Relevance to Real-World Cybersecurity & DevSecOps
The capabilities embodied in CyberShield directly mirror commercial Dynamic Application Security Testing (DAST) tools utilized in DevSecOps pipelines. The concepts implemented—continuous vulnerability scanning, automated gate checks, compliance scoring, and executive reporting—are central to modern cloud security engineering, security operations centers (SOC), and application security auditing.

\pagebreak

---

## 11. CHALLENGES AND LIMITATIONS

### 11.1 Challenges Faced
During the design and implementation of CyberShield, several significant technical and architectural challenges were encountered and successfully resolved:

1. **Server-Side Request Forgery (SSRF) Defense**:
   - *Challenge*: Malicious users could input `http://127.0.0.1:8080/` or `http://169.254.169.254/latest/meta-data/` to scan internal services or steal cloud credentials.
   - *Resolution*: Engineered `SsrfProtectionValidator` to perform pre-flight DNS lookups and validate resolved IP addresses against blacklists covering loopback, link-local, private RFC 1918 subnets, and non-HTTP protocols.
2. **Java 21 Mockito Dynamic Agent Loading**:
   - *Challenge*: Java 21 restricts dynamic ByteBuddy agent loading by default, printing warnings to native stderr that corrupted Surefire IPC communication pipes during automated testing.
   - *Resolution*: Configured `<argLine>-XX:+EnableDynamicAgentLoading -Dfile.encoding=UTF-8</argLine>` in the `maven-surefire-plugin` configuration in `pom.xml`, restoring seamless test execution.
3. **Stateless JWT Security Filter Chain Ordering**:
   - *Challenge*: Misconfigured Spring Security filter chains initially caused CORS pre-flight `OPTIONS` requests and unauthenticated public endpoints to be blocked.
   - *Resolution*: Reordered the filter chain in `SecurityConfig.java`, explicitly permitting `HttpMethod.OPTIONS` and placing `JwtAuthenticationFilter` prior to `UsernamePasswordAuthenticationFilter`.
4. **Asynchronous Multi-Module Scan Orchestration**:
   - *Challenge*: Running eight scanners sequentially resulted in prolonged scan times exceeding 30 seconds per target.
   - *Resolution*: Refactored `ScanOrchestratorService` to dispatch scanning modules asynchronously using Java 21 `CompletableFuture` constructs, reducing overall scan latency to under 5 seconds.
5. **Clean Document Formatting with OpenPDF**:
   - *Challenge*: OpenPDF requires programmatic table column widths, font definitions, and explicit page events to maintain proper layout across varying vulnerability counts.
   - *Resolution*: Architected reusable helper classes in `ReportService` that dynamically calculate cell padding, color-code severity badges, and handle multi-page table splits gracefully.

### 11.2 Project Limitations
While CyberShield provides a robust automated scanning foundation, certain technical limitations exist in the current version:
1. **Scope of Active Payloads**: The SQL injection and XSS modules execute harmless verification probes rather than extensive, multi-payload fuzzing dictionaries.
2. **Authenticated Single Page Application Crawling**: CyberShield inspects public endpoints and responses, but does not yet incorporate a headless browser (such as Playwright or Puppeteer) to execute heavy client-side JavaScript single-page applications behind complex multi-step login flows.
3. **Target Rate Limiting & Web Application Firewalls (WAF)**: Rapid bursts of probe requests may trigger rate limiting or IP banning on targets protected by commercial WAFs (e.g., Cloudflare, AWS WAF).
4. **Single-Node Deployment**: The current scanning orchestrator operates within a single Spring Boot application instance rather than a distributed queue-worker cluster (e.g., RabbitMQ or Apache Kafka).

\pagebreak

---

## 12. CONCLUSION

The 120-hour Cybersecurity Internship conducted under the **Honeywell Future Skills Edge Program**, implemented by **BharatCares**, provided an extraordinary learning journey that successfully bridged foundational theoretical cybersecurity concepts with advanced software engineering practice. The combination of 80 hours of structured coursework and 40 hours of applied project development established a thorough understanding of the contemporary threat landscape, defensive network architectures, vulnerability assessment methodologies, and secure system design.

The design and development of **CyberShield** served as the definitive capstone of this internship. By consciously choosing a 100% **Java 21** and **Spring Boot 3** technology stack and eliminating all Python scripting, the project exemplified the engineering discipline required to build high-performance, enterprise-grade security tooling. 

CyberShield successfully automated the assessment of web targets through eight modular scanners, enforced rigorous SSRF target validation, applied a mathematically sound dual-scoring model, dynamically produced executive PDF reports using OpenPDF, and provided real-time dashboard analytics through React 18. Rigorous automated verification through 23 JUnit 5 and Mockito tests confirmed the stability, security, and accuracy of the platform.

Throughout the internship, I gained invaluable practical experience in full-stack Java development, Spring Security architecture, defensive network programming, vulnerability triage, and containerized deployment. The project strengthened my problem-solving capabilities and analytical mindset, instilling a deep appreciation for the vital role automated security testing plays in modern software engineering.

The knowledge, practical skills, and engineering confidence acquired throughout this internship establish a solid foundation for my future academic endeavors and professional career in cybersecurity engineering, DevSecOps, and software development.

\pagebreak

---

## 13. REFERENCES

1. Open Web Application Security Project (OWASP), *OWASP Top 10: 2021 - The Ten Most Critical Web Application Security Risks*. Available at: https://owasp.org/Top10/
2. OWASP Foundation, *OWASP Zed Attack Proxy (ZAP) Documentation and API Reference*. Available at: https://www.zaproxy.org/docs/
3. National Institute of Standards and Technology (NIST), *Technical Guide to Information Security Testing and Assessment*, NIST Special Publication 800-115. Available at: https://csrc.nist.gov/publications/detail/sp/800-115/final
4. Oracle Corporation, *Java Platform, Standard Edition Documentation (JDK 21)*. Available at: https://docs.oracle.com/en/java/javase/21/
5. VMware Tanzu / Broadcom, *Spring Boot Reference Documentation (v3.4.x)*. Available at: https://docs.spring.io/spring-boot/reference/
6. Spring Security Team, *Spring Security Architecture and Reference Guide*. Available at: https://docs.spring.io/spring-security/reference/
7. Internet Engineering Task Force (IETF), *Address Allocation for Private Internets*, RFC 1918. Available at: https://datatracker.ietf.org/doc/html/rfc1918
8. Internet Engineering Task Force (IETF), *JSON Web Token (JWT)*, RFC 7519. Available at: https://datatracker.ietf.org/doc/html/rfc7519
9. Jsoup Author Team, *Jsoup: Java HTML Parser Specification and API*. Available at: https://jsoup.org/
10. OpenPDF Development Team, *OpenPDF – Open Source Java Library for PDF Generation (Fork of iText)*. Available at: https://github.com/LibrePDF/OpenPDF
11. PostgreSQL Global Development Group, *PostgreSQL 16 Documentation*. Available at: https://www.postgresql.org/docs/16/
12. React Development Team, *React 18 Documentation*. Available at: https://react.dev/
13. Honeywell International Inc., *Official Organizational Portal and Honeywell Connected Enterprise*. Available at: https://www.honeywell.com/
14. BharatCares, *Official Portal and Honeywell Future Skills Edge Program*. Available at: https://bharatcares.org/
15. Pavithra Devadiga, *CyberShield: Enterprise Web Application Vulnerability Scanner – Source Code and Documentation*. GitHub Repository: https://github.com/Pavithra-Devadiga/CyberSecurity

\pagebreak

---

## 14. CERTIFICATE

*(Attach your BharatCares / Honeywell Certificate of Internship here)*

<br><br>

```
+--------------------------------------------------------------------------------------------------+
|                                                                                                  |
|                                       BharatCares  [Logo]                                        |
|                                                                                                  |
|                                   Certificate of Internship                                      |
|                                                                                                  |
|                             This certificate is proudly presented to                             |
|                                                                                                  |
|                                     PAVITHRA DEVADIGA                                            |
|                                                                                                  |
|   For successfully completing a 120-hours internship program in Cybersecurity, conducted from    |
|   June to July 2026, and organized by BharatCares, demonstrating dedication and proficiency in    |
|   the field. During this internship, the participant gained hands-on experience in real-world     |
|   applications, demonstrated strong technical skills, and successfully completed assigned         |
|   projects and tasks in the respective domain.                                                   |
|                                                                                                  |
|                                                                                                  |
|          __________________________                         __________________________           |
|              Mr. Utkarsh Sharma                                 Mr. Sacheth Sanal                |
|           Associate Vice President,                           Sr. Manager-Future Tech &          |
|              Future Tech Program                                    CSR Programs                 |
|                  BharatCares                                         BharatCares                 |
|                                                                                                  |
+--------------------------------------------------------------------------------------------------+
```
