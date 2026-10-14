# AGENTS.md — Multi-Agent Development Guide

Welcome, AI Agents! This repository is an SDET automation test framework built with **Java 17+**, **Maven**, **Playwright Java**, **REST Assured**, **Cucumber BDD**, **Serenity BDD**, and **Docker Compose**.

Please follow the rules and conventions below when inspecting, modifying, or extending this codebase.

> **Note for Agent Tooling**: This file serves as the **primary, canonical development guide** for all AI coding agents working on this project (including Claude Code, Google Gemini / Antigravity, GitHub Copilot, Windsurf, Cursor, etc.). Agent-specific files like `CLAUDE.md` and `GEMINI.md` refer back to this document.

---

## ⚡ Mandatory First Step: Read Workspace Skills Before Working

> **CRITICAL RULE FOR ALL AI AGENTS**:
> Before inspecting, creating, modifying, or refactoring any feature files, test steps, or automation code in this repository, **you MUST first read the relevant workspace skill guides** located in [`.agents/skills/`](.agents/skills/):
>
> 1. [**`cucumber-bdd-best-practices`**](.agents/skills/cucumber-bdd-best-practices/SKILL.md): Core BDD lifecycle (Discovery, Formulation, Automation), declarative vs. imperative Gherkin style, scenario outlining, Data Tables, and step definition architecture.
>    - Supporting examples: [Declarative vs Imperative](.agents/skills/cucumber-bdd-best-practices/examples/declarative_vs_imperative.feature), [Outlines & Data Tables](.agents/skills/cucumber-bdd-best-practices/examples/scenario_outline_and_datatable.feature).
>    - Supporting reference: [Three Amigos & Discovery Guide](.agents/skills/cucumber-bdd-best-practices/references/three_amigos_and_discovery.md).
>
> Do not skip this step. Adhering to these skills ensures all test scenarios are business-readable, robust, and aligned with project standards.

---

## 🧭 Repository Overview

- **Language / Runtime**: Java 17+ (Tested on OpenJDK 17, 21, and 26; containerized on OpenJDK 21 Noble)
- **Containerization**: Docker & Docker Compose (`docker compose run --rm tests`)
- **Build System**: Apache Maven 3.9+ via Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **UI Automation**: Playwright Java (headless by default)
- **API Automation**: Serenity REST / REST Assured with Jackson Databind and Hamcrest
- **BDD Framework**: Cucumber JVM 7.x with JUnit 5 Platform Suite Engine
- **Test Runner**: JUnit 5 Platform Suite (`CucumberTestRunner.java`) with Serenity Reporter
- **Configuration**: Centralized via `src/main/resources/config.properties`, `ConfigurationManager.java`, and `src/test/resources/serenity.conf`
- **Reporting**: Serenity BDD Living Documentation (`target/site/serenity/index.html`) and Cucumber HTML/JSON reports

---

## 🏗️ Project Architecture & Layout

```text
SDET-QA-Assignment/
├── .mvn/wrapper/                            # Maven wrapper files (do not delete)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/atait/assignment/
│   │   │       ├── config/
│   │   │       │   └── ConfigurationManager.java # Centralized config & property loader
│   │   │       ├── context/
│   │   │       │   └── ScenarioContext.java      # ThreadLocal cross-step data sharing
│   │   │       ├── driver/
│   │   │       │   └── PlaywrightManager.java    # ThreadLocal Playwright browser/page manager
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
│   │       └── config.properties                 # Environment configuration (base URL, timeouts)
│   └── test/
│       ├── java/
│       │   └── com/atait/assignment/
│       │       └── bdd/
│       │           ├── CucumberTestRunner.java   # JUnit 5 @Suite runner with Serenity reporter
│       │           ├── hooks/
│       │           │   └── Hooks.java            # Scenario lifecycle hooks & failure screenshot
│       │           └── stepdefinitions/
│       │               ├── ApiStepDefinitions.java       # SerenityRest API steps
│       │               ├── AuthStepDefinitions.java       # Authentication & route defense
│       │               ├── CatalogStepDefinitions.java    # Catalog fidelity & sorting
│       │               ├── CartStepDefinitions.java       # Cart badge & item manipulation
│       │               ├── CheckoutStepDefinitions.java   # Checkout boundaries & pricing
│       │               ├── NavigationStepDefinitions.java # Navigation drawer & social links
│       │               ├── PersonaStepDefinitions.java    # Persona defects & latency tests
│       │               └── WebStepDefinitions.java        # Generic web assertions
│       └── resources/
│           ├── features/                         # Gherkin .feature specifications
│           │   ├── test-coverage/
│           │   │   └── Part_0_scenario_list.md   # Test coverage matrix & ambiguity log
│           │   ├── ui-tests/
│           │   │   └── Part_1_Playwright_for_Java_project.feature # 28 Option B UI Scenarios
│           │   └── api-tests/
│           │       └── Part_2_Java_and_REST_Assured_project.feature # API Test Scenarios
│           ├── junit-platform.properties         # Cucumber glue & Serenity reporter settings
│           └── serenity.conf                     # Serenity BDD runtime configuration
├── Dockerfile                              # Container definition (Playwright Java noble base)
├── docker-compose.yml                      # Docker Compose service definition
├── docker-entrypoint.sh                    # Container entrypoint script
├── .dockerignore                           # Docker build ignore rules
├── mvnw                                    # Linux/macOS Maven wrapper
├── mvnw.cmd                                # Windows Maven wrapper
├── pom.xml                                 # Maven dependencies, Surefire & Serenity plugins
├── README.md                               # Human-facing documentation
├── AGENTS.md                               # Primary canonical agent guide (this file)
├── CLAUDE.md                               # Claude Code pointer
└── GEMINI.md                               # Gemini / Antigravity pointer
```

---

## ⚙️ Configuration Management & Zero-Hardcoding Policy

> **CRITICAL RULE FOR ALL DEVELOPERS & AGENTS**:
> **NEVER hardcode base URLs, target hostnames, ports, timeouts, or environment-specific values in Page Objects, Step Definitions, or Test classes.**

### 1. Centralized Configuration File (`config.properties`)
All application-level defaults reside in [`src/main/resources/config.properties`](src/main/resources/config.properties):
```properties
# Application Environment Configuration
app.base.url=https://www.saucedemo.com
app.timeout.ms=30000
```

### 2. Configuration Manager (`ConfigurationManager.java`)
Access configuration exclusively through [`com.atait.assignment.config.ConfigurationManager`](src/main/java/com/atait/assignment/config/ConfigurationManager.java). It automatically resolves values in hierarchical order:
1. **OS Environment Variables** (e.g., `APP_BASE_URL` overrides `app.base.url`) — ideal for Docker / CI pipelines.
2. **JVM System Properties** (e.g., `-Dapp.base.url=https://staging.saucedemo.com`) — ideal for CLI overrides.
3. **`config.properties` File** — bundled project fallback.

### 3. Usage Patterns in Code

#### ❌ Prohibited (Hardcoded URLs):
```java
// NEVER hardcode URLs in Page Objects or Step Definitions
public static final String LOGIN_URL = "https://www.saucedemo.com/";
getPage().navigate("https://www.saucedemo.com/inventory.html");
assertTrue(page.url().equals("https://www.saucedemo.com/"));
```

#### ✅ Required (Configuration Driven):
```java
// In Page Objects: Define relative routes and resolve via ConfigurationManager
public static final String LOGIN_PATH = "/";
public static final String INVENTORY_PATH = "/inventory.html";

public void open() {
    navigateTo(ConfigurationManager.getUrl(LOGIN_PATH));
}

// In Step Definitions or assertions:
String inventoryUrl = ConfigurationManager.getUrl(InventoryPage.INVENTORY_PATH);
assertTrue(page.url().equals(ConfigurationManager.getUrl("/")));
```

#### Available Helper Methods:
- `ConfigurationManager.getBaseUrl()`: Returns sanitized base URL without trailing slash (e.g., `https://www.saucedemo.com`).
- `ConfigurationManager.getUrl(String path)`: Joins base URL with a relative path (e.g., `getUrl("/cart.html")` -> `https://www.saucedemo.com/cart.html`).
- `ConfigurationManager.getProperty(String key)`: Retrieves raw property by key with env/system override.
- `ConfigurationManager.getIntProperty(String key, int defaultValue)`: Retrieves numeric property safely.

---

## ⚡ Command Cheatsheet

### 🐳 Docker Compose Execution (Zero Local Prerequisites)
Execute tests inside Docker containers without local Java or Maven:
- **Execute full test suite in container**:
  ```bash
  docker compose run --rm tests
  ```
- **Filter by tag via environment variable**:
  ```bash
  docker compose run --rm -e CUCUMBER_FILTER_TAGS="@smoke" tests
  ```
- **Filter by tag via CLI arguments**:
  ```bash
  docker compose run --rm tests -Dcucumber.filter.tags="@smoke"
  ```
- **Override target base URL**:
  ```bash
  docker compose run --rm -e APP_BASE_URL="https://staging.saucedemo.com" tests
  ```
- **Rebuild Docker image**:
  ```bash
  docker compose build
  ```

---

### 💻 Local Host Execution (Maven Wrapper)
Always use the Maven Wrapper (`.\mvnw.cmd` on Windows, `./mvnw` on Unix) to ensure consistent execution regardless of host environment.

#### Building & Compiling
- **Compile project & tests**:
  - Windows: `.\mvnw.cmd test-compile`
  - Unix: `./mvnw test-compile`

#### Running Tests on the `verify` Lifecycle
The Maven `verify` lifecycle automatically compiles the project, discovers and runs [`CucumberTestRunner`](src/test/java/com/atait/assignment/bdd/CucumberTestRunner.java), and triggers `serenity-maven-plugin:aggregate` to generate the Serenity living documentation report into `target/site/serenity/`:

- **Execute full test suite and generate Serenity report**:
  - Windows: `.\mvnw.cmd clean verify`
  - Unix: `./mvnw clean verify`

- **Execute by Cucumber tag filter on `verify`**:
  - Windows (PowerShell): `.\mvnw.cmd verify "-Dcucumber.filter.tags=@smoke"`
  - Unix (Bash): `./mvnw verify -Dcucumber.filter.tags="@smoke"`

- **Execute with dry-run mode (verifies step mapping without browser launch)**:
  - Windows: `.\mvnw.cmd verify "-Dcucumber.execution.dry-run=true"`
  - Unix: `./mvnw verify -Dcucumber.execution.dry-run=true`

#### Running Tests on the `test` Phase
- **Run Cucumber suite on `test`**:
  - Windows: `.\mvnw.cmd test`
  - Unix: `./mvnw test`

- **Filter by tags on `test`**:
  - Windows (PowerShell): `.\mvnw.cmd test "-Dcucumber.filter.tags=@regression and not @persona"`
  - Unix (Bash): `./mvnw test -Dcucumber.filter.tags="@regression and not @persona"`

#### Serenity Reporting Utilities
- **Manually aggregate Serenity HTML living documentation**:
  - Windows: `.\mvnw.cmd serenity:aggregate`
  - Unix: `./mvnw serenity:aggregate`
  - Report output: `target/site/serenity/index.html`

- **Cucumber HTML Report location**: `target/cucumber-reports/cucumber.html`

---

## 📋 Coding & Engineering Standards for Agents

1. **Avoid Hardcoding & Centralize Configuration**:
   - Store all endpoints, base URLs, and environment variables in `config.properties`.
   - Access via `ConfigurationManager`.
   - Never place raw URL strings inside test methods or step definitions.

2. **Browser Lifecycle & Playwright**:
   - Never instantiate a raw `Playwright` or `Browser` inside test step methods.
   - Use `PlaywrightManager.getPage()` to guarantee thread safety across test runs.
   - Any scenario involving browser actions must be tagged with `@ui` so `Hooks.java` cleans up resources automatically.

3. **API Testing Conventions & SerenityRest**:
   - Use `net.serenitybdd.rest.SerenityRest.given()` to construct requests. This automatically records full HTTP request and response details in Serenity reports.
   - Validate HTTP status codes, headers, and payload attributes using Hamcrest matchers (`equalTo`, `notNullValue`, `hasSize`).
   - Use Jackson POJOs or raw text blocks (`""" ... """`) for JSON payloads.

4. **Gherkin & BDD Guidelines**:
   - Place feature files in `src/test/resources/features/`.
   - Feature files must be categorized with tags: `@ui`, `@api`, `@smoke`, `@regression`.
   - Keep steps declarative, business-readable, and free from low-level technical UI selectors.
   - For complete BDD patterns, refer to the workspace skill: [`.agents/skills/cucumber-bdd-best-practices/SKILL.md`](.agents/skills/cucumber-bdd-best-practices/SKILL.md).

5. **Page Object Model (POM) Discipline**:
   - Step definitions must remain thin translation layers.
   - All locator strategies, CSS selectors, click actions, and text extractions belong strictly inside `src/main/java/com/atait/assignment/pages/`.

6. **Playwright & Serenity Dual Screenshot Strategy**:
   - Because the framework uses Playwright Java rather than Selenium WebDriver, screenshots on scenario failure are handled via [`Hooks.java`](src/test/java/com/atait/assignment/bdd/hooks/Hooks.java) using a Dual-Attachment pattern:
     1. **Playwright Full-Page Capture**: `page.screenshot(new Page.ScreenshotOptions().setFullPage(true))` captures the entire scrollable viewport.
     2. **Serenity Evidence Attachment**: Files are saved to `target/site/serenity/screenshots/` and attached as downloadable evidence via `Serenity.recordReportData().asEvidence().withTitle("Failure Screenshot").downloadable().fromFile(path)`.
     3. **Cucumber Native Attachment**: `scenario.attach(bytes, "image/png", title)` embeds the screenshot into Cucumber HTML and JSON reports.
   - Lifecycle timing: Triggered in `@AfterStep("@ui")` with fallback in `@After("@ui")`, guarded by an idempotent `ThreadLocal` flag to guarantee exactly one screenshot per failed scenario before browser cleanup.

---

## 🛠️ Step-by-Step Recipes

### Recipe 1: Adding a New Page Object
1. Create `<Name>Page.java` under `src/main/java/com/atait/assignment/pages/` extending `BasePage`.
2. Define relative route: `public static final String PATH = "/target-route.html";`.
3. Implement `open()` using `navigateTo(ConfigurationManager.getUrl(PATH));`.
4. Encapsulate all element locators as private fields.

### Recipe 2: Adding a New UI Test Scenario
1. Add the Gherkin scenario to `src/test/resources/features/ui-tests/` (tagged with `@ui` and appropriate domain/scope tags).
2. Implement step definitions under `src/test/java/com/atait/assignment/bdd/stepdefinitions/`.
3. Verify execution via the `verify` lifecycle:
   ```powershell
   .\mvnw.cmd verify "-Dcucumber.filter.tags=@ui"
   ```

### Recipe 3: Running Tests and Viewing Reports
1. Run the test suite:
   ```powershell
   # Via Docker
   docker compose run --rm tests

   # Or via local Maven Wrapper
   .\mvnw.cmd clean verify
   ```
2. Open the generated report:
   ```powershell
   Start-Process target/site/serenity/index.html
   ```

---

## 🔐 Verification Checklist

Before concluding any agent task:
- [ ] Relevant workspace skills in [`.agents/skills/`](.agents/skills/) were read and followed
- [ ] No hardcoded base URLs exist in code; all routes resolve through `ConfigurationManager` and `config.properties`
- [ ] Code compiles with `.\mvnw.cmd test-compile`
- [ ] Docker configuration (`Dockerfile`, `docker-compose.yml`, `docker-entrypoint.sh`) is maintained with LF line endings
- [ ] Relevant test suites run on `.\mvnw.cmd verify` or `docker compose run --rm tests`
- [ ] Failure screenshots are captured as full-page PNGs into `target/site/serenity/screenshots/` and embedded as evidence in `target/site/serenity/index.html`
- [ ] Gherkin feature files adhere to declarative BDD syntax standards
- [ ] Documentation (`README.md`, `AGENTS.md`) is kept up-to-date
- [ ] Sensitive/temporary artifacts (`target/`, `.serenity/`) remain excluded in `.gitignore` and `.dockerignore`
