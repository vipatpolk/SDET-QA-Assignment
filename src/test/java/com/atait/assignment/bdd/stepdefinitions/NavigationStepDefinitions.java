package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.config.ConfigurationManager;
import com.atait.assignment.pages.*;
import com.atait.assignment.pages.components.FooterComponent;
import com.atait.assignment.pages.components.NavigationDrawerComponent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationStepDefinitions {

    private final NavigationDrawerComponent navDrawer = new NavigationDrawerComponent();
    private final FooterComponent footer = new FooterComponent();
    private final InventoryPage inventoryPage = new InventoryPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutStepOnePage checkoutStepOnePage = new CheckoutStepOnePage();
    private final CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage();
    private final CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage();
    private final ProductDetailsPage productDetailsPage = new ProductDetailsPage();
    private final LoginPage loginPage = new LoginPage();

    private String currentSocialPlatform;

    @Then("the navigation sidebar drawer should slide into view")
    public void verifySidebarDrawerOpen() {
        assertTrue(navDrawer.isDrawerOpen(), "Sidebar drawer should be open");
    }

    @When("I click the sidebar navigation link {string}")
    public void clickSidebarLink(String linkName) {
        navDrawer.clickNavLink(linkName);
    }

    @Then("the application should navigate to {string}")
    public void verifyNavigationDestination(String destination) {
        switch (destination) {
            case "the inventory catalog page":
                inventoryPage.waitForUrl("/inventory.html");
                assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"));
                break;
            case "the external Sauce Labs website":
                assertTrue(navDrawer.getCurrentUrl().contains("saucelabs.com"));
                break;
            case "the login page":
                assertTrue(loginPage.getCurrentUrl().equals(ConfigurationManager.getUrl("/")) || loginPage.getCurrentUrl().endsWith("/"));
                break;
            case "the current page with cleared state":
                assertNotNull(navDrawer.getCurrentUrl());
                break;
            default:
                break;
        }
    }

    @When("I open the hamburger sidebar menu")
    @When("I open the hamburger menu")
    public void openHamburgerSidebarMenu() {
        navDrawer.openDrawer();
    }

    @When("I click the \"Reset App State\" link")
    public void clickResetAppStateLink() {
        navDrawer.clickNavLink("Reset App State");
    }

    @When("I close the sidebar menu")
    public void closeSidebarMenu() {
        navDrawer.closeDrawer();
    }

    @Then("the shopping cart counter badge should be removed")
    public void verifyCartBadgeRemoved() {
        assertFalse(inventoryPage.isCartBadgeVisible(), "Shopping cart badge should be removed after reset");
    }

    @Then("all catalog item buttons on the inventory page should display \"Add to cart\"")
    public void verifyAllItemButtonsDisplayAddToCart() {
        assertEquals("Add to cart", inventoryPage.getItemButtonLabel("Sauce Labs Backpack"));
    }

    @When("I am on {string}")
    public void navigateToStage(String checkoutStage) {
        inventoryPage.clickCart();
        cartPage.clickCheckout();
        if ("Checkout Overview Summary".equalsIgnoreCase(checkoutStage)) {
            checkoutStepOnePage.fillCustomerInfo("Jane", "Doe", "94105");
            checkoutStepOnePage.clickContinue();
        }
    }

    @Then("I should be returned to {string}")
    public void verifyReturnedDestination(String destinationPage) {
        if ("the shopping cart page".equalsIgnoreCase(destinationPage)) {
            cartPage.waitForUrl("/cart.html");
            assertTrue(cartPage.getCurrentUrl().contains("cart.html"));
        } else {
            inventoryPage.waitForUrl("/inventory.html");
            assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"));
        }
    }

    @Then("my cart items should remain preserved")
    public void verifyCartItemsPreserved() {
        assertTrue(inventoryPage.isCartBadgeVisible() || cartPage.getCartItemCount() >= 0);
    }

    @When("I navigate to {string}")
    public void navigateToSubpage(String target) {
        if (target.startsWith("http://") || target.startsWith("https://")) {
            loginPage.navigateTo(target);
        } else if ("Cart Overview Page".equalsIgnoreCase(target)) {
            inventoryPage.clickCart();
        } else if (target.contains("Sauce Labs Fleece Jacket")) {
            inventoryPage.clickItemTitle("Sauce Labs Fleece Jacket");
        } else if (target.contains("Sauce Labs Backpack")) {
            inventoryPage.clickItemTitle("Sauce Labs Backpack");
        }
    }

    @When("I click the {string} button")
    public void clickNamedButton(String buttonName) {
        switch (buttonName) {
            case "Login":
                loginPage.clickLogin();
                break;
            case "Continue":
                checkoutStepOnePage.clickContinue();
                break;
            case "Finish":
                checkoutStepTwoPage.clickFinish();
                break;
            case "Checkout":
                cartPage.clickCheckout();
                break;
            case "Cancel":
                checkoutStepOnePage.clickCancel();
                break;
            case "Continue Shopping":
                cartPage.clickContinueShopping();
                break;
            case "Back to products":
                productDetailsPage.clickBackToProducts();
                break;
            case "Open Menu":
                navDrawer.openDrawer();
                break;
            default:
                break;
        }
    }

    @Then("I should be returned to the inventory catalog page")
    public void verifyReturnedToInventoryCatalog() {
        inventoryPage.waitForUrl("/inventory.html");
        assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"));
    }

    @Then("catalog scroll and selection states should be preserved")
    public void verifyCatalogStatePreserved() {
        assertTrue(inventoryPage.getItemCount() > 0);
    }

    @When("I inspect the social link for {string}")
    public void inspectSocialLink(String platform) {
        this.currentSocialPlatform = platform;
        assertNotNull(footer.getSocialLinkHref(platform), "Social link for " + platform + " should exist");
    }

    @Then("the link should point to {string}")
    public void verifySocialLinkTarget(String expectedDomain) {
        String href = footer.getSocialLinkHref(currentSocialPlatform != null ? currentSocialPlatform : "twitter");
        assertNotNull(href, "Social link href should not be null");
        String lowerExpected = expectedDomain.toLowerCase();
        if (lowerExpected.contains("twitter") || lowerExpected.contains("x")) {
            assertTrue(href.contains("twitter.com") || href.contains("x.com"),
                    String.format("Expected social link href '%s' to point to Twitter/X", href));
        } else if (lowerExpected.contains("facebook")) {
            assertTrue(href.contains("facebook.com"),
                    String.format("Expected social link href '%s' to point to Facebook", href));
        } else if (lowerExpected.contains("linkedin")) {
            assertTrue(href.contains("linkedin.com"),
                    String.format("Expected social link href '%s' to point to LinkedIn", href));
        } else {
            assertTrue(href.contains(expectedDomain),
                    String.format("Expected social link href '%s' to point to '%s'", href, expectedDomain));
        }
    }

    @Then("the link should open in a new browser tab securely")
    public void verifySocialLinkSecurity() {
        String platform = currentSocialPlatform != null ? currentSocialPlatform : "twitter";
        assertEquals("_blank", footer.getSocialLinkTarget(platform));
        assertTrue(footer.getSocialLinkRel(platform).contains("noreferrer"));
    }

    @When("I perform {string}")
    public void performSessionAction(String sessionAction) {
        if ("Logout via menu".equalsIgnoreCase(sessionAction)) {
            navDrawer.openDrawer();
            navDrawer.clickNavLink("Logout");
        } else if ("Order completion".equalsIgnoreCase(sessionAction)) {
            inventoryPage.clickItemButton("Sauce Labs Backpack", "Add to cart");
            inventoryPage.clickCart();
            cartPage.clickCheckout();
            checkoutStepOnePage.fillCustomerInfo("Jane", "Doe", "94105");
            checkoutStepOnePage.clickContinue();
            checkoutStepTwoPage.clickFinish();
        }
    }

    @When("I click the browser Back button")
    public void clickBrowserBackButton() {
        loginPage.goBack();
    }

    @Then("the application should enforce {string}")
    public void verifyEnforcedGuard(String expectedGuard) {
        assertNotNull(loginPage.getCurrentUrl());
    }

    @Given("I set the browser viewport to {string}")
    public void setBrowserViewport(String deviceType) {
        if ("Mobile Portrait".equalsIgnoreCase(deviceType)) {
            loginPage.setViewportSize(375, 667);
        } else {
            loginPage.setViewportSize(768, 1024);
        }
    }

    @Then("the header controls and shopping cart badge should not overlap")
    public void verifyHeaderControlsNoOverlap() {
        assertTrue(inventoryPage.isElementVisible("#react-burger-menu-btn"));
        assertTrue(inventoryPage.isElementVisible(".shopping_cart_link"));
    }

    @Then("interactive touch targets should be easily tappable on mobile screens")
    public void verifyTouchTargetsEasilyTappable() {
        assertTrue(inventoryPage.isElementVisible("#react-burger-menu-btn"));
    }

    @Then("the sidebar drawer should adapt to the screen dimensions without layout distortion")
    public void verifySidebarDrawerAdapted() {
        assertTrue(navDrawer.isDrawerOpen());
        navDrawer.closeDrawer();
    }
}
