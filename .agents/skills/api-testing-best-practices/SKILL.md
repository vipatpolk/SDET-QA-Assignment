---
name: api-testing-best-practices
description: >-
  Guidance, architectural patterns, and industry best practices for API test automation and strategy.
  Use this skill when authoring, reviewing, or refactoring API test specifications, REST Assured / SerenityRest tests,
  contract tests, CI/CD pipeline test stages, and API-level Gherkin feature files.
---

# API Testing Best Practices & Strategy Guide

This skill provides actionable design principles, architecture models, and execution runbooks for designing maintainable, high-speed, and scalable API test automation frameworks using **REST Assured**, **Serenity REST**, and **Cucumber BDD**.

---

## 🏛️ 1. Core Architecture: The "API Test Trapezoid"

The traditional UI-centric test pyramid (many unit tests, fewer integration tests, fewest UI tests) breaks down when applied to modern distributed architectures and microservices.

```
       /  End-to-End  \      <- Multi-service user journeys (few, critical paths)
      /================\
     / API Integration  \    <- Consumer-perspective service integrations & workflows
    /--------------------\
   /   Contract Tests     \  <- Schema validation & consumer-provider contracts (Pact)
  /------------------------\
 /        Unit Tests        \ <- Implementation-specific unit tests (isolated logic)
/____________________________\
```

### Key Architectural Tenets:
1. **Shift Left to the Service Layer**: Over 80% of application traffic flows through APIs. API tests execute directly against HTTP/gRPC/GraphQL endpoints, bypassing the browser to catch **60–80% of integration defects 10× earlier and 10× cheaper** than UI testing.
2. **Speed & Reliability**: A typical API test runs in 50–200 milliseconds compared to 5–30 seconds for a browser test, offering instant feedback in continuous integration.
3. **Consumer-First Viewpoint**: Tests must validate API behavior from the consumer's perspective, asserting public contracts, status codes, and payload schemas rather than internal implementation details.

---

## 🧭 2. The Seven Core API Testing Types

Every enterprise API quality strategy must encompass seven distinct test types ordered by risk:

| # | Test Type | Focus & Purpose | Typical Cadence & Tooling |
| :--- | :--- | :--- | :--- |
| **1** | **Functional (Happy Path)** | Validates that endpoints return correct data, headers, and status codes for valid inputs. | Every commit / PR (REST Assured, SerenityRest, Postman). |
| **2** | **Negative & Boundary** | Intentionally sends malformed data, missing mandatory fields, oversized strings, and duplicate resources. | Every commit / PR (Cucumber Scenario Outlines with edge cases). |
| **3** | **Contract / Schema** | Verifies that request/response schemas adhere strictly to specifications (OpenAPI, JSON Schema, Pact). | Every PR; blocks breaking changes between microservices. |
| **4** | **API Integration** | Validates sequential multi-step workflows across services, databases, and message queues. | PR merge & deployment pipelines. |
| **5** | **Security & Access Control** | Checks authentication token lifecycle and OWASP API Top 10 (especially Broken Object-Level Authorization - BOLA). | CI automated checks + scheduled DAST (OWASP ZAP). |
| **6** | **Performance & Latency SLAs** | Validates response times under normal and peak traffic loads; enforces sub-second SLA thresholds. | Built-in SLA assertions in functional tests + nightly k6/Gatling runs. |
| **7** | **Chaos & Resilience** | Injects artificial latency, timeouts, rate-limiting (`429`), and dependency outages to ensure graceful degradation. | Staging / pre-release environments. |

---

## ✍️ 3. Test Design & Automation Best Practices

### Rule 1: The Arrange-Act-Assert (AAA) Pattern
Structure every automated API test explicitly into three distinct phases:
- **Arrange**: Set up pre-requisite state, construct request specifications, attach authentication tokens, and build payloads dynamically.
- **Act**: Issue the HTTP request (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`).
- **Assert**: Validate the response status code, headers, JSON body attributes, schema compliance, and response latency.
- **Teardown**: Automatically delete any created resources to prevent state leakage.

### Rule 2: Test Independence & Zero Hardcoded Data
- **Never rely on pre-existing database records or static IDs**. Each test must create its own prerequisite resources via setup calls and clean them up afterward.
- Scenarios must be fully idempotent and executable in parallel across any arbitrary thread count.

### Rule 3: Enforce Response Time SLAs on Critical Endpoints
- A functional test that passes in 8 seconds is a hidden performance defect.
- Embed response time assertions directly in functional tests:
  ```java
  response.then().time(lessThan(2000L));
  ```

### Rule 4: Comprehensive Status Code Verification
Never test only `200 OK`. Explicitly verify status codes across all HTTP classes:
- **`2xx Success`**: `200 OK` (retrieval/update), `201 Created` (resource created), `204 No Content` (deletion).
- **`4xx Client Error`**: `400 Bad Request` (validation failure), `401 Unauthorized` (missing/invalid token), `403 Forbidden` (role unauthorized), `404 Not Found` (non-existent entity), `409 Conflict` (duplicate unique key), `429 Too Many Requests` (rate limited).
- **`5xx Server Error`**: Verify the API returns structured error responses without leaking stack traces or internal infrastructure details.

### Rule 5: Zero-Hardcoding Configuration Policy
- Base URIs, ports, environment credentials, and API keys must resolve hierarchically via environment variables, system properties, and centralized property managers (e.g., `ConfigurationManager`).
- Never hardcode `http://localhost` or staging URLs in test classes or step definitions.

---

## 🛠️ 4. REST Assured & Serenity REST Implementation Patterns

### Reusable Request & Response Specifications
Use `RequestSpecBuilder` and `ResponseSpecBuilder` to establish unified standards across all API requests:

```java
RequestSpecification baseRequestSpec = new RequestSpecBuilder()
    .setBaseUri(ConfigurationManager.getProperty("api.base.url"))
    .setContentType(ContentType.JSON)
    .setAccept(ContentType.JSON)
    .addFilter(new AllureRestAssured()) // or SerenityRest filters
    .build();
```

### Living Documentation with SerenityRest
Leverage `net.serenitybdd.rest.SerenityRest` in Cucumber step definitions to automatically capture request parameters, headers, bodies, and responses directly into the Serenity Living Documentation report:

```java
SerenityRest.given()
    .spec(baseRequestSpec)
    .body(payload)
    .when()
    .post("/posts")
    .then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("title", equalTo("Expected Title"))
    .time(lessThan(1500L));
```

### JSON Schema Validation
Protect against silent contract drift by asserting responses against JSON schemas stored in `src/test/resources/schemas/`:

```java
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
```

---

## 🥒 5. Gherkin BDD Authoring for APIs

Feature files for APIs must remain **declarative and business-aligned**, avoiding raw HTTP mechanics where possible.

### ❌ Prohibited (Overly Technical & Imperative):
```gherkin
Scenario: Add a user
  Given I set the header "Content-Type" to "application/json"
  And I set the body to '{"name":"John","role":"QA"}'
  When I execute a POST on endpoint "http://api.internal/users"
  Then the response code should be 201
  And the JSON path "$.name" should equal "John"
```

### ✅ Recommended (Declarative & Intent-Driven):
```gherkin
@api @smoke
Scenario: Create a new post successfully
  Given the API endpoint is configured
  When I create a new post with title "API Best Practices", body "Shift-left testing guide", and userId 1
  Then the response status code should be 201
  And the response body should contain a valid id
  And the response body should contain "title" with value "API Best Practices"
  And the response time should be less than 1500 milliseconds
```

---

## 🚀 6. CI/CD Integration & Pipeline Strategy

A mature API testing pipeline gates releases through progressive verification:

```
[ Commit / PR ]  --> 1. Fast Functional & Auth Smoke Tests (< 2 mins)
                 --> 2. Schema / Contract Verification Tests (< 3 mins)
                 --> Gate: Fails fast on critical path; PR blocked on non-2xx

[ PR Merge ]     --> 3. Cross-Service Integration & End-to-End Scenarios (< 10 mins)

[ Nightly / CD ] --> 4. Full Regression Suite (Parallel runners)
                 --> 5. Security Scans (OWASP ZAP automated baseline)
                 --> 6. Performance & Load Stress Tests (k6 / Gatling)
```

### Flakiness Mitigation Routine:
- **Asynchronous Endpoints**: If an endpoint responds asynchronously (`202 Accepted`), use polling libraries (e.g., Awaitility) with retry-until-timeout instead of arbitrary `Thread.sleep()`.
- **Third-Party Mocking**: Mock external dependencies (e.g., WireMock) during PR checks to guarantee determinism and fast pipeline cycles. Keep a dedicated live integration suite for pre-release validation.

---

## 📋 7. API Testing Quality Checklist

Before committing or releasing any API automation code, verify:
- [ ] Every endpoint has both **positive (happy path)** and **negative (edge/boundary)** test cases.
- [ ] Base URLs and credentials resolve from configuration/environment variables (no hardcoded URLs).
- [ ] Tests are self-contained, create their own test data, and run idempotently.
- [ ] Critical endpoints include response time SLA assertions.
- [ ] Scenarios are tagged with appropriate layers (`@api`), scopes (`@smoke`, `@regression`), and features.
- [ ] Step definitions delegate to reusable helper classes and assert clean status codes.
- [ ] All HTTP interactions are captured in test execution reports (Serenity / Cucumber).
