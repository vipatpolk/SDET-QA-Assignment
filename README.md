# SDET-QA-Assignment

Automation test framework featuring **Playwright Java** (UI testing), **REST Assured / Serenity REST** (API testing), and **Cucumber BDD** on **JUnit 5 Platform** with **Serenity BDD Living Documentation**, **Docker Compose**, and **Maven**.

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

## 🚀 REST API Testing (GoREST Users API)

This project contains comprehensive API test automation for the GoREST v2 Users API (`https://gorest.co.in/public/v2/users`) authored in [`Part_2_Java_and_REST_Assured_project.feature`](src/test/resources/features/api-tests/Part_2_Java_and_REST_Assured_project.feature).

### 🔑 Authentication & Environment Setup

1. **Authentication Token**: GoREST requires a Bearer token for write operations (POST, PUT, DELETE).
2. **Local Setup via `.env`**:
   - Copy `.env.example` to `.env`:\n     ```bash\n     cp .env.example .env\n     ```
   - Populate `GOREST_API_TOKEN`:
     ```env
     GOREST_API_TOKEN=your_token_here
     ```
   - `.env` is automatically ignored in Git by [`.gitignore`](.gitignore).
3. **Environment Variable Override**:
   Alternatively, set the system environment variable:
   - **Windows (PowerShell)**: `$env:GOREST_API_TOKEN="your_token_here"`
   - **macOS / Linux**: `export GOREST_API_TOKEN="your_token_here"`
4. **Fail-Fast Protection**: If the token is missing during write operations, `ConfigurationManager` fails immediately with an informative exception rather than silently skipping tests.

### 🛡️ Test Safety & Lifecycle Management (Build up > Test > Tear down)
- **Live Service Discipline**: GoREST is a public shared live service. Tests **never mutate or delete unowned records**.
- **Collision Avoidance**: [`GoRestTestDataGenerator`](src/test/java/com/atait/assignment/api/utils/GoRestTestDataGenerator.java) creates uniquely timestamped and randomized test users.
- **Guaranteed Cleanup**: Every test records created user IDs in [`ScenarioContext`](src/main/java/com/atait/assignment/context/ScenarioContext.java). The Cucumber hook `@After("@api or @gorest")` in [`Hooks.java`](src/test/java/com/atait/assignment/bdd/hooks/Hooks.java) automatically deletes all test-created users, even if an assertion fails midway.
- **Rate Limit Resilience**: [`GoRestClient`](src/test/java/com/atait/assignment/api/client/GoRestClient.java) implements exponential backoff retry when receiving HTTP `429 Too Many Requests`.

### 🧪 Executing the API Test Suite

- **Run GoREST API tests via Maven Wrapper (Windows)**:
  ```powershell
  .\mvnw.cmd test "-Dcucumber.filter.tags=@gorest"
  ```
- **Run GoREST API tests via Maven Wrapper (macOS / Linux)**:
  ```bash
  ./mvnw test -Dcucumber.filter.tags="@gorest"
  ```
- **Run GoREST API tests in Docker**:
  ```bash
  docker compose run --rm -e CUCUMBER_FILTER_TAGS="@gorest" tests
  ```

---

## 🐳 Dockerized Execution (Zero Local Java / Maven Setup)

You can run the entire test automation suite without installing Java, Maven, or browser dependencies on your host machine.

### 1. Run Full Test Suite in Docker
```bash
docker compose run --rm tests
```

### 2. Filter Scenarios by Cucumber Tag in Docker
```bash
# Run UI Smoke tests
docker compose run --rm -e CUCUMBER_FILTER_TAGS="@smoke and @ui" tests

# Run GoREST API tests
docker compose run --rm -e CUCUMBER_FILTER_TAGS="@gorest" tests
```

### 3. Clean Build & Cached Dependencies
```bash
# Rebuild the Docker image
docker compose build

# Clear Maven cache volume
docker compose down -v
```

---

## 🚀 Local Host Execution (Alternative)

### 1. Download / Install Browsers (For UI Tests)

Playwright downloads required browsers (Chromium, Firefox, WebKit) automatically on the first test run, or you can explicitly install them using the Playwright CLI:

**On Windows:**
```bash
.\mvnw.cmd exec:java -Dexec.args="install"
```

**On macOS / Linux:**
```bash
./mvnw exec:java -Dexec.args="install"
```

---

### 2. Run Tests & Generate Reports

#### Full Test Suite on Maven `verify` Lifecycle
The Maven `verify` lifecycle automatically compiles the project, runs tests via `CucumberTestRunner`, and aggregates the Serenity living documentation:

- **Run all tests and generate Serenity report**:
  - Windows: `.\mvnw.cmd clean verify`
  - Unix: `./mvnw clean verify`

- **Filter by Tag on `verify` (e.g. `@gorest` or `@smoke`)**:
  - Windows (PowerShell): `.\mvnw.cmd verify "-Dcucumber.filter.tags=@gorest"`
  - Unix (Bash): `./mvnw verify -Dcucumber.filter.tags="@gorest"`

---

### 📊 Serenity BDD Living Documentation

#### Serenity BDD Living Documentation (Recommended)
All test runs (JUnit 5, Cucumber BDD, and SerenityRest API calls) automatically output results into `target/site/serenity/`.

- **Report location**: `target/site/serenity/index.html`
- **Manually re-aggregate report**:
  ```bash
  .\mvnw.cmd serenity:aggregate
  ```
- **Open report in default browser (Windows PowerShell)**:
  ```powershell
  Start-Process target/site/serenity/index.html
  ```

#### Cucumber HTML Report
- **HTML Report**: `target/cucumber-reports/cucumber.html`
- **JSON Report**: `target/cucumber-reports/cucumber.json`

---

### 3. Playwright Tools & CLI Utilities

#### Code Generator (Playwright Codegen)
Record user actions and generate test code interactively:
```bash
.\mvnw.cmd exec:java -Dexec.args="codegen https://www.saucedemo.com"
```

#### Trace Viewer
Inspect Playwright trace files:
```bash
.\mvnw.cmd exec:java -Dexec.args="show-trace trace.zip"
```

---

## 🔄 CI/CD Pipeline (GitHub Actions — Part 3)

The repository includes CI/CD pipeline automation under `.github/workflows/`. As part of Part 3 of the assignment, the broken starter workflow ([`.github/workflows/ci-broken.yml`](.github/workflows/ci-broken.yml)) was thoroughly audited, corrected, and implemented as a production-ready pipeline in [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

### 🔍 Issues Found in `ci-broken.yml` & Their Resolutions

| # | Pipeline Component | Issue Found in `ci-broken.yml` | Technical Impact | Resolution Implemented in `ci.yml` |
| :-: | :--- | :--- | :--- | :--- |
| **1** | **Repository Checkout** | Missing `actions/checkout@v4` step completely. | Runner begins with an empty workspace; Maven fails immediately with `no POM in this directory`. | Added `actions/checkout@v4` as step 1 in all jobs. |
| **2** | **Java Version** | Specified `java-version: '8'`. | Project requires Java 17 (`pom.xml`). Playwright 1.50, Serenity 4.2, and JUnit 5 fail bytecode compilation on Java 8. | Upgraded to `java-version: '21'` (`temurin`). |
| **3** | **Playwright Dependencies** | No browser binaries or Linux shared libraries installed. | UI test runs on Ubuntu crash with `PlaywrightException: Host system is missing dependencies to run browsers`. | Added `./mvnw exec:java -Dexec.args="install --with-deps"`. |
| **4** | **API Credentials** | `GOREST_API_TOKEN` secret not passed to job environment. | Mutating API requests fail with `HTTP 401 Unauthorized` or fail-fast `IllegalStateException`. | Injected `GOREST_API_TOKEN: ${{ secrets.GOREST_API_TOKEN }}` with validation step. |
| **5** | **Failure Suppression** | `mvn clean test \|\| true` | Swallows errors and converts test failures into false greens, undermining the CI quality gate. | Removed `\|\| true` and enabled strict exit-code verification. |
| **6** | **Test Scoping** | Both steps ran `mvn clean test` without `-Dcucumber.filter.tags`. | Full test suite ran redundantly twice (UI tests in API step and vice versa). | Added `-Dcucumber.filter.tags=@ui` and `-Dcucumber.filter.tags=@gorest`. |
| **7** | **Report Deletion** | Consecutive `mvn clean test` steps. | Second step's `clean` wiped the `target/` directory and erased UI test outcomes. | Split into two isolated parallel jobs: `ui-tests` and `api-tests`. |
| **8** | **Serenity Aggregation** | Invoked `mvn test` instead of `mvn verify`. | `serenity-maven-plugin:aggregate` binds to `post-integration-test`. Living doc reports were never built. | Executed `./mvnw clean verify` to automatically aggregate Serenity HTML reports. |
| **9** | **Artifact Archival** | No artifact upload steps. | Test reports, traces, and screenshots were discarded when the runner terminated. | Added `actions/upload-artifact@v4` with `if: always()` for Serenity and Cucumber reports. |
| **10** | **Dependency Caching** | No Maven dependency cache. | Re-downloaded all dependencies from Maven Central on every push/PR run. | Configured `cache: 'maven'` in `actions/setup-java`. |
| **11** | **Maven Wrapper Portability**| Used host `mvn`. | Execution depended on runner-installed Maven version, risking discrepancies. | Standardized on `./mvnw` with `chmod +x mvnw`. |
| **12** | **Timeout & Dispatch** | Missing timeouts and manual triggers. | Jobs could hang indefinitely (up to 6 hrs); cannot run ad-hoc tests from GitHub UI. | Set `timeout-minutes` and added `workflow_dispatch` with custom tag input. |

---

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
