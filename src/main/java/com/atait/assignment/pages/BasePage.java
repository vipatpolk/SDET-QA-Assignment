package com.atait.assignment.pages;

import com.atait.assignment.driver.PlaywrightManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public abstract class BasePage {

    protected Page getPage() {
        return PlaywrightManager.getPage();
    }

    public void navigateTo(String url) {
        getPage().navigate(url);
    }

    public String getCurrentUrl() {
        return getPage().url();
    }

    public String getTitle() {
        return getPage().title();
    }

    public void waitForUrl(String urlSubstring) {
        getPage().waitForURL(url -> url.contains(urlSubstring));
    }

    public void reload() {
        getPage().reload();
    }

    public void goBack() {
        getPage().goBack();
    }

    public void setViewportSize(int width, int height) {
        getPage().setViewportSize(width, height);
    }

    public boolean isElementVisible(String selector) {
        try {
            return getPage().locator(selector).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getText(String selector) {
        return getPage().locator(selector).textContent().trim();
    }
}
