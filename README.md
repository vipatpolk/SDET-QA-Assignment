# SDET-QA-Assignment

Automation test framework featuring **Playwright Java** (UI testing), **REST Assured** (API testing), and **Cucumber BDD** on **JUnit 5 Platform** with **Allure Reporting** and **Maven**.

---

## 🛠️ Tech Stack & Prerequisites

- **Java**: JDK 17 or newer (e.g., OpenJDK 17/21/26)
- **Build Tool**: Maven 3.9+ (A Maven Wrapper `mvnw` / `mvnw.cmd` is included so local Maven installation is optional)
- **Frameworks & Libraries**:
  - [Cucumber BDD](https://cucumber.io/) — Behavior-Driven Development with Gherkin
  - [Playwright for Java](https://playwright.dev/java/) — Fast and reliable end-to-end web testing
  - [REST Assured](https://rest-assured.io/) — RESTful API testing & validation
  - [Allure Report](https://allurereport.org/) — Rich, interactive test execution reporting
  - [Jackson Databind](https://github.com/FasterXML/jackson) — JSON serialization & deserialization
  - [Hamcrest](http://hamcrest.org/) — Matchers for API assertions
  - [JUnit 5 Jupiter / Platform](https://junit.org/junit5/) — Modern test runner and suite execution

---

## 📁 Project Structure

```text
SDET-QA-Assignment/
├── .mvn/wrapper/                            # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/atait/assignment/driver/
│   │   │       └── PlaywrightManager.java  # Thread-safe Playwright browser/page manager
│   │   └── resources/
│   └── test/
│       ├── java/
│       │   └── com/atait/assignment/
│       │       ├── bdd/
│       │       │   ├── CucumberTestRunner.java     # JUnit 5 @Suite runner for Cucumber
│       │       │   ├── hooks/
│       │       │   │   └── Hooks.java              # Scenario hooks (UI cleanup, screenshots)
│       │       │   └── stepdefinitions/
│       │       │       ├── ApiStepDefinitions.java # BDD step defs with Allure REST Assured filter
│       │       │       └── WebStepDefinitions.java # BDD step defs for Playwright
│       │       ├── PlaywrightSampleTest.java       # Standalone JUnit 5 UI test
│       │       └── RestAssuredSampleTest.java      # Standalone JUnit 5 API test
│       └── resources/
│           ├── features/                           # Gherkin feature files
│           │   ├── playwright_web.feature          # UI BDD scenarios
│           │   └── posts_api.feature               # API BDD scenarios
│           └── junit-platform.properties           # Cucumber reporting & glue settings
├── mvnw                                    # Maven wrapper script (macOS/Linux)
├── mvnw.cmd                                # Maven wrapper script (Windows)
├── pom.xml                                 # Dependencies & build configuration
└── README.md
```

---

## 🚀 Getting Started

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

### 2. Run Tests

#### Run All Tests (BDD Scenarios + Standalone Tests)
```bash
.\mvnw.cmd clean test
```

#### Run Only Cucumber BDD Scenarios
```bash
.\mvnw.cmd test -Dtest=CucumberTestRunner
```

#### Run Cucumber Scenarios by Tag
Filter scenarios using tags (e.g., `@smoke`, `@regression`, `@ui`, `@api`):

**On Windows (PowerShell):**
```bash
.\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@smoke"
```
```bash
.\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@api"
```
```bash
.\mvnw.cmd test -Dtest=CucumberTestRunner "-Dcucumber.filter.tags=@ui"
```

**On macOS / Linux (Bash):**
```bash
./mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"
```

#### Run Standalone JUnit Tests
- **API Tests only:**
  ```bash
  .\mvnw.cmd test -Dtest=RestAssuredSampleTest
  ```
- **UI Tests only:**
  ```bash
  .\mvnw.cmd test -Dtest=PlaywrightSampleTest
  ```

---

### 📊 Allure & Cucumber Reporting

#### Allure Report (Recommended)
All test runs (JUnit 5, Cucumber BDD, and REST Assured HTTP calls) automatically output results into `target/allure-results`.

- **Generate and serve interactive Allure report in browser:**
  ```bash
  .\mvnw.cmd allure:serve
  ```
- **Generate standalone HTML report into `target/site/allure-maven-plugin`:**
  ```bash
  .\mvnw.cmd allure:report
  ```

#### Cucumber HTML Report
- **Interactive HTML Report**: `target/cucumber-reports/cucumber.html`
- **JSON Report**: `target/cucumber-reports/cucumber.json`

---

### 3. Playwright Tools & CLI Utilities

#### Code Generator (Playwright Codegen)
Record user actions and generate test code interactively:
```bash
.\mvnw.cmd exec:java -Dexec.args="codegen https://playwright.dev"
```

#### Trace Viewer
Inspect Playwright trace files:
```bash
.\mvnw.cmd exec:java -Dexec.args="show-trace trace.zip"
```
