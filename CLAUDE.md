# CLAUDE.md — Instructions for Claude Code

> 📖 **Primary Guide**: This repository maintains a central, canonical development guide for all AI agents in [**`AGENTS.md`**](AGENTS.md).
> Always refer to [`AGENTS.md`](AGENTS.md) for full project architecture, coding standards, driver lifecycles, and verification procedures.

---

## ⚡ Mandatory First Step: Read Skills First

> **CRITICAL RULE**: Before modifying, writing, or refactoring any code or test scenarios, **you MUST first read the relevant workspace skills** in [`.agents/skills/`](.agents/skills/):
> - [**`cucumber-bdd-best-practices`**](.agents/skills/cucumber-bdd-best-practices/SKILL.md): BDD lifecycle, declarative Gherkin, scenario outlines, Data Tables, and step definitions architecture.

---

## 🚀 Quick Commands Cheatsheet

### 🐳 Docker Compose (Zero Local Setup)
| Task | Command |
| :--- | :--- |
| **Run All Tests** | `docker compose run --rm tests` |
| **Filter by Tag** | `docker compose run --rm -e CUCUMBER_FILTER_TAGS="@smoke" tests` |
| **Filter by Tag (CLI)** | `docker compose run --rm tests -Dcucumber.filter.tags="@smoke"` |
| **Rebuild Container** | `docker compose build` |

### 💻 Local Host Execution (Maven Wrapper)
| Task | Windows (PowerShell) | Unix / macOS (Bash) |
| :--- | :--- | :--- |
| **Compile & Verify** | `.\mvnw.cmd test-compile` | `./mvnw test-compile` |
| **Run All Tests (Verify)** | `.\mvnw.cmd clean verify` | `./mvnw clean verify` |
| **Run Cucumber BDD** | `.\mvnw.cmd test -Dtest=CucumberTestRunner` | `./mvnw test -Dtest=CucumberTestRunner` |
| **Filter by Tag on Verify** | `.\mvnw.cmd verify "-Dcucumber.filter.tags=@smoke"` | `./mvnw verify -Dcucumber.filter.tags="@smoke"` |
| **Aggregate Serenity Report** | `.\mvnw.cmd serenity:aggregate` | `./mvnw serenity:aggregate` |

---

## 📌 Claude Code Guidelines

1. **Start with Skills**: Read [`.agents/skills/cucumber-bdd-best-practices/SKILL.md`](.agents/skills/cucumber-bdd-best-practices/SKILL.md) before authoring BDD tests.
2. **Strictly adhere to [`AGENTS.md`](AGENTS.md)** for:
   - Zero-hardcoding URL policy via `ConfigurationManager`.
   - Playwright browser and thread lifecycle via `PlaywrightManager`.
   - REST Assured HTTP payload logging with `SerenityRest`.
   - Cucumber Gherkin formatting, `@ui` and `@api` tag requirements.
   - Serenity living documentation report generation (`target/site/serenity/index.html`).
   - Docker execution compatibility.
3. **Quality Gate**: Run `.\mvnw.cmd test-compile` and relevant test execution prior to completing user requests.
