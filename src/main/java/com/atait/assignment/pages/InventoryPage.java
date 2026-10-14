package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;
import com.microsoft.playwright.Locator;

import java.util.ArrayList;
import java.util.List;

public class InventoryPage extends BasePage {

    public static final String INVENTORY_PATH = "/inventory.html";

    private final String headerTitle = ".title";
    private final String inventoryItems = ".inventory_item";
    private final String sortDropdown = ".product_sort_container";
    private final String cartBadge = ".shopping_cart_badge";
    private final String cartLink = ".shopping_cart_link";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(INVENTORY_PATH));
    }

    public String getHeaderText() {
        return getText(headerTitle);
    }

    public int getItemCount() {
        return getPage().locator(inventoryItems).count();
    }

    public Locator getItemCard(String itemName) {
        return getPage().locator(".inventory_item").filter(
            new Locator.FilterOptions().setHasText(itemName)
        );
    }

    public String getItemPrice(String itemName) {
        return getItemCard(itemName).locator(".inventory_item_price").textContent().trim();
    }

    public String getItemDescription(String itemName) {
        return getItemCard(itemName).locator(".inventory_item_desc").textContent().trim();
    }

    public String getItemButtonLabel(String itemName) {
        return getItemCard(itemName).locator("button").textContent().trim();
    }

    public void clickItemButton(String itemName, String buttonLabel) {
        getItemCard(itemName).locator("button", new Locator.LocatorOptions().setHasText(buttonLabel)).click();
    }

    public void clickItemTitle(String itemName) {
        getItemCard(itemName).locator(".inventory_item_name").click();
    }

    public void clickCart() {
        getPage().click(cartLink);
    }

    public String getCartBadgeText() {
        if (getPage().locator(cartBadge).isVisible()) {
            return getPage().locator(cartBadge).textContent().trim();
        }
        return "no badge displayed";
    }

    public boolean isCartBadgeVisible() {
        return getPage().locator(cartBadge).isVisible();
    }

    public void selectSortOption(String sortOption) {
        String value;
        switch (sortOption) {
            case "Name (A to Z)": value = "az"; break;
            case "Name (Z to A)": value = "za"; break;
            case "Price (low to high)": value = "lohi"; break;
            case "Price (high to low)": value = "hilo"; break;
            default: value = sortOption;
        }
        getPage().selectOption(sortDropdown, value);
    }

    public List<String> getAllItemNames() {
        return getPage().locator(".inventory_item_name").allTextContents();
    }

    public List<String> getAllItemDescriptions() {
        return getPage().locator(".inventory_item_desc").allTextContents();
    }

    public String getFirstItemName() {
        return getPage().locator(".inventory_item_name").first().textContent().trim();
    }

    public String getLastItemName() {
        return getPage().locator(".inventory_item_name").last().textContent().trim();
    }

    public List<String> getAllItemImageSources() {
        List<String> sources = new ArrayList<>();
        Locator images = getPage().locator(".inventory_item_img img");
        for (int i = 0; i < images.count(); i++) {
            sources.add(images.nth(i).getAttribute("src"));
        }
        return sources;
    }
}
