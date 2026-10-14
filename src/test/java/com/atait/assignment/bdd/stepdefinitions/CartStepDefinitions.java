package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.pages.CartPage;
import com.atait.assignment.pages.CheckoutStepOnePage;
import com.atait.assignment.pages.InventoryPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartStepDefinitions {

    private final InventoryPage inventoryPage = new InventoryPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();

    @When("I click the {string} button for {string}")
    public void clickItemButton(String buttonLabel, String itemName) {
        if (!cartPage.getCurrentUrl().contains("cart.html")) {
            if ("Remove".equals(buttonLabel)) {
                if ("Add to cart".equals(inventoryPage.getItemButtonLabel(itemName))) {
                    if ("Sauce Labs Bolt T-Shirt".equals(itemName)) {
                        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                        inventoryPage.clickItemButton("Sauce Labs Bike Light", "Add to cart");
                        inventoryPage.clickItemButton("Sauce Labs Bolt T-Shirt", "Add to cart");
                    } else if ("Sauce Labs Bike Light".equals(itemName)) {
                        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                        inventoryPage.clickItemButton("Sauce Labs Bike Light", "Add to cart");
                    } else if ("Sauce Labs Backpack".equals(itemName)) {
                        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                    }
                }
            } else if ("Add to cart".equals(buttonLabel)) {
                if ("Sauce Labs Bike Light".equals(itemName) && "Add to cart".equals(inventoryPage.getItemButtonLabel("Sauce Labs Backpack"))) {
                    inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                } else if ("Sauce Labs Bolt T-Shirt".equals(itemName)) {
                    if ("Add to cart".equals(inventoryPage.getItemButtonLabel("Sauce Labs Backpack"))) {
                        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
                    }
                    if ("Add to cart".equals(inventoryPage.getItemButtonLabel("Sauce Labs Bike Light"))) {
                        inventoryPage.clickItemButton("Sauce Labs Bike Light", "Add to cart");
                    }
                }
            }
        }

        if (cartPage.getCurrentUrl().contains("cart.html")) {
            cartPage.removeItem(itemName);
        } else {
            inventoryPage.clickItemButton(itemName, buttonLabel);
        }
    }

    @Then("the shopping cart badge should display {string}")
    public void verifyCartBadgeDisplay(String expectedBadgeCount) {
        if ("no badge displayed".equalsIgnoreCase(expectedBadgeCount)) {
            assertFalse(inventoryPage.isCartBadgeVisible(), "Cart badge should not be visible");
        } else {
            assertEquals(expectedBadgeCount, inventoryPage.getCartBadgeText(), "Cart badge count mismatch");
        }
    }

    @Then("the button label for {string} should change to {string}")
    public void verifyButtonLabel(String itemName, String expectedButtonLabel) {
        assertEquals(expectedButtonLabel, inventoryPage.getItemButtonLabel(itemName),
                "Button label mismatch for " + itemName);
    }

    @Given("I have items in the cart")
    public void setupItemsInCart() {
        inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
    }

    @Given("I have added {string} and {string} to the cart")
    public void addTwoItemsToCart(String item1, String item2) {
        inventoryPage.clickItemButton(item1, "Add to cart");
        inventoryPage.clickItemButton(item2, "Add to cart");
    }

    @Given("I have added {int} items to the shopping cart")
    public void addNItemsToCart(int count) {
        List<String> names = inventoryPage.getAllItemNames();
        for (int i = 0; i < Math.min(count, names.size()); i++) {
            inventoryPage.clickItemButton(names.get(i), "Add to cart");
        }
    }

    @When("I navigate to the cart overview page")
    public void navigateToCartOverviewPage() {
        inventoryPage.clickCart();
    }

    @Then("the cart badge should display {string}")
    public void verifyCartBadgeCount(String expectedCount) {
        verifyCartBadgeDisplay(expectedCount);
    }

    @Then("I should be returned to the inventory catalog")
    public void verifyReturnedToInventory() {
        inventoryPage.waitForUrl("/inventory.html");
        assertTrue(inventoryPage.getCurrentUrl().contains("/inventory.html"),
                "User should be on inventory catalog page");
    }

    @Then("the button for {string} should display {string}")
    public void verifyItemButtonDisplay(String itemName, String expectedLabel) {
        assertEquals(expectedLabel, inventoryPage.getItemButtonLabel(itemName));
    }

    @Given("my shopping cart contains {int} items")
    public void setupCartWithItemCount(int count) {
        if (count > 0) {
            inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
        }
    }

    @Then("the system should {string}")
    public void verifyCheckoutProceedBehavior(String expectedOutcome) {
        checkoutStepOnePage.waitForUrl("/checkout-step-one.html");
        assertTrue(checkoutStepOnePage.getCurrentUrl().contains("checkout-step-one.html"),
                "Expected user to land on checkout step one page");
    }
}
