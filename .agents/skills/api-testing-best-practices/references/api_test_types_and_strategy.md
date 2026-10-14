# API Testing Types, Risk Prioritization & CI/CD Gating Reference

This reference provides architectural depth on the seven core API testing types, risk prioritization criteria, OWASP API Top 10 vulnerabilities, and pipeline integration models.

---

## 1. Risk-Based Endpoint Prioritization

Testing everything equally results in testing everything poorly. Prioritize endpoints using this risk matrix:

```
High Impact |  Tier 2: High Traffic Endpoints  |  Tier 1: Mission-Critical Paths
            |  - Catalog search, item feeds    |  - Auth, payment gateways, checkout
            |----------------------------------+----------------------------------
Low Impact  |  Tier 4: Internal Diagnostics    |  Tier 3: Standard CRUD Utilities
            |  - Health checks, metrics        |  - User preferences, tags, comments
            +---------------------------------------------------------------------
                        Low Complexity                  High Complexity / Risk
```

### Prioritization Rules:
1. **Tier 1 (Critical Paths)**: Require full spectrum testing (Functional, Contract, Negative, Security, Performance). A failure here halts revenue or breaches user trust.
2. **Tier 2 & 3 (CRUD & High Traffic)**: Require Functional, Contract, and SLA latency assertions.
3. **Tier 4 (Internal Utilities)**: Basic smoke tests and availability checks.

---

## 2. OWASP API Security Top 10 Alignment

API automation suites must incorporate continuous automated security verification against the most common vulnerabilities:

| OWASP Vulnerability | Risk Description | Automated API Verification Strategy |
| :--- | :--- | :--- |
| **BOLA (Broken Object Level Authorization)** | User A alters resource ID to access User B's record. | Send request for Tenant A resource using Tenant B's JWT; assert `403 Forbidden` or `404 Not Found`. |
| **Broken Authentication** | Weak tokens, missing expiration, credential stuffing. | Send requests with expired, malformed, or missing tokens; assert `401 Unauthorized`. |
| **Broken Object Property Level Authorization** | Mass assignment of sensitive fields (e.g., `role: "admin"`). | Attempt `PUT`/`PATCH` injecting elevated parameters; assert forbidden status or unmutated state. |
| **Unrestricted Resource Consumption** | Denial of Service via unthrottled requests. | Loop burst requests above threshold; assert `429 Too Many Requests`. |
| **Improper Assets Management** | Deprecated API versions (v1, debug endpoints) left exposed. | Assert old endpoints are sunsetted or protected behind authorization. |

---

## 3. CI/CD Multi-Tier Pipeline Execution

Modern pipelines structure test execution to maintain short developer feedback loops while safeguarding production:

```
+-------------------------------------------------------------------+
| Stage 1: Commit / PR Fast Gate (< 3 minutes)                      |
| - Fast Functional Smoke Tests (@smoke)                            |
| - Authentication & Authorization Checks (@auth)                   |
| - OpenAPI / JSON Schema Validation (@contract)                    |
| --> Rule: Fail-fast immediately on critical path failure          |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
| Stage 2: PR Merge Gate (< 8 minutes)                              |
| - Multi-Service Integration Tests (@integration)                  |
| - State Idempotency & Lifecycle Tests (@lifecycle)                |
| - Negative & Boundary Suite (@negative)                           |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
| Stage 3: Nightly & Pre-Release Pipeline                           |
| - Full Regression Suite (Parallelized runners)                    |
| - Performance, Concurrency & SLA Load Testing (k6 / Gatling)       |
| - Dynamic Security Scans (OWASP ZAP baseline)                    |
+-------------------------------------------------------------------+
```

---

## 4. Handling Flaky Tests & Asynchronous Workflows

### The Flakiness Quarantine Protocol:
1. **Identify**: Monitor test historical trends in dashboards (e.g., Serenity BDD / Testomat.io).
2. **Quarantine**: Tag flaky tests as `@quarantine` or `@flaky` and exclude from merge-blocking gates until fixed.
3. **Root Cause Analysis**:
   - *State pollution*: Fix by dynamically creating and tearing down isolated test fixtures.
   - *Timing race conditions*: Replace arbitrary sleeps with condition polling (Awaitility).
   - *External dependency flakiness*: Use MockServer or WireMock for external service calls during CI.
