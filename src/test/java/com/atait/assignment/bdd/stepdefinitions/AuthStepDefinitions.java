package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.config.ConfigurationManager;
import com.atait.assignment.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AuthStepDefinitions {

    private final LoginPage loginPage = new LoginPage();
    private final InventoryPage inventoryPage = new InventoryPage();

    @Given("I am on the SauceDemo login page")
    public void navigateToLoginPage() {
        loginPage.open();
    }

    @When("I navigate to SauceDemo and log in as {string}")
    public void navigateAndLoginAs(String username) {
        loginPage.open();
        loginPage.login(username, "secret_sauce");
    }

    @When("I enter username {string} and password {string}")
    public void enterCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @Then("I should be {string}")
    public void verifyAuthenticationOutcome(String outcome) {
        switch (outcome) {
            case "granted access to product catalog":
            case "granted access after delay":
                inventoryPage.waitForUrl("/inventory.html");
                assertTrue(inventoryPage.getCurrentUrl().contains("/inventory.html"),
                        "Expected user to be on the inventory page");
                break;
            case "denied access due to account lock":
                assertFalse(loginPage.getCurrentUrl().contains("/inventory.html"),
                        "Locked out user should not access inventory page");
                break;
            default:
                break;
        }
    }

    @Then("the page should display {string}")
    public void verifyPageContent(String expectedContent) {
        if ("Products".equalsIgnoreCase(expectedContent)) {
            assertEquals("Products", inventoryPage.getHeaderText(), "Expected Products header on inventory");
        } else {
            String actualError = loginPage.getErrorMessage();
            String normalizedExpected = expectedContent.replace("☹️", "Epic sadface").replace("??", "Epic sadface");
            assertTrue(actualError.contains(expectedContent) 
                            || actualError.contains(normalizedExpected) 
                            || (expectedContent.contains("Sorry, this user has been locked out.") && actualError.contains("Sorry, this user has been locked out.")),
                    String.format("Expected error banner '%s' but found '%s'", expectedContent, actualError));
        }
    }

    @Then("I should remain on the login page")
    public void verifyRemainingOnLoginPage() {
        assertFalse(loginPage.getCurrentUrl().contains("/inventory.html"),
                "User should remain on the login page and not access inventory");
    }

    @Then("an authentication error banner should display {string}")
    public void verifyAuthenticationErrorBanner(String expectedError) {
        String actualError = loginPage.getErrorMessage();
        if (expectedError.contains("when you are logged in")) {
            assertTrue(actualError.contains("when you are logged in"),
                    String.format("Expected error to contain login access guard message but was '%s'", actualError));
        } else {
            assertTrue(actualError.contains(expectedError),
                    String.format("Expected error to contain '%s' but found '%s'", expectedError, actualError));
        }
    }

    @Given("I am an unauthenticated guest user")
    public void setupUnauthenticatedUser() {
        loginPage.open();
    }

    @When("I attempt to navigate directly to {string}")
    public void attemptDirectNavigation(String protectedPage) {
        String targetUrl;
        switch (protectedPage) {
            case "the inventory catalog page":
                targetUrl = ConfigurationManager.getUrl(InventoryPage.INVENTORY_PATH);
                break;
            case "the shopping cart page":
                targetUrl = ConfigurationManager.getUrl(CartPage.CART_PATH);
                break;
            case "the checkout information page":
                targetUrl = ConfigurationManager.getUrl(CheckoutStepOnePage.STEP_ONE_PATH);
                break;
            default:
                targetUrl = protectedPage;
        }
        loginPage.navigateTo(targetUrl);
    }

    @Then("I should be redirected to the login page")
    public void verifyRedirectToLoginPage() {
        assertTrue(loginPage.isElementVisible("#login-button") || loginPage.getCurrentUrl().equals(ConfigurationManager.getUrl(LoginPage.LOGIN_PATH)),
                "Expected user to be presented with the login page interface");
    }
}
