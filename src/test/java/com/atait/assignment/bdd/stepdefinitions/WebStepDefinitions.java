package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.driver.PlaywrightManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebStepDefinitions {
    private Page page;

    @Given("I open the browser")
    public void openBrowser() {
        page = PlaywrightManager.getPage();
    }

    @When("I navigate to {string}")
    public void navigateTo(String url) {
        page.navigate(url);
    }

    @Then("the page title should contain {string}")
    public void verifyPageTitle(String expectedSubstring) {
        String title = page.title();
        assertNotNull(title, "Page title should not be null");
        assertTrue(title.contains(expectedSubstring),
            String.format("Expected title '%s' to contain '%s'", title, expectedSubstring));
    }
}
