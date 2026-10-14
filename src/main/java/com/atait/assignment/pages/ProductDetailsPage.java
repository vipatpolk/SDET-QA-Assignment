package com.atait.assignment.pages;

public class ProductDetailsPage extends BasePage {

    private final String itemName = ".inventory_details_name";
    private final String itemDesc = ".inventory_details_desc";
    private final String itemPrice = ".inventory_details_price";
    private final String backButton = "#back-to-products";

    public String getItemName() {
        return getText(itemName);
    }

    public String getItemDescription() {
        return getText(itemDesc);
    }

    public String getItemPrice() {
        return getText(itemPrice);
    }

    public void clickBackToProducts() {
        getPage().click(backButton);
    }
}
