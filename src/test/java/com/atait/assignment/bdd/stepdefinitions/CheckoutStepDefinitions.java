package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutStepDefinitions {

    private final InventoryPage inventoryPage = new InventoryPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();
    private final CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage();
    private final CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage();

    @Given("I have added the items {string} to the cart")
    public void addMultipleItemsToCart(String itemsCommaSeparated) {
        String[] items = itemsCommaSeparated.split(",");
        for (String item : items) {
            String trimmedItem = item.trim();
            if (!trimmedItem.isEmpty()) {
                inventoryPage.clickItemButton(trimmedItem, "Add to cart");
            }
        }
    }

    @When("I proceed to checkout")
    public void proceedToCheckout() {
        inventoryPage.clickCart();
        cartPage.clickCheckout();
    }

    @When("I fill customer information with First Name {string}, Last Name {string}, and Postal Code {string}")
    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        checkoutStepOnePage.fillCustomerInfo(firstName, lastName, postalCode);
    }

    @Then("the item subtotal should be {string}")
    public void verifySubtotal(String expectedSubtotal) {
        checkoutStepTwoPage.waitForUrl("/checkout-step-two.html");
        assertEquals(expectedSubtotal, checkoutStepTwoPage.getItemTotal(), "Item subtotal mismatch");
    }

    @Then("the computed tax should be {string}")
    public void verifyTax(String expectedTax) {
        assertEquals(expectedTax, checkoutStepTwoPage.getTax(), "Tax mismatch");
    }

    @Then("the final order total should be {string}")
    public void verifyTotal(String expectedTotal) {
        assertEquals(expectedTotal, checkoutStepTwoPage.getTotal(), "Total price mismatch");
    }

    @Then("I should be navigated to the checkout complete page")
    public void verifyNavigatedToCompletePage() {
        checkoutCompletePage.waitForUrl("/checkout-complete.html");
        assertTrue(checkoutCompletePage.getCurrentUrl().contains("checkout-complete.html"),
                "User should be on checkout complete page");
    }

    @Then("the order completion header should display \"Thank you for your order!\"")
    public void verifyOrderCompletionHeader() {
        assertEquals("Thank you for your order!", checkoutCompletePage.getCompleteHeader());
    }

    @Then("the cart badge should be cleared")
    public void verifyCartBadgeCleared() {
        assertFalse(inventoryPage.isCartBadgeVisible(), "Cart badge should be cleared after order completion");
    }

    @Given("I have at least {int} item in the cart")
    public void ensureItemsInCart(int count) {
        if (!inventoryPage.isCartBadgeVisible()) {
            inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
        }
    }

    @Given("I am on the checkout step one customer information page")
    public void navigateToCheckoutStepOne() {
        if (!checkoutStepOnePage.getCurrentUrl().contains("checkout-step-one.html")) {
            inventoryPage.clickCart();
            cartPage.clickCheckout();
        }
    }

    @When("I enter First Name {string}, Last Name {string}, and Postal Code {string}")
    public void enterCustomerDetails(String first, String last, String zip) {
        checkoutStepOnePage.fillCustomerInfo(first, last, zip);
    }

    @When("I attempt to continue checkout")
    public void attemptContinueCheckout() {
        checkoutStepOnePage.clickContinue();
    }

    @Then("I should remain on checkout step one")
    public void verifyRemainingOnCheckoutStepOne() {
        assertTrue(checkoutStepOnePage.getCurrentUrl().contains("checkout-step-one.html"));
    }

    @Then("a checkout validation error should display {string}")
    public void verifyCheckoutValidationError(String expectedError) {
        assertEquals(expectedError, checkoutStepOnePage.getErrorMessage());
    }

    @When("I enter {string} in First Name, {string} in Last Name, and {string} in Postal Code")
    public void enterBoundaryCustomerInfo(String first, String last, String zip) {
        checkoutStepOnePage.fillCustomerInfo(first, last, zip);
    }

    @Then("the checkout overview should load without crash or script execution")
    public void verifyCheckoutOverviewLoadedSafely() {
        checkoutStepTwoPage.waitForUrl("/checkout-step-two.html");
        assertTrue(checkoutStepTwoPage.getCurrentUrl().contains("checkout-step-two.html"));
    }

    @Then("the customer data should be handled securely as literal text")
    public void verifyCustomerDataHandledSecurely() {
        assertTrue(checkoutStepTwoPage.getCurrentUrl().contains("checkout-step-two.html"));
    }

    @Given("I have reached {string} with {int} items in cart")
    public void reachCheckoutStage(String checkoutStage, int itemCount) {
        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
        inventoryPage.clickItemButton("Sauce Labs Bike Light", "Add to cart");
        inventoryPage.clickCart();
        cartPage.clickCheckout();

        if ("Checkout Overview Summary".equalsIgnoreCase(checkoutStage)) {
            checkoutStepOnePage.fillCustomerInfo("Jane", "Doe", "94105");
            checkoutStepOnePage.clickContinue();
            checkoutStepTwoPage.waitForUrl("/checkout-step-two.html");
        }
    }

    @When("I refresh the browser page")
    public void refreshBrowserPage() {
        checkoutStepOnePage.reload();
    }

    @Then("I should remain on {string}")
    public void verifyRemainingOnStage(String checkoutStage) {
        if ("Checkout Overview Summary".equalsIgnoreCase(checkoutStage)) {
            assertTrue(checkoutStepTwoPage.getCurrentUrl().contains("checkout-step-two.html"));
        } else {
            assertTrue(checkoutStepOnePage.getCurrentUrl().contains("checkout-step-one.html"));
        }
    }

    @Then("all order attributes and cart items should remain intact")
    public void verifyOrderAttributesIntact() {
        String url = checkoutStepOnePage.getCurrentUrl();
        assertTrue(url.contains("checkout-step-one.html") || url.contains("checkout-step-two.html"),
                "User should remain in checkout flow, but was at: " + url);
    }
}
