package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;
import com.microsoft.playwright.Locator;

public class CartPage extends BasePage {

    public static final String CART_PATH = "/cart.html";

    private final String cartItems = ".cart_item";
    private final String continueShoppingButton = "#continue-shopping";
    private final String checkoutButton = "#checkout";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(CART_PATH));
    }

    public int getCartItemCount() {
        return getPage().locator(cartItems).count();
    }

    public Locator getCartItemRow(String itemName) {
        return getPage().locator(".cart_item").filter(
            new Locator.FilterOptions().setHasText(itemName)
        );
    }

    public void removeItem(String itemName) {
        getCartItemRow(itemName).locator("button", new Locator.LocatorOptions().setHasText("Remove")).click();
    }

    public void clickContinueShopping() {
        getPage().click(continueShoppingButton);
    }

    public void clickCheckout() {
        getPage().click(checkoutButton);
    }

    public String getItemDescription(String itemName) {
        return getCartItemRow(itemName).locator(".inventory_item_desc").textContent().trim();
    }
}
