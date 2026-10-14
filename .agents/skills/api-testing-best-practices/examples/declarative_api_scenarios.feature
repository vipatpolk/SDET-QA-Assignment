@api
Feature: Exemplary Declarative API Automation Scenarios
  As an API consumer and test automation engineer
  I want to validate RESTful service endpoints against contract, functionality, security, and latency
  So that our service layer guarantees reliability, high performance, and backward compatibility

  Background:
    Given the API endpoint is configured

  # =========================================================================
  # 1. Functional Happy Path & Status Codes
  # =========================================================================
  @smoke @functional
  Scenario: Retrieve an existing resource by unique identifier
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the response body should contain "userId" with value 1
    And the response body should contain "id" with value 1
    And the response time should be less than 2000 milliseconds

  @smoke @functional
  Scenario: Create a new resource with valid payload
    When I send a POST request to "/posts" with title "Declarative BDD", body "Best practices in action", and userId 10
    Then the response status code should be 201
    And the response body should contain a valid id
    And the response body should contain "title" with value "Declarative BDD"
    And the response time should be less than 2000 milliseconds

  # =========================================================================
  # 2. Negative & Boundary Testing with Scenario Outlines
  # =========================================================================
  @regression @negative
  Scenario Outline: Verify endpoint behavior with invalid or non-existent resource IDs
    When I send a GET request to "/posts/<resource_id>"
    Then the response status code should be <expected_status>
    And the response time should be less than 2000 milliseconds

    Examples:
      | resource_id | expected_status |
      | 999999      | 404             |
      | invalid_id  | 404             |
      | 0           | 404             |

  # =========================================================================
  # 3. Idempotency & Resource Modification
  # =========================================================================
  @regression @idempotency
  Scenario: Update an existing resource idempotently
    When I send a PUT request to "/posts/1" with title "Updated Title", body "Updated Body Content", and userId 1
    Then the response status code should be 200
    And the response body should contain "id" with value 1
    And the response body should contain "title" with value "Updated Title"
    And the response time should be less than 2000 milliseconds

  @regression @lifecycle
  Scenario: Delete a resource and verify lifecycle completion
    When I send a DELETE request to "/posts/1"
    Then the response status code should be 200
    And the response time should be less than 2000 milliseconds
