package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;

public class CheckoutCompletePage extends BasePage {

    public static final String COMPLETE_PATH = "/checkout-complete.html";

    private final String completeHeader = ".complete-header";
    private final String backHomeButton = "#back-to-products";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(COMPLETE_PATH));
    }

    public String getCompleteHeader() {
        return getText(completeHeader);
    }

    public void clickBackHome() {
        getPage().click(backHomeButton);
    }
}
