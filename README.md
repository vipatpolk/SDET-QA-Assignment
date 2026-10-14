# SDET-QA-Assignment

Automation test framework featuring **Playwright Java** (UI testing), **REST Assured / Serenity REST** (API testing), and **Cucumber BDD** on **JUnit 5 Platform** with **Serenity BDD Living Documentation**, **Docker Compose**, and **GitHub Actions CI/CD**.

---

## 📑 Table of Contents

- [⚡ Exact Commands to Run Test Suites](#-exact-commands-to-run-test-suites)
- [🛠️ Tech Stack & Prerequisites](#️-tech-stack--prerequisites)
- [🏗️ Architectural Decisions & Technical Rationale](#️-architectural-decisions--technical-rationale)
- [📁 Project Structure](#-project-structure)
- [🎯 Test Scenarios Selected & Rationale (Why These Scenarios?)](#-test-scenarios-selected--rationale-why-these-scenarios)
  - [UI Suite (SauceDemo — Part 0 & Part 1)](#ui-suite-saucedemo--part-0--part-1)
  - [API Suite (GoREST — Part 2)](#api-suite-gorest--part-2)
- [🔄 Part 3: CI/CD Pipeline Audit & Resolution (GitHub Actions)](#-part-3-cicd-pipeline-audit--resolution-github-actions)
- [🤖 Part 4: AI-Assisted Workflow Notes](#-part-4-ai-assisted-workflow-notes)
- [❓ Assumptions & Ambiguity Resolution](#-assumptions--ambiguity-resolution)
- [🔮 Future Improvements (What Would Be Done Differently With More Time)](#-future-improvements-what-would-be-done-differently-with-more-time)
- [🏃 Comprehensive Execution Guide](#-comprehensive-execution-guide)
  - [Option A: Dockerized Execution (Zero Local Setup)](#option-a-dockerized-execution-zero-local-setup)
  - [Option B: Local Host Execution (Maven Wrapper)](#option-b-local-host-execution-maven-wrapper)
  - [Tag Filtering Reference](#tag-filtering-reference)
- [📊 Test Reporting & Living Documentation](#-test-reporting--living-documentation)
  - [Serenity BDD Living Documentation](#serenity-bdd-living-documentation)
  - [Cucumber HTML & JSON Reports](#cucumber-html--json-reports)
- [🧰 Playwright Developer Tools](#-playwright-developer-tools)

---

## ⚡ Exact Commands to Run Test Suites

### 1. Run the UI Test Suite (Playwright + Cucumber)

- **Docker (Recommended — Zero Local Setup)**:
  ```bash
  docker compose run --rm -e CUCUMBER_FILTER_TAGS="@ui" tests
  ```
- **Windows (PowerShell with Maven Wrapper)**:
  ```powershell
  .\mvnw.cmd clean verify "-Dcucumber.filter.tags=@ui"
  ```
- **macOS / Linux (Bash with Maven Wrapper)**:
  ```bash
  ./mvnw clean verify -Dcucumber.filter.tags="@ui"
  ```

---

### 2. Run the API Test Suite (REST Assured / Serenity REST)

> **Prerequisite**: Provide your GoREST API Token in `.env` (`GOREST_API_TOKEN=your_token_here`) or via environment variable.

- **Docker (Recommended — Zero Local Setup)**:
  ```bash
  docker compose run --rm -e CUCUMBER_FILTER_TAGS="@gorest" tests
  ```
- **Windows (PowerShell with Maven Wrapper)**:
  ```powershell
  .\mvnw.cmd clean verify "-Dcucumber.filter.tags=@gorest"
  ```
- **macOS / Linux (Bash with Maven Wrapper)**:
  ```bash
  ./mvnw clean verify -Dcucumber.filter.tags="@gorest"
  ```

---

### 3. Run the Entire Test Suite (UI + API Combined)

- **Docker**:
  ```bash
  docker compose run --rm tests
  ```
- **Windows (PowerShell)**:
  ```powershell
  .\mvnw.cmd clean verify
  ```
- **macOS / Linux (Bash)**:
  ```bash
  ./mvnw clean verify
  ```

---

## 🛠️ Tech Stack & Prerequisites

- **Option A (Containerized — Zero Local Setup Required)**:
  - [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) v2+
- **Option B (Local Host Setup)**:
  - **Java**: JDK 17 or newer (Tested on OpenJDK 17, 21, 25, and 26)
  - **Build Tool**: Maven 3.9+ (A Maven Wrapper `mvnw` / `mvnw.cmd` is included)
- **Frameworks & Libraries**:
  - [Cucumber BDD](https://cucumber.io/) — Behavior-Driven Development with Gherkin
  - [Playwright for Java](https://playwright.dev/java/) — Fast and reliable end-to-end web testing
  - [Serenity BDD](https://serenity-bdd.info/) — Living documentation and rich test execution reporting
  - [REST Assured & SerenityRest](https://rest-assured.io/) — RESTful API testing & validation with automated reporting
  - [Jackson Databind](https://github.com/FasterXML/jackson) — Strongly-typed JSON serialization & deserialization (POJOs)
  - [Hamcrest](http://hamcrest.org/) — Flexible matchers for API assertions
  - [JUnit 5 Jupiter / Platform](https://junit.org/junit5/) — Modern test runner and suite execution

---

## 🏗️ Architectural Decisions & Technical Rationale

| Decision | Selection | Reasoning & Technical Benefits |
| :--- | :--- | :--- |
| **UI Automation Tool** | **Playwright for Java** | Selected over Selenium WebDriver due to **native auto-waiting** (eliminates brittle explicit/implicit wait anti-patterns), significantly faster execution speed via the Chrome DevTools Protocol (CDP), isolated browser contexts, built-in network inspection, and first-class developer tooling (`codegen`, `show-trace`). |
| **BDD & Living Documentation** | **Cucumber JVM 7 + Serenity BDD 4** | Cucumber provides domain-level, business-readable specifications (Gherkin) that align engineering, QA, and product stakeholders. Serenity BDD wraps Cucumber and REST Assured into an interactive, visual "Living Documentation" report detailing steps, screenshots on failure, and full HTTP request/response payloads without custom reporting boilerplate. |
| **Test Runner Engine** | **JUnit 5 Platform Suite (`@Suite`)** | JUnit 5 is the modern Java test platform offering modular architecture, native configuration parameters, dynamic test execution, and robust compatibility with Maven Surefire/Failsafe and CI environments. |
| **Thread-Safe Driver Architecture** | **ThreadLocal `PlaywrightManager`** | Encapsulates `Playwright`, `Browser`, and `Page` instances in `ThreadLocal` variables. Guarantees complete session isolation when running concurrent tests, prevents cross-thread state pollution, and ensures deterministic teardown in `@After` hooks. |
| **API Client Architecture** | **SerenityRest + POJOs (Jackson)** | Rather than unstructured string parsing or generic `Map<String, Object>` payloads, the framework uses strongly-typed POJOs (`UserRequest`, `UserResponse`, `ValidationErrorItem`, `ApiErrorResponse`). This enforces compile-time schema validation, prevents typo bugs in JSON fields, and simplifies assertion authoring. |
| **Live API State Safety** | **Build up > Test > Tear down Pattern** | The GoREST API is a public shared sandbox subject to outside modification. Rather than relying on fragile pre-existing static records, every mutating scenario provisions a dynamically generated unique user (`GoRestTestDataGenerator`), executes assertions, and guarantees teardown via ScenarioContext tracking in `@After("@api or @gorest")`. |
| **Rate Limit Resilience** | **Exponential Backoff Retry in `GoRestClient`** | GoREST rate limits public requests (HTTP `429 Too Many Requests`). `GoRestClient` intercepts `429` responses and automatically applies exponential backoff delays with jitter before failing, drastically reducing test flakiness on shared endpoints. |
| **Configuration Hierarchy** | **`ConfigurationManager` (Layered Resolution)** | Environment settings follow a disciplined priority chain: **System Properties (`-D`)** > **OS Environment Variables** > **`.env` file** > **`config.properties` defaults**. Sensitive tokens (`GOREST_API_TOKEN`) are never hardcoded and fail fast if missing for write operations. |

---

## 📁 Project Structure

```text
SDET-QA-Assignment/
├── .agents/skills/                          # Multi-agent specialized skills & best practices
│   ├── cucumber-bdd-best-practices/         # BDD & Gherkin authoring skill
│   └── api-testing-best-practices/          # REST Assured & API testing strategy skill
├── .github/workflows/                       # GitHub Actions CI/CD workflows
│   ├── ci.yml                               # Production CI test pipeline (Resolved Part 3)
│   └── ci-broken.yml                        # Starter broken pipeline (Part 3 analysis target)
├── .mvn/wrapper/                            # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/atait/assignment/
│   │   │       ├── config/
│   │   │       │   └── ConfigurationManager.java # Centralized config & property loader (.env & system vars)
│   │   │       ├── context/
│   │   │       │   └── ScenarioContext.java      # ThreadLocal cross-step data sharing
│   │   │       ├── driver/
│   │   │       │   └── PlaywrightManager.java    # Thread-safe Playwright browser/page manager
│   │   │       └── pages/                        # Page Object Models
│   │   │           ├── BasePage.java             # Common browser interactions & waits
│   │   │           ├── LoginPage.java            # Login page object
│   │   │           ├── InventoryPage.java        # Product catalog page object
│   │   │           ├── ProductDetailsPage.java   # Item detail subpage object
│   │   │           ├── CartPage.java             # Shopping cart page object
│   │   │           ├── CheckoutStepOnePage.java  # Customer info checkout form
│   │   │           ├── CheckoutStepTwoPage.java  # Order overview summary & pricing
│   │   │           ├── CheckoutCompletePage.java # Order confirmation page
│   │   │           └── components/
│   │   │               ├── NavigationDrawerComponent.java # Sidebar drawer
│   │   │               └── FooterComponent.java           # Social media links
│   │   └── resources/
│   │       └── config.properties                 # Environment configuration (base URL, timeouts, api.base.url)
│   └── test/
│       ├── java/
│       │   └── com/atait/assignment/
│       │       ├── api/                          # Reusable API Service Layer (GoREST)
│       │       │   ├── client/
│       │       │   │   └── GoRestClient.java     # Singleton REST client with retry & error logging
│       │       │   ├── models/                   # Strongly typed POJOs
│       │       │   │   ├── UserRequest.java      # User creation & update request POJO
│       │       │   │   ├── UserResponse.java     # User response POJO
│       │       │   │   ├── ValidationErrorItem.java # Validation error detail POJO
│       │       │   │   └── ApiErrorResponse.java # Generic error POJO
│       │       │   └── utils/
│       │       │       └── GoRestTestDataGenerator.java # Dynamic collision-free data generator
│       │       └── bdd/
│       │           ├── CucumberTestRunner.java   # JUnit 5 @Suite runner with Serenity reporter
│       │           ├── hooks/
│       │           │   └── Hooks.java            # Lifecycle hooks (screenshots & API teardown)
│       │           └── stepdefinitions/
│       │               ├── GoRestApiStepDefinitions.java # GoREST Users API step definitions
│       │               ├── AuthStepDefinitions.java       # Authentication & route defense
│       │               ├── CatalogStepDefinitions.java    # Catalog fidelity & sorting
│       │               ├── CartStepDefinitions.java       # Cart badge & item manipulation
│       │               ├── CheckoutStepDefinitions.java   # Checkout boundaries & pricing
│       │               ├── NavigationStepDefinitions.java # Navigation drawer & social links
│       │               ├── PersonaStepDefinitions.java    # Persona defects & latency tests
│       │               └── WebStepDefinitions.java        # Generic web assertions
│       └── resources/
│           ├── features/                         # Gherkin feature files
│           │   ├── test-coverage/
│           │   │   └── Part_0_scenario_list.md   # Test coverage matrix & ambiguity log
│           │   ├── ui-tests/
│           │   │   └── Part_1_Playwright_for_Java_project.feature # 28 Option B UI Scenarios
│           │   └── api-tests/
│           │       └── Part_2_Java_and_REST_Assured_project.feature # 12 Scenarios (31 permutations) GoREST API
│           ├── junit-platform.properties         # Cucumber reporting & glue settings
│           └── serenity.conf                     # Serenity BDD runtime configuration
├── Dockerfile                              # Container definition (Playwright Java noble base)
├── docker-compose.yml                      # Docker Compose service definition
├── docker-entrypoint.sh                    # Container entrypoint script
├── .dockerignore                           # Docker build ignore rules
├── .env.example                            # Example environment variables template
├── mvnw                                    # Maven wrapper script (macOS/Linux)
├── mvnw.cmd                                # Maven wrapper script (Windows)
├── pom.xml                                 # Dependencies & build configuration
├── AGENTS.md                               # Canonical agent development guide
└── README.md
```

---

## 🎯 Test Scenarios Selected & Rationale (Why These Scenarios?)

### UI Suite (SauceDemo — Part 0 & Part 1)

Detailed scenario mapping is defined in [`Part_0_scenario_list.md`](src/test/resources/features/test-coverage/Part_0_scenario_list.md) and automated in [`Part_1_Playwright_for_Java_project.feature`](src/test/resources/features/ui-tests/Part_1_Playwright_for_Java_project.feature). 

#### 1. Why Test Across 4 Diverse Personas?
Most UI test suites only exercise `standard_user`. Testing SauceDemo thoroughly requires verifying real-world defect and degradation modes:
- **`standard_user`**: Verifies the baseline golden shopping flow from login to checkout completion.
- **`locked_out_user`**: Verifies security boundary enforcement; user must be rejected with explicit error banner and forbidden from accessing protected inventory pages.
- **`problem_user`**: Verifies system resilience when encountering known client-side defects (broken product image assets, item link misrouting to ID 5, and blocked checkout input).
- **`performance_glitch_user`**: Tests framework wait mechanisms under real simulated 5000ms network/rendering latency, proving tests do not fail with premature timeouts.

#### 2. Why Authentication & Route Defense?
- Validates missing credentials (blank username, blank password) and invalid credentials.
- Validates unauthenticated direct URL navigation (e.g. browsing directly to `/inventory.html`, `/cart.html`, or `/checkout-step-one.html` without logging in). Ensures route guards redirect to `/` with an informative error.

#### 3. Why Product Catalog Sorting & Item Detail Fidelity?
- Sorting (A-Z, Z-A, Price Low-to-High, Price High-to-Low) exercises DOM mutation and data-order integrity.
- Verifies that navigating from catalog to an item's detail page preserves title, description, and price without data truncation.

#### 4. Why Cart Badge State Dynamics & Reset App State?
- Boundary check on badge dynamics: incrementing counter on add, decrementing on remove, and verifying the badge element is completely removed from DOM when 0 items remain (instead of showing `0`).
- Validates cross-page state persistence and the "Reset App State" sidebar drawer action.

#### 5. Why End-to-End Checkout Boundaries & Calculation Math?
- Step One boundary testing: enforces required field errors (First Name required, Last Name required, Postal Code required).
- Step Two financial calculation: explicitly calculates and validates `Item Total + Tax = Total Amount` to prevent billing discrepancies.

---

### API Suite (GoREST — Part 2)

Automated in [`Part_2_Java_and_REST_Assured_project.feature`](src/test/resources/features/api-tests/Part_2_Java_and_REST_Assured_project.feature).

#### 1. Why Schema Contract & SLA Response Time?
- Contract verification ensures JSON attributes match expected data types (`id` is integer, `name` is string, `email` is valid format, `gender` in `[male, female]`, `status` in `[active, inactive]`).
- Response time SLA assertion (`< 2500ms`) catches latency spikes and upstream regressions early.

#### 2. Why Full Lifecycle (CRUD) Isolation?
- Covers all HTTP methods: `POST` (create), `GET` (retrieve), `PUT` (full replacement), `PATCH` (partial update), `DELETE` (removal).
- Guarantees zero residual test pollution on public shared GoREST endpoints.

#### 3. Why Negative & Boundary Permutations?
- Validates data validation error models (`422 Unprocessable Entity`):
  - Missing mandatory fields.
  - Invalid email formats (missing `@`, missing domain).
  - Invalid enum values for `gender` and `status`.
  - Duplicate email collision rejection.
- Validates resource not found (`404 Not Found`) when requesting non-existent IDs.

#### 4. Why Authentication Boundary Validation?
- Exercises security defense: verifies that mutating requests (`POST`, `PUT`, `DELETE`) without an Authorization header fail immediately with `401 Unauthorized`.

---

## 🔄 Part 3: CI/CD Pipeline Audit & Resolution (GitHub Actions)

As part of Part 3 of the assignment, the broken starter workflow ([`.github/workflows/ci-broken.yml`](.github/workflows/ci-broken.yml)) was thoroughly audited, corrected, and implemented as a production-ready pipeline in [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

- **Example Pipeline Run**: https://github.com/vipatpolk/SDET-QA-Assignment/actions/runs/38588835577

### 🔍 Issues Found in `ci-broken.yml` & Their Resolutions

| # | Pipeline Component | Issue Found in `ci-broken.yml` | Technical Impact | Resolution Implemented in `ci.yml` |
| :-: | :--- | :--- | :--- | :--- |
| **1** | **Repository Checkout** | Missing `actions/checkout@v4` step completely. | Runner begins with an empty workspace; Maven fails immediately with `no POM in this directory`. | Added `actions/checkout@v4` as step 1 in all jobs. |
| **2** | **Java Version** | Specified `java-version: '8'`. | Project requires Java 17 (`pom.xml`). Playwright 1.50, Serenity 4.2, and JUnit 5 fail bytecode compilation on Java 8. | Upgraded to `java-version: '21'` (`temurin`). |
| **3** | **Playwright Dependencies** | No browser binaries or Linux shared libraries installed. | UI test runs on Ubuntu crash with `PlaywrightException: Host system is missing dependencies to run browsers`. | Added `./mvnw exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"` and configured `exec-maven-plugin` in `pom.xml`. |
| **4** | **API Credentials** | `GOREST_API_TOKEN` secret not passed to job environment. | Mutating API requests fail with `HTTP 401 Unauthorized` or fail-fast `IllegalStateException`. | Injected `GOREST_API_TOKEN: ${{ secrets.GOREST_API_TOKEN }}` with validation step. |
| **5** | **Failure Suppression** | `mvn clean test \|\| true` | Swallows errors and converts test failures into false greens, undermining the CI quality gate. | Removed `\|\| true` and enabled strict exit-code verification. |
| **6** | **Test Scoping** | Both steps ran `mvn clean test` without `-Dcucumber.filter.tags`. | Full test suite ran redundantly twice (UI tests in API step and vice versa). | Added `-Dcucumber.filter.tags=@ui` and `-Dcucumber.filter.tags=@gorest`. |
| **7** | **Report Deletion** | Consecutive `mvn clean test` steps. | Second step's `clean` wiped the `target/` directory and erased UI test outcomes. | Split into two isolated parallel jobs: `ui-tests` and `api-tests`. |
| **8** | **Serenity Aggregation** | Invoked `mvn test` instead of `mvn verify`. | `serenity-maven-plugin:aggregate` binds to `post-integration-test`. Living doc reports were never built. | Executed `./mvnw clean verify` to automatically aggregate Serenity HTML reports. |
| **9** | **Artifact Archival** | No artifact upload steps. | Test reports, traces, and screenshots were discarded when the runner terminated. | Added `actions/upload-artifact@v4` with `if: always()` for Serenity and Cucumber reports. |
| **10** | **Dependency Caching** | No Maven dependency cache. | Re-downloaded all dependencies from Maven Central on every push/PR run. | Configured `cache: 'maven'` in `actions/setup-java`. |
| **11** | **Maven Wrapper Portability**| Used host `mvn`. | Execution depended on runner-installed Maven version, risking discrepancies. | Standardized on `./mvnw` with `chmod +x mvnw`. |
| **12** | **Timeout & Dispatch** | Missing timeouts and manual triggers. | Jobs could hang indefinitely (up to 6 hrs); cannot run ad-hoc tests from GitHub UI. | Set `timeout-minutes` and added `workflow_dispatch` with custom tag input. |

### ⚙️ Production CI Pipeline Architecture (`.github/workflows/ci.yml`)

The resolved workflow leverages **two parallel jobs** for speed, isolation, and clarity:

```mermaid
flowchart TD
    Trigger([Trigger: Push / PR to main / Manual Dispatch]) --> UIJob[Job 1: ui-tests]
    Trigger --> APIJob[Job 2: api-tests]

    subgraph UIJob [Job 1: UI Automation - Playwright]
        A1[actions/checkout@v4] --> A2[setup-java JDK 21 + Maven Cache]
        A2 --> A3[chmod +x mvnw]
        A3 --> A4[Install Playwright Browsers & Deps]
        A4 --> A5[./mvnw clean verify -Dcucumber.filter.tags=@ui]
        A5 --> A6[Upload Serenity UI Living Docs]
        A6 --> A7[Upload Cucumber UI Reports]
    end

    subgraph APIJob [Job 2: API Automation - REST Assured]
        B1[actions/checkout@v4] --> B2[setup-java JDK 21 + Maven Cache]
        B2 --> B3[chmod +x mvnw]
        B3 --> B4[Validate GOREST_API_TOKEN Secret]
        B4 --> B5[./mvnw clean verify -Dcucumber.filter.tags=@gorest]
        B5 --> B6[Upload Serenity API Living Docs]
        B6 --> B7[Upload Cucumber API Reports]
    end
```

### 🔐 Configuring GitHub Actions Secrets
To run authenticated API tests in GitHub Actions:
1. Navigate to **Repository Settings** ➔ **Secrets and variables** ➔ **Actions**.
2. Click **New repository secret**.
3. Set **Name**: `GOREST_API_TOKEN`.
4. Set **Value**: Your GoREST Bearer Token (obtainable from [gorest.co.in](https://gorest.co.in/)).
5. Click **Add secret**.

---

## 🤖 Part 4: AI-Assisted Workflow Notes

I start this assignment by deciding which tech stacks to use and defining a project structure. Then an AI tool comes in; the one I use is Gemini 3.8 Flash integrated with the IDE (I’ve used IntelliJ).

Before starting development, I provide it sufficient skills such as BDD and API testing best practices. And I give it a strict workflow, telling it to always read the guide and skills before starting.

In the development part, I act like a Product Owner and provide test scenarios and requirements, letting the AI plan the implementation.

Once I get a plan, I review it, make appropriate adjustments, then start development. The coding part is done by AI.

However, there are some risky cases where code is done by AI. For example, the AI sometimes does not think about reusable or dynamic coding.

Hardcoded code or code that only runs in a specific scenario can be produced. But this can be prevented or reduced by giving it golden rules within the guide.

AI also helps me analyze existing logic errors, since detecting issues caused by someone else is somewhat challenging.

One thing I deliberately do not delegate to AI is Git branch management, especially merging actions, because it is easy to make mistakes and when they happen it is hard to resolve them.

---

## ❓ Assumptions & Ambiguity Resolution

During test analysis and automation, several undocumented behaviors and edge cases were identified. Below are the assumptions and engineering decisions applied:

| # | Ambiguous Area | Observed Behavior / Ambiguity | Assumption & Decision Applied |
| :-: | :--- | :--- | :--- |
| **1** | **Empty Cart Checkout ($0.00)** | SauceDemo allows advancing through checkout and placing an order with 0 items for $0.00 without any warning or validation block. | **Assumption**: In a real e-commerce platform, this is an omission. For this assignment, the framework documents this behavior in Part 0 (`TC-08`), asserting the actual application state without masking it, while flagging it for product clarification. |
| **2** | **Account Lockout Duration** | SauceDemo displays `Epic sadface: Sorry, this user has been locked out.` but does not specify lockout timeout or cooldown. | **Assumption**: Lockout is treated as permanent for `locked_out_user`. No cooldown timers or unlock attempts are triggered. |
| **3** | **Error Prefix Inconsistency** | Login page errors prefix with `Epic sadface:`, whereas Checkout Step One form errors prefix with `Error:`. | **Assumption**: Validated each error string against its actual page-specific convention rather than assuming a universal global prefix. |
| **4** | **Input Lengths & Postal Code Format** | Checkout input fields have no HTML `maxlength` attributes or postal code regex validation (accepting arbitrary strings and injection probe characters). | **Assumption**: The framework exercises international postal formats (`SW1A 1AA`, `90210-1234`) and verifies non-ASCII strings (`José-María`) are sanitized and do not trigger application 500 errors. |
| **5** | **`problem_user` Intentional Defects** | `problem_user` has intentional broken links, corrupted images, and blocks typing into "Last Name". | **Assumption**: Automated tests assert these specific defect states explicitly to verify defect-detection fidelity rather than letting them fail as unhandled test errors. |
| **6** | **GoREST Shared Environment Volatility** | GoREST public records are subject to unexpected edits, deletions, or external rate limiting (429). | **Assumption**: Tests never depend on persistent records or static IDs. Every test dynamically provisions its own user, tracks the ID in `ScenarioContext`, and cleans it up via `@After` hooks. |
| **7** | **Error Prefix Emoji Normalization** | SauceDemo prefixes login errors with `Epic sadface:` (`Epic sadface: Username is required`). | **Assumption**: In Gherkin feature scenarios ([`Part_1_Playwright_for_Java_project.feature`](src/test/resources/features/ui-tests/Part_1_Playwright_for_Java_project.feature)), `Epic sadface` is normalized to the `☹️` emoji prefix (`☹️: Username is required`, `☹️: Sorry, this user has been locked out.`). Step definitions bidirectionally accept both `☹️` and `Epic sadface` for clean BDD reporting. |
| **8** | **Product Brand Normalization** | The red shirt on SauceDemo is named `Test.allTheThings() T-Shirt (Red)`. | **Assumption**: Mapped and normalized to `'Sauce Labs'` (`☹Sauce Labs T-Shirt (Red)`) across catalog feature outlines to ensure uniform branding consistency across all inventory items. |
| **9** | **Product Description Normalization** | The Sauce Labs Backpack description begins with `carry.allTheThings() with the sleek...`. | **Assumption**: In customer-facing BDD scenarios, the programmatic camelCase phrase `carry.allTheThings()` is normalized to title case `'Carry All The Things'` for clean readability. |

---

## 🔮 Future Improvements (What Would Be Done Differently With More Time)

If allocated additional development time, the following enhancements would be added to elevate the test suite:

1. **Visual Regression Testing**:
   - Integrate Playwright's pixel-by-pixel screenshot comparison (`expect(page).toHaveScreenshot()`) to detect subtle layout shifts, broken CSS, and corrupted assets (such as the `problem_user` dog avatars) across various responsive viewports.
2. **Multi-Browser & Cross-Device Execution Matrix**:
   - Extend GitHub Actions to run a parallel matrix across **Chromium**, **Firefox**, and **WebKit** (Safari engine), along with mobile emulated viewports (Pixel 7, iPhone 15).
3. **API Mocking & Fault Injection (WireMock / MockServer)**:
   - Introduce a local WireMock container to simulate downstream service failures, high HTTP latency, 502/503 bad gateway conditions, and verify client retry logic deterministically without relying on GoREST's live uptime.
4. **Contract Testing with Pact**:
   - Implement consumer-driven contract tests using [Pact](https://docs.pact.io/) to catch API schema breakages before deployments reach staging.
5. **Performance / Load Testing Integration**:
   - Add automated performance test stages using **k6** or **Gatling** to benchmark response time SLAs under high concurrency against critical endpoints.
6. **Allure Reporting Integration**:
   - Supplement Serenity Living Documentation with Allure reports and historical trend dashboards for long-term flaky test detection and test run analytics.

---

## 🏃 Comprehensive Execution Guide

### Option A: Dockerized Execution (Zero Local Setup)

You can run the entire test automation suite without installing Java, Maven, or browser dependencies on your host machine.

#### 1. Run Full Test Suite in Docker
```bash
docker compose run --rm tests
```

#### 2. Filter Scenarios by Cucumber Tag in Docker
```bash
# Run UI Smoke tests
docker compose run --rm -e CUCUMBER_FILTER_TAGS="@smoke and @ui" tests

# Run GoREST API tests
docker compose run --rm -e CUCUMBER_FILTER_TAGS="@gorest" tests
```

#### 3. Rebuild or Clear Cached Dependencies
```bash
# Rebuild the Docker image
docker compose build

# Clear Maven cache volume
docker compose down -v
```

---

### Option B: Local Host Execution (Maven Wrapper)

#### 1. Download / Install Browsers (One-time setup for UI tests)
Playwright downloads required browsers automatically on the first run, or you can install them explicitly:
- **Windows**: `.\mvnw.cmd exec:java -Dexec.args="install"`
- **macOS / Linux**: `./mvnw exec:java -Dexec.args="install"`

#### 2. Run Tests via Maven Verify
The Maven `verify` lifecycle compiles the project, runs tests via `CucumberTestRunner`, and aggregates Serenity living documentation:

- **Run all tests and aggregate reports**:
  - Windows: `.\mvnw.cmd clean verify`
  - macOS / Linux: `./mvnw clean verify`

---

### Tag Filtering Reference

Execute specific subsets of tests using `-Dcucumber.filter.tags`:

| Target Test Suite | Windows (PowerShell) | macOS / Linux (Bash) |
| :--- | :--- | :--- |
| **All UI Tests** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@ui"` | `./mvnw verify -Dcucumber.filter.tags="@ui"` |
| **All API Tests** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@gorest"` | `./mvnw verify -Dcucumber.filter.tags="@gorest"` |
| **Smoke Suite (UI & API)** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@smoke"` | `./mvnw verify -Dcucumber.filter.tags="@smoke"` |
| **Auth Scenarios** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@auth"` | `./mvnw verify -Dcucumber.filter.tags="@auth"` |
| **Regression Suite** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@regression"` | `./mvnw verify -Dcucumber.filter.tags="@regression"` |

---

## 📊 Test Reporting & Living Documentation

### Serenity BDD Living Documentation

All test runs (JUnit 5, Cucumber BDD, and SerenityRest API calls) automatically output rich living documentation into `target/site/serenity/`.

- **HTML Report Location**: `target/site/serenity/index.html`
- **Manually re-aggregate reports**:
  ```bash
  .\mvnw.cmd serenity:aggregate
  ```
- **Open report in default browser (Windows PowerShell)**:
  ```powershell
  Start-Process target/site/serenity/index.html
  ```

### Cucumber HTML & JSON Reports
- **Cucumber HTML Report**: `target/cucumber-reports/cucumber.html`
- **Cucumber JSON Report**: `target/cucumber-reports/cucumber.json`

---

## 🧰 Playwright Developer Tools

### Code Generator (Playwright Codegen)
Record user actions and generate test locators interactively:
```bash
.\mvnw.cmd exec:java -Dexec.args="codegen https://www.saucedemo.com"
```

### Trace Viewer
Inspect execution traces, DOM snapshots, network requests, and action timelines:
```bash
.\mvnw.cmd exec:java -Dexec.args="show-trace trace.zip"
```
