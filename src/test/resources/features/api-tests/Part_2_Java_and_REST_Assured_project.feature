@api @gorest
Feature: APart 2: API automation (Java and REST Assured)
  As an API consumer and test automation engineer
  I want to validate the GoREST user management endpoints (/public/v2/users)
  So that user provisioning, retrieval, updates, and lifecycle operations remain robust, secure, and compliant with API specifications

  Background:
    Given the GoREST API client is configured

  # =========================================================================
  # RULE 1: User Provisioning, Contract & Full Lifecycle
  # =========================================================================
  Rule: User Provisioning, Contract & Full Lifecycle

    @smoke @contract
    Scenario: Successfully provision user with strict schema contract verification
      # Phase 1: Build up (Create User)
      When I create a user with payload:
        | name   | dynamic  |
        | email  | dynamic  |
        | gender | male     |
        | status | active   |
      # Phase 2: Assert Contract & Schema Compliance
      Then the response status code should be 201
      And the response body should contain a valid user id
      And the response body should conform to the user schema
      And the response time should be less than 2500 milliseconds
      And the response headers should contain:
        | header       |
        | content-type |
      # Phase 3: Tear down
      When I delete a user by ID "{user_id}"
      Then the response status code should be 204

    @smoke @crud @lifecycle
    Scenario Outline: Complete user lifecycle through build up, test, and tear down
      # Phase 1: Build up (Add)
      When I create a user with payload:
        | name   | <name>   |
        | email  | dynamic  |
        | gender | <gender> |
        | status | <status> |
      Then the response status code should be 201
      And the response body should contain a valid user id
      And the response time should be less than 2500 milliseconds

      # Phase 2: Test (Read & Verify initial state)
      When I retrieve a user by ID "{user_id}"
      Then the response status code should be 200
      And the response body field "name" should be "<name>"
      And the response body field "gender" should be "<gender>"
      And the response body field "status" should be "<status>"

      # Phase 3: Test (Update & Verify mutated state)
      When I update a user by ID "{user_id}" with payload:
        | name   | <updated_name>   |
        | status | <updated_status> |
      Then the response status code should be 200
      And the response body field "name" should be "<updated_name>"
      And the response body field "status" should be "<updated_status>"

      # Phase 4: Tear down (Delete & Verify removal)
      When I delete a user by ID "{user_id}"
      Then the response status code should be 204
      When I retrieve a user by ID "{user_id}"
      Then the response status code should be 404
      And the response error message should be "Resource not found"

      Examples:
        | name               | gender | status   | updated_name        | updated_status |
        | Tenali Ramakrishna | male   | active   | Updated Ramakrishna | inactive       |
        | Anita Deshmukh     | female | active   | Updated Deshmukh    | inactive       |
        | Suresh Raina       | male   | inactive | Suresh Raina Lead   | active         |

  # =========================================================================
  # RULE 2: User Retrieval & Pagination Validation
  # =========================================================================
  Rule: User Retrieval & Pagination Validation

    @regression @query
    Scenario Outline: Retrieve a paginated list of users with dynamic pagination parameters
      When I retrieve users on page <page_number> with page size <page_size>
      Then the response status code should be 200
      And the response body should contain <page_size> items
      And the response headers should contain:
        | header             |
        | x-pagination-page  |
        | x-pagination-limit |
        | x-pagination-total |

      Examples:
        | page_number | page_size |
        | 1           | 5         |
        | 1           | 10        |
        | 2           | 10        |

    @regression @negative
    Scenario Outline: Return not found when querying non-existent or invalid user identifiers
      When I retrieve a user by ID "<invalid_id>"
      Then the response status code should be 404
      And the response error message should be "Resource not found"

      Examples:
        | invalid_id |
        | 99999999   |
        | 88888888   |
        | 0          |

  # =========================================================================
  # RULE 3: User Modification & Conflict Rejection
  # =========================================================================
  Rule: User Modification & Conflict Rejection

    @regression @validation
    Scenario: Reject update when assigning an email already taken by another user
      # Build up two independent test users
      Given two existing users are created
      # Test collision rejection
      When I update a user by ID "{first_user_id}" with the email of user "{second_user_id}"
      Then the response status code should be 422
      And the response validation error for "email" should be "has already been taken"
      # Tear down both users
      When I delete a user by ID "{first_user_id}"
      Then the response status code should be 204
      When I delete a user by ID "{second_user_id}"
      Then the response status code should be 204

    @regression @negative
    Scenario Outline: Reject update operations for non-existent user identifiers
      When I update a user by ID "<invalid_id>" with payload:
        | name   | Updated Name |
        | status | inactive     |
      Then the response status code should be 404
      And the response error message should be "Resource not found"

      Examples:
        | invalid_id |
        | 99999999   |
        | 0          |

  # =========================================================================
  # RULE 4: User Deletion & Lifecycle Verification
  # =========================================================================
  Rule: User Deletion & Lifecycle Verification

    @regression @negative
    Scenario Outline: Return not found when deleting non-existent user identifiers
      When I delete a user by ID "<invalid_id>"
      Then the response status code should be 404
      And the response error message should be "Resource not found"

      Examples:
        | invalid_id |
        | 99999999   |
        | 0          |

  # =========================================================================
  # RULE 5: Request Payload Validation & Boundary Conditions
  # =========================================================================
  Rule: Request Payload Validation & Boundary Conditions

    @regression @validation
    Scenario: Reject user creation when email address already exists
      # Build up primary user
      Given an existing user is created with payload:
        | name   | Primary User |
        | email  | dynamic      |
        | gender | male         |
        | status | active       |
      # Test duplicate creation rejection
      When I create a user with duplicate email of the existing user
      Then the response status code should be 422
      And the response validation error for "email" should be "has already been taken"
      # Tear down
      When I delete a user by ID "{user_id}"
      Then the response status code should be 204

    @regression @validation
    Scenario Outline: Reject user creation when mandatory attributes are missing
      When I create a user with missing field "<missing_field>"
      Then the response status code should be 422
      And the response validation error for "<missing_field>" should be "<expected_error>"

      Examples:
        | missing_field | expected_error                       |
        | name          | can't be blank                       |
        | email         | can't be blank                       |
        | gender        | can't be blank, can be male of female |
        | status        | can't be blank                       |

    @regression @validation
    Scenario Outline: Reject user creation with malformed attribute values
      When I create a user with invalid field "<field>" set to "<invalid_value>"
      Then the response status code should be 422
      And the response validation error for "<field>" should be "<expected_error>"

      Examples:
        | field  | invalid_value      | expected_error                       |
        | email  | invalid-email-addr | is invalid                           |
        | email  | missing-domain@    | is invalid                           |
        | gender | other              | can't be blank, can be male of female |
        | gender | unknown            | can't be blank, can be male of female |
        | status | pending            | can't be blank                       |
        | status | suspended          | can't be blank                       |

  # =========================================================================
  # RULE 6: Security & Protected Write Operations Enforcement
  # =========================================================================
  Rule: Security & Protected Write Operations Enforcement

    @regression @security
    Scenario Outline: Reject user creation when authentication credentials are invalid or missing
      Given the API authentication token is "<token_state>"
      When I create a user with payload:
        | name   | Security Candidate |
        | email  | dynamic            |
        | gender | male               |
        | status | active             |
      Then the response status code should be <expected_status>
      And the response error message should be present
      And no user record should be created

      Examples:
        | token_state | expected_status |
        | omitted     | 401             |
        | invalid     | 401             |

    @regression @security
    Scenario Outline: Reject user update when authentication credentials are invalid or missing
      # Build up with valid credentials
      Given an existing user is created with payload:
        | name   | Protected User |
        | email  | dynamic        |
        | gender | male           |
        | status | active         |
      # Test update with missing / invalid token
      Given the API authentication token is "<token_state>"
      When I update a user by ID "{user_id}" with payload:
        | name   | Tampered Name |
        | status | inactive      |
      Then the response status code should be <expected_status>
      And the response error message should be present
      # Verify user record was NOT mutated (side-effect check)
      Given the API authentication token is "default"
      When I retrieve a user by ID "{user_id}"
      Then the response status code should be 200
      And the response body should match the attributes of the existing user
      # Tear down
      When I delete a user by ID "{user_id}"
      Then the response status code should be 204

      Examples:
        | token_state | expected_status |
        | omitted     | 404             |
        | invalid     | 401             |

    @regression @security
    Scenario Outline: Reject user deletion when authentication credentials are invalid or missing
      # Build up with valid credentials
      Given an existing user is created with payload:
        | name   | Delete Protected User |
        | email  | dynamic               |
        | gender | female                |
        | status | active                |
      # Test deletion with missing / invalid token
      Given the API authentication token is "<token_state>"
      When I delete a user by ID "{user_id}"
      Then the response status code should be <expected_status>
      And the response error message should be present
      # Verify user record was NOT deleted (side-effect check)
      Given the API authentication token is "default"
      When I retrieve a user by ID "{user_id}"
      Then the response status code should be 200
      # Tear down with valid credentials
      When I delete a user by ID "{user_id}"
      Then the response status code should be 204

      Examples:
        | token_state | expected_status |
        | omitted     | 404             |
        | invalid     | 401             |
