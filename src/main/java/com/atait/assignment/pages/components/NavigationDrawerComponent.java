package com.atait.assignment.pages.components;

import com.atait.assignment.pages.BasePage;
import com.microsoft.playwright.Locator;

public class NavigationDrawerComponent extends BasePage {

    private final String menuButton = "#react-burger-menu-btn";
    private final String closeButton = "#react-burger-cross-btn";
    private final String drawerWrap = ".bm-menu-wrap";
    private final String allItemsLink = "#inventory_sidebar_link";
    private final String aboutLink = "#about_sidebar_link";
    private final String logoutLink = "#logout_sidebar_link";
    private final String resetLink = "#reset_sidebar_link";

    public void openDrawer() {
        getPage().click(menuButton);
        getPage().waitForSelector(drawerWrap + "[aria-hidden='false']");
    }

    public void closeDrawer() {
        getPage().click(closeButton);
        getPage().waitForSelector(drawerWrap + "[aria-hidden='true']");
    }

    public boolean isDrawerOpen() {
        return "false".equals(getPage().locator(drawerWrap).getAttribute("aria-hidden"));
    }

    public void clickNavLink(String linkName) {
        switch (linkName) {
            case "All Items":
                getPage().click(allItemsLink);
                break;
            case "About":
                getPage().click(aboutLink);
                break;
            case "Logout":
                getPage().click(logoutLink);
                break;
            case "Reset App State":
                getPage().click(resetLink);
                break;
            default:
                getPage().locator(".bm-item-list a").filter(new Locator.FilterOptions().setHasText(linkName)).click();
        }
    }
}
