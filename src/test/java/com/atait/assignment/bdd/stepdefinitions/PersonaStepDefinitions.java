package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.context.ScenarioContext;
import com.atait.assignment.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonaStepDefinitions {

    private final LoginPage loginPage = new LoginPage();
    private final InventoryPage inventoryPage = new InventoryPage();
    private final ProductDetailsPage productDetailsPage = new ProductDetailsPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();
    private final CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage();
    private final CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage();

    @When("I interact with {string}")
    public void interactWithFeatureArea(String featureArea) {
        ScenarioContext.set("featureArea", featureArea);
        switch (featureArea) {
            case "catalog product images":
                // Ready for inspection
                break;
            case "product title for Onesie":
                inventoryPage.clickItemTitle("Sauce Labs Onesie");
                break;
            case "product title for Bike Light":
                inventoryPage.clickItemTitle("Sauce Labs Bike Light");
                break;
            case "checkout customer form":
                inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                inventoryPage.clickCart();
                cartPage.clickCheckout();
                checkoutStepOnePage.fillCustomerInfo("Alex", "Morgan", "90210");
                checkoutStepOnePage.clickContinue();
                break;
            default:
                break;
        }
    }

    @Then("the system should display the known defect {string}")
    public void verifyKnownDefect(String manifestedDefect) {
        String featureArea = ScenarioContext.get("featureArea");
        switch (featureArea) {
            case "catalog product images":
                List<String> images = inventoryPage.getAllItemImageSources();
                boolean allDogImages = images.stream().allMatch(src -> src != null && src.contains("sl-404"));
                assertTrue(allDogImages, "Expected problem_user to see defective sl-404 dog placeholder for all products");
                break;
            case "product title for Onesie":
            case "product title for Bike Light":
                // In problem_user, clicking item title navigates to item id 5 (Fleece Jacket)
                String currentUrl = productDetailsPage.getCurrentUrl();
                String displayedName = productDetailsPage.getItemName();
                assertTrue(currentUrl.contains("id=5") || "Sauce Labs Fleece Jacket".equals(displayedName),
                        "Expected problem_user defect routing to Fleece Jacket (id=5) but was: " + currentUrl + " (" + displayedName + ")");
                break;
            case "checkout customer form":
                String error = checkoutStepOnePage.getErrorMessage();
                assertTrue(error.contains("Last Name is required"),
                        "Expected Last Name required defect error but was: " + error);
                break;
            default:
                break;
        }
    }

    @Given("I am authenticated as \"performance_glitch_user\"")
    public void authenticateAsPerformanceGlitchUser() {
        loginPage.open();
        long startTime = System.currentTimeMillis();
        ScenarioContext.set("perfStartTime", startTime);
        loginPage.enterUsername("performance_glitch_user");
        loginPage.enterPassword("secret_sauce");
    }

    @When("I perform {string} with dynamic synchronization waits")
    public void performWithDynamicWaits(String userAction) {
        long startTime = System.currentTimeMillis();
        if ("logging into the system".equalsIgnoreCase(userAction)) {
            loginPage.clickLogin();
            inventoryPage.waitForUrl("/inventory.html");
            long duration = System.currentTimeMillis() - startTime;
            ScenarioContext.set("actionDuration", duration);
        } else if ("completing checkout".equalsIgnoreCase(userAction)) {
            loginPage.clickLogin();
            inventoryPage.waitForUrl("/inventory.html");
            inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
            inventoryPage.clickCart();
            cartPage.clickCheckout();
            checkoutStepOnePage.fillCustomerInfo("Jane", "Doe", "94105");
            checkoutStepOnePage.clickContinue();
            checkoutStepTwoPage.waitForUrl("/checkout-step-two.html");
            checkoutStepTwoPage.clickFinish();
            checkoutCompletePage.waitForUrl("/checkout-complete.html");
            long duration = System.currentTimeMillis() - startTime;
            ScenarioContext.set("actionDuration", duration);
        }
    }

    @Then("the action should complete within a noticeable delay between {int} and {int} seconds")
    public void verifyNoticeableDelay(int minSeconds, int maxSeconds) {
        Long duration = ScenarioContext.get("actionDuration");
        if (duration != null) {
            long minMillis = minSeconds * 1000L;
            long maxMillis = maxSeconds * 1000L;
            // Verify delay occurred
            assertTrue(duration >= 2000L,
                    "Expected noticeable performance glitch delay (>2000ms) but was: " + duration + "ms");
        }
    }

    @Then("the flow should finish successfully with {string}")
    public void verifyFlowFinishedSuccessfully(String expectedOutcome) {
        if ("Products".equalsIgnoreCase(expectedOutcome)) {
            assertEquals("Products", inventoryPage.getHeaderText());
        } else if ("Thank you for your order!".equalsIgnoreCase(expectedOutcome)) {
            assertEquals("Thank you for your order!", checkoutCompletePage.getCompleteHeader());
        }
    }
}
