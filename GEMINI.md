# GEMINI.md — Instructions for Gemini & Antigravity Agents

> 📖 **Primary Guide**: This repository maintains a central, canonical development guide for all AI agents in [**`AGENTS.md`**](AGENTS.md).
> Always refer to [`AGENTS.md`](AGENTS.md) for full project architecture, coding standards, step-by-step recipes, and verification procedures.

---

## ⚡ Mandatory First Step: Read Skills First

> **CRITICAL RULE**: Before modifying, creating, or refactoring test scenarios or automation code, **you MUST first read the relevant workspace skills** in [`.agents/skills/`](.agents/skills/):
> - [**`cucumber-bdd-best-practices`**](.agents/skills/cucumber-bdd-best-practices/SKILL.md): BDD lifecycle, declarative Gherkin, scenario outlines, Data Tables, and step definitions architecture.

---

## 🎯 Role & Objective

You are an expert **SDET & Test Automation Architect**. Adhere to the core principles and standards defined in [`AGENTS.md`](AGENTS.md).

---

## ⚠️ Gemini & Antigravity Operational Reminders

1. **Dockerized Test Execution**:
   - Tests can be run containerized with `docker compose run --rm tests`.
   - Supports passing tags via `-e CUCUMBER_FILTER_TAGS="@smoke"` or CLI args.
   - Outputs mount directly to `./target` on the host.

2. **PowerShell Argument Quoting**:
   - In PowerShell, command-line arguments containing dots and equal signs must be enclosed in double quotes.
   - Example:
     ```powershell
     .\mvnw.cmd verify "-Dcucumber.filter.tags=@smoke"
     ```
     *(Unquoted `.` in `-Dcucumber.filter.tags` causes PowerShell to misinterpret the dot as a cmdlet parameter).*

3. **Always Use the Maven Wrapper for Local Host Runs**:
   - Execute commands with `.\mvnw.cmd` on Windows.
   - Do not assume `mvn` is globally available on the system `PATH`.

4. **Background Tasks & Daemons**:
   - Tests and reports run directly via Maven lifecycles (`clean verify` or `serenity:aggregate`).

5. **Detailed Reference & Recipes**:
   - For complete recipes (adding UI tests, adding API tests, browser lifecycle, Serenity reporting, Docker), see the dedicated sections in [**`AGENTS.md`**](AGENTS.md).
