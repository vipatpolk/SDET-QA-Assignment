# AGENTS.md — Multi-Agent Development Guide

Welcome, AI Agents! This repository is an SDET automation test framework built with **Java 17+**, **Maven**, **Playwright Java**, **REST Assured**, **Cucumber BDD**, and **Allure Report**.

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

- **Language / Runtime**: Java 17+ (Tested on OpenJDK 17, 21, and 26)
- **Build System**: Apache Maven 3.9+ via Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **UI Automation**: Playwright Java (headless by default)
- **API Automation**: REST Assured with Jackson Databind and Hamcrest
- **BDD Framework**: Cucumber JVM 7.x with JUnit 5 Platform Suite Engine
- **Test Runner**: JUnit 5 Jupiter
- **Reporting**: Allure Report (JUnit 5 + Cucumber 7 + REST Assured listeners) and Cucumber HTML/JSON reports

---

## 🏗️ Project Architecture & Layout

```text
SDET-QA-Assignment/
├── .mvn/wrapper/                            # Maven wrapper files (do not delete)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/atait/assignment/
│   │   │       └── driver/
│   │   │           └── PlaywrightManager.java  # ThreadLocal Playwright browser/page manager
│   │   └── resources/                         # Application resources
│   └── test/
│       ├── java/
│       │   └── com/atait/assignment/
│       │       ├── bdd/
│       │       │   ├── CucumberTestRunner.java     # JUnit 5 @Suite runner for Cucumber
│       │       │   ├── hooks/
│       │       │   │   └── Hooks.java              # Scenario hooks (@ui teardown & screenshot)
│       │       │   └── stepdefinitions/
│       │       │       ├── ApiStepDefinitions.java # Step defs with AllureRestAssured filter
│       │       │       └── WebStepDefinitions.java # Step defs with PlaywrightManager
│       │       ├── PlaywrightSampleTest.java       # Standalone JUnit 5 UI test
│       │       └── RestAssuredSampleTest.java      # Standalone JUnit 5 API test
│       └── resources/
│           ├── features/                           # Gherkin .feature files
│           │   ├── playwright_web.feature          # UI test scenarios
│           │   └── posts_api.feature               # API test scenarios
│           └── junit-platform.properties           # Cucumber glue & Allure plugin settings
├── mvnw                                    # Linux/macOS Maven wrapper
├── mvnw.cmd                                # Windows Maven wrapper
├── pom.xml                                 # Maven dependencies & plugins
├── README.md                               # Human-facing documentation
├── AGENTS.md                               # Primary canonical agent guide (this file)
├── CLAUDE.md                               # Claude Code pointer
└── GEMINI.md                               # Gemini / Antigravity pointer
```

---

## ⚡ Command Cheatsheet

Always use the Maven Wrapper (`.\mvnw.cmd` on Windows, `./mvnw` on Unix) to ensure consistent execution regardless of host environment.

### Building & Compiling
- **Compile project & tests**:
  - Windows: `.\mvnw.cmd test-compile`
  - Unix: `./mvnw test-compile`

### Running Tests
- **Run all tests (BDD + Standalone)**:
  - Windows: `.\mvnw.cmd clean test`
  - Unix: `./mvnw clean test`

- **Run only Cucumber BDD suite**:
  - Windows: `.\mvnw.cmd test -Dtest=CucumberTestRunner`
  - Unix: `./mvnw test -Dtest=CucumberTestRunner`

- **Run Cucumber scenarios by tag** (e.g. `@smoke`, `@regression`, `@ui`, `@api`):
  - Windows (PowerShell): `.\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@smoke"`
  - Unix (Bash): `./mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"`
  *(Note: In PowerShell, surround arguments containing dots/equals with quotes to prevent argument splitting).*

- **Run only REST Assured standalone tests**:
  - Windows: `.\mvnw.cmd test -Dtest=RestAssuredSampleTest`
  - Unix: `./mvnw test -Dtest=RestAssuredSampleTest`

- **Run only Playwright standalone tests**:
  - Windows: `.\mvnw.cmd test -Dtest=PlaywrightSampleTest`
  - Unix: `./mvnw test -Dtest=PlaywrightSampleTest`

### Playwright CLI Utilities
- **Install/update Playwright browsers**:
  - Windows: `.\mvnw.cmd exec:java -Dexec.args="install"`
  - Unix: `./mvnw exec:java -Dexec.args="install"`

- **Record tests with Codegen**:
  - Windows: `.\mvnw.cmd exec:java -Dexec.args="codegen https://example.com"`
  - Unix: `./mvnw exec:java -Dexec.args="codegen https://example.com"`

### Reports
- **Generate and serve interactive Allure Report**:
  - Windows: `.\mvnw.cmd allure:serve`
  - Unix: `./mvnw allure:serve`

- **Generate static Allure Report to `target/site/allure-maven-plugin`**:
  - Windows: `.\mvnw.cmd allure:report`
  - Unix: `./mvnw allure:report`

- **Cucumber HTML Report location**: `target/cucumber-reports/cucumber.html`

---

## 📋 Coding & Engineering Standards for Agents

1. **Browser Lifecycle & Playwright**:
   - Never instantiate a raw `Playwright` or `Browser` inside test step methods.
   - Use `PlaywrightManager.getPage()` or `PlaywrightManager.initPage()` to guarantee thread safety.
   - Any scenario involving browser actions must be tagged with `@ui` so `Hooks.java` cleans up resources automatically.

2. **API Testing Conventions & REST Assured**:
   - Always attach `new AllureRestAssured()` filter to requests or `RestAssured.filters()` so requests/responses are logged in Allure.
   - Validate HTTP status codes, headers, and payload attributes using Hamcrest matchers (`equalTo`, `notNullValue`, `hasSize`).
   - Use Jackson POJOs or raw text blocks (`""" ... """`) for JSON payloads.

3. **Gherkin & BDD Guidelines**:
   - Place feature files in `src/test/resources/features/`.
   - Feature files must be categorized with tags: `@ui`, `@api`, `@smoke`, `@regression`.
   - Keep steps declarative, clean, and reusable across scenarios.
   - For complete BDD patterns, refer to the workspace skill: [`.agents/skills/cucumber-bdd-best-practices/SKILL.md`](.agents/skills/cucumber-bdd-best-practices/SKILL.md).

4. **Allure Annotations & Attachments**:
   - When writing standalone JUnit 5 tests, use `@Epic`, `@Feature`, `@Story`, `@DisplayName`, and `@Description`.
   - On UI test failure, `Hooks.java` automatically attaches screenshots to Allure.

5. **Code Modification Rules**:
   - Preserve existing comments and docstrings unless directly superseded.
   - Do not edit binary files or Maven wrapper jars directly.
   - Always verify that code compiles and tests pass before finishing a task.

---

## 🛠️ Step-by-Step Recipes

### Recipe 1: Adding a New UI Test Scenario
1. Add the Gherkin scenario to `src/test/resources/features/playwright_web.feature` (or create a new `.feature` file). Tag it with `@ui` and appropriate tags (e.g., `@smoke`).
2. Implement or reuse step definitions in `WebStepDefinitions.java`. Always obtain the page via `PlaywrightManager.getPage()`.
3. Verify execution:
   ```powershell
   .\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@ui"
   ```

### Recipe 2: Adding a New API Test Scenario
1. Add the Gherkin scenario to `src/test/resources/features/posts_api.feature`. Tag it with `@api`.
2. Implement or reuse step definitions in `ApiStepDefinitions.java`.
3. Ensure the request has `new AllureRestAssured()` attached so HTTP traffic appears in Allure.
4. Verify execution:
   ```powershell
   .\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@api"
   ```

### Recipe 3: Generating Allure Reports
1. Run the test suite:
   ```powershell
   .\mvnw.cmd clean test
   ```
2. Generate the static HTML report:
   ```powershell
   .\mvnw.cmd allure:report
   ```
3. Report output is stored in `target/site/allure-maven-plugin/index.html`.

---

## 🔒 Verification Checklist

Before concluding any agent task:
- [ ] Relevant workspace skills in [`.agents/skills/`](.agents/skills/) were read and followed
- [ ] Code compiles with `.\mvnw.cmd test-compile`
- [ ] Relevant test suites pass with `.\mvnw.cmd test`
- [ ] Gherkin feature files adhere to syntax standards
- [ ] Documentation (`README.md`, `AGENTS.md`) is kept up-to-date
- [ ] Sensitive/temporary artifacts (`allure-results/`, `target/`) remain excluded in `.gitignore`
