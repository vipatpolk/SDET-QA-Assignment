# SDET-QA-Assignment

Automation test framework featuring **Playwright Java** (UI testing), **REST Assured / Serenity REST** (API testing), and **Cucumber BDD** on **JUnit 5 Platform** with **Serenity BDD Living Documentation**, **Docker Compose**, and **Maven**.

---

## 🛠️ Tech Stack & Prerequisites

- **Option A (Containerized — Zero Local Setup Required)**:
  - [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) v2+
- **Option B (Local Host Setup)**:
  - **Java**: JDK 17 or newer (e.g., OpenJDK 17/21/26)
  - **Build Tool**: Maven 3.9+ (A Maven Wrapper `mvnw` / `mvnw.cmd` is included)
- **Frameworks & Libraries**:
  - [Cucumber BDD](https://cucumber.io/) — Behavior-Driven Development with Gherkin
  - [Playwright for Java](https://playwright.dev/java/) — Fast and reliable end-to-end web testing
  - [Serenity BDD](https://serenity-bdd.info/) — Living documentation and rich test execution reporting
  - [REST Assured & SerenityRest](https://rest-assured.io/) — RESTful API testing & validation with automated reporting
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
│   │   │   └── com/atait/assignment/
│   │   │       ├── config/
│   │   │       │   └── ConfigurationManager.java # Centralized config & property loader
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
│           ├── features/                         # Gherkin feature files
│           │   ├── test-coverage/
│           │   │   └── Part_0_scenario_list.md   # Test coverage matrix & ambiguity log
│           │   ├── ui-tests/
│           │   │   └── Part_1_Playwright_for_Java_project.feature # 28 Option B UI Scenarios
│           │   └── api-tests/
│           │       └── Part_2_Java_and_REST_Assured_project.feature # API Test Scenarios
│           ├── junit-platform.properties         # Cucumber reporting & glue settings
│           └── serenity.conf                     # Serenity BDD runtime configuration
├── Dockerfile                              # Container definition (Playwright Java noble base)
├── docker-compose.yml                      # Docker Compose service definition
├── docker-entrypoint.sh                    # Container entrypoint script
├── .dockerignore                           # Docker build ignore rules
├── mvnw                                    # Maven wrapper script (macOS/Linux)
├── mvnw.cmd                                # Maven wrapper script (Windows)
├── pom.xml                                 # Dependencies & build configuration
├── AGENTS.md                               # Canonical agent development guide
└── README.md
```

---

## 🐳 Dockerized Execution (Zero Local Java / Maven Setup)

You can run the entire test automation suite without installing Java, Maven, or browser dependencies on your host machine.

### 1. Run Full Test Suite in Docker
```bash
docker compose run --rm tests
```
*Note: Test reports and failure screenshots are automatically synchronized to your host `./target/site/serenity/` directory via volume mapping.*

### 2. Filter Scenarios by Cucumber Tag
Filter tests using an environment variable or command arguments:
```bash
# Via Environment Variable
docker compose run --rm -e CUCUMBER_FILTER_TAGS="@smoke" tests

# Via Command Line Argument
docker compose run --rm tests -Dcucumber.filter.tags="@smoke"
```

### 3. Override Target Environment Base URL
```bash
docker compose run --rm -e APP_BASE_URL="https://staging.saucedemo.com" tests
```

### 4. Clean Build & Cached Dependencies
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

- **Filter by Tag on `verify` (e.g. `@smoke`)**:
  - Windows (PowerShell): `.\mvnw.cmd verify "-Dcucumber.filter.tags=@smoke"`
  - Unix (Bash): `./mvnw verify -Dcucumber.filter.tags="@smoke"`

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
