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

1. **PowerShell Argument Quoting**:
   - In PowerShell, command-line arguments containing dots and equal signs must be enclosed in double quotes.
   - Example:
     ```powershell
     .\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@smoke"
     ```
     *(Unquoted `.` in `-Dcucumber.filter.tags` causes PowerShell to misinterpret the dot as a cmdlet parameter).*

2. **Always Use the Maven Wrapper**:
   - Execute commands with `.\mvnw.cmd` on Windows.
   - Do not assume `mvn` is globally available on the system `PATH`.

3. **Background Tasks & Daemons**:
   - Avoid executing long-running daemon processes (e.g. `allure:serve`) synchronously unless specifically instructed by the user, as it runs a persistent HTTP server.
   - Use `.\mvnw.cmd allure:report` to generate static reports without holding the terminal open.

4. **Detailed Reference & Recipes**:
   - For complete recipes (adding UI tests, adding API tests, browser lifecycle, Allure filters), see the dedicated sections in [**`AGENTS.md`**](AGENTS.md).
