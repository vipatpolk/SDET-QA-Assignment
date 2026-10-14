package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;

public class CheckoutStepOnePage extends BasePage {

    public static final String STEP_ONE_PATH = "/checkout-step-one.html";

    private final String firstNameInput = "#first-name";
    private final String lastNameInput = "#last-name";
    private final String postalCodeInput = "#postal-code";
    private final String continueButton = "#continue";
    private final String cancelButton = "#cancel";
    private final String errorBanner = "[data-test='error']";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(STEP_ONE_PATH));
    }

    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        if (firstName != null) {
            getPage().fill(firstNameInput, firstName);
        }
        if (lastName != null) {
            getPage().fill(lastNameInput, lastName);
        }
        if (postalCode != null) {
            getPage().fill(postalCodeInput, postalCode);
        }
    }

    public void clickContinue() {
        getPage().click(continueButton);
    }

    public void clickCancel() {
        getPage().click(cancelButton);
    }

    public String getErrorMessage() {
        if (getPage().locator(errorBanner).isVisible()) {
            return getPage().locator(errorBanner).textContent().trim();
        }
        return "";
    }
}
