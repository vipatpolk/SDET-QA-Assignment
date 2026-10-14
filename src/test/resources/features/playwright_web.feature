@ui
Feature: Web UI Testing with Playwright
  As a web user
  I want to visit the Playwright homepage
  So that I can verify page details and navigation

  @smoke
  Scenario: Verify Playwright homepage title
    Given I open the browser
    When I navigate to "https://playwright.dev"
    Then the page title should contain "Playwright"
