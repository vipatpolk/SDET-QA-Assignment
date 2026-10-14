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
> 2. [**`api-testing-best-practices`**](.agents/skills/api-testing-best-practices/SKILL.md): Core API testing strategy, the "API Test Trapezoid" model, seven API testing types, zero-hardcoding configuration, response time SLA assertions, and REST Assured / Serenity REST implementation patterns.
>    - Supporting examples: [Declarative API Scenarios](.agents/skills/api-testing-best-practices/examples/declarative_api_scenarios.feature), [RestAssured Step Examples](.agents/skills/api-testing-best-practices/examples/RestAssuredStepExamples.java).
>    - Supporting reference: [API Testing Types & Strategy](.agents/skills/api-testing-best-practices/references/api_test_types_and_strategy.md).
>
> Do not skip this step. Adhering to these skills ensures all test scenarios are business-readable, robust, and aligned with project standards.

---

## 🧭 Repository Overview

- **Language / Runtime**: Java 17+ (Tested on OpenJDK 17, 21, 25, and 26; containerized on OpenJDK 21 Noble)
- **Containerization**: Docker & Docker Compose (`docker compose run --rm tests`)
- **Build System**: Apache Maven 3.9+ via Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **UI Automation**: Playwright Java (headless by default)
- **API Automation**: Serenity REST / REST Assured with Jackson Databind and Hamcrest
- **BDD Framework**: Cucumber JVM 7.x with JUnit 5 Platform Suite Engine
- **Test Runner**: JUnit 5 Platform Suite (`CucumberTestRunner.java`) with Serenity Reporter
- **Configuration**: Centralized via `src/main/resources/config.properties`, `ConfigurationManager.java`, `.env` / environment variables, and `src/test/resources/serenity.conf`
- **Reporting**: Serenity BDD Living Documentation (`target/site/serenity/index.html`) and Cucumber HTML/JSON reports

---

## 🏗️ Project Architecture & Layout

```text
SDET-QA-Assignment/
├── .agents/skills/
│   ├── cucumber-bdd-best-practices/         # BDD & Gherkin authoring skill
│   └── api-testing-best-practices/          # REST Assured & API testing strategy skill
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
│   │       └── config.properties                 # Environment configuration (base URL, timeouts, api.base.url)
│   └── test/
│       ├── java/
│       │   └── com/atait/assignment/
│       │       ├── api/                          # Reusable API Service Layer (GoREST & REST Assured)
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
│       │           │   └── Hooks.java            # Scenario lifecycle hooks, teardown & screenshots
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
│           ├── features/                         # Gherkin .feature specifications
│           │   ├── test-coverage/
│           │   │   └── Part_0_scenario_list.md   # Test coverage matrix & ambiguity log
│           │   ├── ui-tests/
│           │   │   └── Part_1_Playwright_for_Java_project.feature # 28 Option B UI Scenarios
│           │   └── api-tests/
│           │       └── Part_2_Java_and_REST_Assured_project.feature # GoREST API Scenarios
│           ├── junit-platform.properties         # Cucumber glue & Serenity reporter settings
│           └── serenity.conf                     # Serenity BDD runtime configuration
├── Dockerfile                              # Container definition (Playwright Java noble base)
├── docker-compose.yml                      # Docker Compose service definition
├── docker-entrypoint.sh                    # Container entrypoint script
├── .dockerignore                           # Docker build ignore rules
├── .env.example                            # Template for local environment variables & tokens
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
> **NEVER hardcode base URLs, target hostnames, ports, timeouts, or API credentials in Page Objects, Step Definitions, or Test classes.**

### 1. Centralized Configuration File (`config.properties`)
All application-level defaults reside in [`src/main/resources/config.properties`](src/main/resources/config.properties):
```properties
# Application Environment Configuration
app.base.url=https://www.saucedemo.com
app.timeout.ms=30000

# REST API Environment Configuration
api.base.url=https://gorest.co.in/public/v2
gorest.api.token=
```

### 2. Configuration Manager (`ConfigurationManager.java`)
Access configuration exclusively through [`com.atait.assignment.config.ConfigurationManager`](src/main/java/com/atait/assignment/config/ConfigurationManager.java). It automatically resolves values in hierarchical order:
1. **OS Environment Variables** (e.g., `GOREST_API_TOKEN`, `APP_BASE_URL`) — ideal for Docker / CI pipelines.
2. **Local `.env` File** — automatically loaded if present at the project root (git-ignored for security).
3. **JVM System Properties** (e.g., `-Dgorest.api.token=...`) — ideal for CLI overrides.
4. **`config.properties` File** — bundled project fallback.

### 3. API Token Handling & Safe Service Usage
- **Credentials Protection**: The API Bearer token MUST never be committed to Git. A `.env.example` file is provided as a template.
- **Fail Fast**: `ConfigurationManager.getGoRestToken()` immediately throws an `IllegalStateException` with clear diagnostic instructions if the token is missing for write operations.
- **Build up > Test > Tear down**: Every mutating test creates only uniquely named records (`GoRestTestDataGenerator`), tracks created IDs in `ScenarioContext`, and guarantees deletion of its own test users in the `@After("@api or @gorest")` hook in `Hooks.java`. Tests NEVER modify or delete unowned records on the live GoREST service.
- **Rate Limiting**: `GoRestClient` handles HTTP 429 responses with exponential backoff retry.

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
  docker compose run --rm -e CUCUMBER_FILTER_TAGS="@gorest" tests
  ```
- **Run API tests with custom token**:
  ```bash
  docker compose run --rm -e GOREST_API_TOKEN="your_token_here" -e CUCUMBER_FILTER_TAGS="@gorest" tests
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

- **Execute GoREST API tests on `verify`**:
  - Windows (PowerShell): `.\mvnw.cmd verify "-Dcucumber.filter.tags=@gorest"`
  - Unix (Bash): `./mvnw verify -Dcucumber.filter.tags="@gorest"`

- **Execute UI tests on `verify`**:
  - Windows (PowerShell): `.\mvnw.cmd verify "-Dcucumber.filter.tags=@ui"`
  - Unix (Bash): `./mvnw verify -Dcucumber.filter.tags="@ui"`

#### Running Tests on the `test` Phase
- **Run GoREST API tests on `test` phase**:
  - Windows (PowerShell): `.\mvnw.cmd test "-Dcucumber.filter.tags=@gorest"`
  - Unix (Bash): `./mvnw test -Dcucumber.filter.tags="@gorest"`

#### Serenity Reporting Utilities
- **Manually aggregate Serenity HTML living documentation**:
  - Windows: `.\mvnw.cmd serenity:aggregate`
  - Unix: `./mvnw serenity:aggregate`
  - Report output: `target/site/serenity/index.html`

- **Cucumber HTML Report location**: `target/cucumber-reports/cucumber.html`

---

## 📋 Coding & Engineering Standards for Agents

1. **Avoid Hardcoding & Centralize Configuration**:
   - Store all endpoints, base URLs, and environment variables in `config.properties` or `.env`.
   - Access via `ConfigurationManager`.
   - Never place raw URL strings inside test methods or step definitions.

2. **Browser Lifecycle & Playwright**:
   - Never instantiate a raw `Playwright` or `Browser` inside test step methods.
   - Use `PlaywrightManager.getPage()` to guarantee thread safety across test runs.
   - Any scenario involving browser actions must be tagged with `@ui` so `Hooks.java` cleans up resources automatically.

3. **API Testing Conventions & SerenityRest**:
   - Use `GoRestClient` service layer rather than scattered raw REST Assured calls.
   - Use strongly-typed POJOs (`UserRequest`, `UserResponse`) rather than raw string concatenation.
   - Use `net.serenitybdd.rest.SerenityRest.given()` under the hood to ensure full HTTP logging in Serenity reports.
   - Always adhere to the **Build up > Test > Tear down** cycle. Clean up created records even upon test failure.

4. **Gherkin & BDD Guidelines**:
   - Place feature files in `src/test/resources/features/`.
   - Feature files must be categorized with tags: `@ui`, `@api`, `@gorest`, `@smoke`, `@regression`, `@crud`, `@destructive`.
   - Use `Scenario Outline` with `Examples` for boundary, schema, pagination, and validation testing.
   - Keep steps declarative, business-readable, and free from low-level technical jargon.
   - For complete BDD patterns, refer to the workspace skills:
     - [`.agents/skills/cucumber-bdd-best-practices/SKILL.md`](.agents/skills/cucumber-bdd-best-practices/SKILL.md)
     - [`.agents/skills/api-testing-best-practices/SKILL.md`](.agents/skills/api-testing-best-practices/SKILL.md)

---

## 🔐 Verification Checklist

Before concluding any agent task:
- [ ] Relevant workspace skills in [`.agents/skills/`](.agents/skills/) were read and followed
- [ ] No hardcoded base URLs exist in code; all routes resolve through `ConfigurationManager` and `config.properties`
- [ ] Credentials (`.env`) are kept out of Git and protected by `.gitignore`
- [ ] Code compiles with `.\mvnw.cmd test-compile`
- [ ] Relevant test suites run on `.\mvnw.cmd test` or `.\mvnw.cmd verify`
- [ ] Gherkin feature files adhere to declarative BDD syntax standards
- [ ] Documentation (`README.md`, `AGENTS.md`) is kept up-to-date
- [ ] Sensitive/temporary artifacts (`target/`, `.serenity/`, `.env`) remain excluded in `.gitignore`
