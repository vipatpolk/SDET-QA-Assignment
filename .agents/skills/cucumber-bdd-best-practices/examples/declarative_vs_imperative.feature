# Example: Declarative vs Imperative Style in Gherkin

# ❌ ANTI-PATTERN: Imperative Style
# - Focuses on UI mechanics and technical clicks
# - Hard to read for business stakeholders
# - Fragile: Breaks on minor UI redesigns
Feature: User Login (Imperative - Bad Example)

  Scenario: Unsuccessful login with bad credentials
    Given I open Chrome browser
    And I navigate to "https://example.com/login"
    When I click on the input box with id "#username"
    And I enter "john_doe"
    And I click on the input box with id "#password"
    And I enter "wrong_pass"
    And I click on the button with css ".login-btn"
    Then I should see the error text "Invalid credentials" in the div "#error-msg"


# ✅ BEST PRACTICE: Declarative Style
# - Focuses on user intent and business rules
# - Concise, readable, and living documentation
# - Resilient against UI implementation changes
Feature: User Login (Declarative - Best Practice)

  @ui @smoke
  Scenario: Unsuccessful login with invalid credentials
    Given a registered user "john_doe"
    When I log in with an incorrect password
    Then access should be denied with an invalid credentials error
