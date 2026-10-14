package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;
import com.microsoft.playwright.Locator;

public class LoginPage extends BasePage {

    public static final String LOGIN_PATH = "/";

    private final String usernameInput = "#user-name";
    private final String passwordInput = "#password";
    private final String loginButton = "#login-button";
    private final String errorBanner = "[data-test='error']";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(LOGIN_PATH));
    }

    public void enterUsername(String username) {
        getPage().fill(usernameInput, username != null ? username : "");
    }

    public void enterPassword(String password) {
        getPage().fill(passwordInput, password != null ? password : "");
    }

    public void clickLogin() {
        getPage().click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        if (getPage().locator(errorBanner).isVisible()) {
            return getPage().locator(errorBanner).textContent().trim();
        }
        return "";
    }

    public boolean isErrorBannerVisible() {
        return getPage().locator(errorBanner).isVisible();
    }
}
