package com.atait.assignment.pages;

import com.atait.assignment.config.ConfigurationManager;
import com.microsoft.playwright.Locator;

public class CheckoutStepTwoPage extends BasePage {

    public static final String STEP_TWO_PATH = "/checkout-step-two.html";

    private final String itemTotalLabel = ".summary_subtotal_label";
    private final String taxLabel = ".summary_tax_label";
    private final String totalLabel = ".summary_total_label";
    private final String finishButton = "#finish";
    private final String cancelButton = "#cancel";

    public void open() {
        navigateTo(ConfigurationManager.getUrl(STEP_TWO_PATH));
    }

    public String getItemTotal() {
        String text = getText(itemTotalLabel);
        return extractAmount(text);
    }

    public String getTax() {
        String text = getText(taxLabel);
        return extractAmount(text);
    }

    public String getTotal() {
        String text = getText(totalLabel);
        return extractAmount(text);
    }

    private String extractAmount(String fullText) {
        int dollarIndex = fullText.indexOf('$');
        if (dollarIndex != -1) {
            return fullText.substring(dollarIndex).trim();
        }
        return fullText.trim();
    }

    public void clickFinish() {
        getPage().click(finishButton);
    }

    public void clickCancel() {
        getPage().click(cancelButton);
    }

    public Locator getItemRow(String itemName) {
        return getPage().locator(".cart_item").filter(
            new Locator.FilterOptions().setHasText(itemName)
        );
    }

    public String getItemDescription(String itemName) {
        return getItemRow(itemName).locator(".inventory_item_desc").textContent().trim();
    }
}
