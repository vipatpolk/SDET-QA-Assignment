# CLAUDE.md — Instructions for Claude Code

> 📖 **Primary Guide**: This repository maintains a central, canonical development guide for all AI agents in [**`AGENTS.md`**](AGENTS.md).
> Always refer to [`AGENTS.md`](AGENTS.md) for full project architecture, coding standards, driver lifecycles, and verification procedures.

---

## ⚡ Mandatory First Step: Read Skills First

> **CRITICAL RULE**: Before modifying, writing, or refactoring any code or test scenarios, **you MUST first read the relevant workspace skills** in [`.agents/skills/`](.agents/skills/):
> - [**`cucumber-bdd-best-practices`**](.agents/skills/cucumber-bdd-best-practices/SKILL.md): BDD lifecycle, declarative Gherkin, scenario outlines, Data Tables, and step definitions architecture.

---

## 🚀 Quick Commands Cheatsheet

Always use the Maven Wrapper (`.\mvnw.cmd` on Windows, `./mvnw` on macOS/Linux):

| Task | Windows (PowerShell) | Unix / macOS (Bash) |
| :--- | :--- | :--- |
| **Compile & Verify** | `.\mvnw.cmd test-compile` | `./mvnw test-compile` |
| **Run All Tests** | `.\mvnw.cmd clean test` | `./mvnw clean test` |
| **Run Cucumber BDD** | `.\mvnw.cmd test -Dtest=CucumberTestRunner` | `./mvnw test -Dtest=CucumberTestRunner` |
| **Filter by Tag** | `.\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@smoke"` | `./mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"` |
| **Run API Tests Only** | `.\mvnw.cmd test -Dtest=RestAssuredSampleTest` | `./mvnw test -Dtest=RestAssuredSampleTest` |
| **Run UI Tests Only** | `.\mvnw.cmd test -Dtest=PlaywrightSampleTest` | `./mvnw test -Dtest=PlaywrightSampleTest` |
| **Generate Allure Report** | `.\mvnw.cmd allure:report` | `./mvnw allure:report` |
| **Serve Allure Report** | `.\mvnw.cmd allure:serve` | `./mvnw allure:serve` |

---

## 📌 Claude Code Guidelines

1. **Start with Skills**: Read [`.agents/skills/cucumber-bdd-best-practices/SKILL.md`](.agents/skills/cucumber-bdd-best-practices/SKILL.md) before authoring BDD tests.
2. **Strictly adhere to [`AGENTS.md`](AGENTS.md)** for:
   - Playwright browser and thread lifecycle via `PlaywrightManager`.
   - REST Assured HTTP payload logging with `AllureRestAssured`.
   - Cucumber Gherkin formatting, `@ui` and `@api` tag requirements.
   - Allure report generation.
3. **Quality Gate**: Run `.\mvnw.cmd test-compile` and relevant test execution prior to completing user requests.
