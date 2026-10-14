package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.context.ScenarioContext;
import com.atait.assignment.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogStepDefinitions {

    private final LoginPage loginPage = new LoginPage();
    private final InventoryPage inventoryPage = new InventoryPage();
    private final ProductDetailsPage productDetailsPage = new ProductDetailsPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();
    private final CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage();

    @Given("I am logged in as {string}")
    public void loginAsUser(String username) {
        loginPage.open();
        loginPage.login(username, "secret_sauce");
        ScenarioContext.set("currentUser", username);
    }

    @When("I view the product {string} on the inventory page")
    public void viewProductOnInventory(String itemName) {
        ScenarioContext.set("selectedItem", itemName);
        assertTrue(inventoryPage.getItemCard(itemName).isVisible(),
                "Product card should be visible for: " + itemName);
    }

    @Then("the product price should be {string}")
    public void verifyProductPrice(String expectedPrice) {
        String itemName = ScenarioContext.get("selectedItem");
        assertEquals(expectedPrice, inventoryPage.getItemPrice(itemName),
                "Price mismatch for " + itemName);
    }

    @Then("the product description should match {string}")
    public void verifyProductDescription(String expectedDesc) {
        String itemName = ScenarioContext.get("selectedItem");
        assertEquals(expectedDesc, inventoryPage.getItemDescription(itemName),
                "Description mismatch for " + itemName);
    }

    @When("I select {string} for purchase")
    public void selectProductForPurchase(String productName) {
        ScenarioContext.set("activeProduct", productName);
        inventoryPage.clickItemButton(productName, "Add to cart");
    }

    @Then("the product name {string} and description should match identically at {string}")
    public void verifyProductFidelityAtStage(String productName, String commerceStage) {
        String expectedDesc = inventoryPage.getItemDescription(productName);
        switch (commerceStage) {
            case "Catalog Inventory View":
                assertEquals(expectedDesc, inventoryPage.getItemDescription(productName));
                break;
            case "Item Details Subpage":
                inventoryPage.clickItemTitle(productName);
                assertEquals(productName, productDetailsPage.getItemName());
                assertEquals(expectedDesc, productDetailsPage.getItemDescription());
                productDetailsPage.clickBackToProducts();
                break;
            case "Cart Overview Page":
                inventoryPage.clickCart();
                assertEquals(expectedDesc, cartPage.getItemDescription(productName));
                cartPage.clickContinueShopping();
                break;
            case "Checkout Overview Summary":
                inventoryPage.clickCart();
                cartPage.clickCheckout();
                checkoutStepOnePage.fillCustomerInfo("Jane", "Doe", "94105");
                checkoutStepOnePage.clickContinue();
                assertEquals(expectedDesc, checkoutStepTwoPage.getItemDescription(productName));
                break;
            default:
                break;
        }
    }

    @When("I select the catalog sort option {string}")
    public void selectSortOption(String sortOption) {
        inventoryPage.selectSortOption(sortOption);
    }

    @Then("the first product should be {string} and the last product should be {string}")
    public void verifySortedBoundaryItems(String firstItem, String lastItem) {
        assertEquals(firstItem, inventoryPage.getFirstItemName(), "First sorted item mismatch");
        assertEquals(lastItem, inventoryPage.getLastItemName(), "Last sorted item mismatch");
    }

    @Then("each item description across all sorted cards should remain strictly bound to its corresponding product title")
    public void verifyDescriptionBinding() {
        List<String> names = inventoryPage.getAllItemNames();
        List<String> descs = inventoryPage.getAllItemDescriptions();
        assertEquals(names.size(), descs.size(), "Each product name must have a corresponding description");
        for (int i = 0; i < names.size(); i++) {
            assertNotNull(descs.get(i));
            assertFalse(descs.get(i).trim().isEmpty(), "Description should not be empty for " + names.get(i));
        }
    }

    @When("I inspect the product {string}")
    public void inspectProduct(String productName) {
        ScenarioContext.set("inspectedProduct", productName);
        assertTrue(inventoryPage.getItemCard(productName).isVisible());
    }

    @Then("the {string} should render the exact text pattern {string} without HTML entity escaping artifacts")
    public void verifyTypographyAndNoEscapingArtifacts(String attribute, String expectedPattern) {
        String productName = ScenarioContext.get("inspectedProduct");
        String text;
        if ("title".equalsIgnoreCase(attribute) || "name".equalsIgnoreCase(attribute)) {
            text = productName;
        } else {
            text = inventoryPage.getItemDescription(productName);
        }

        assertTrue(text.contains(expectedPattern),
                String.format("Expected '%s' to contain '%s'", text, expectedPattern));
        assertFalse(text.contains("&#"), "Text should not contain unescaped HTML numeric entities");
        assertFalse(text.contains("&quot;"), "Text should not contain unescaped &quot; entities");
        assertFalse(text.contains("&amp;"), "Text should not contain unescaped &amp; entities");
    }
}
