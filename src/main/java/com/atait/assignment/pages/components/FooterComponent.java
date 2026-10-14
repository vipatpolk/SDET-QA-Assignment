package com.atait.assignment.pages.components;

import com.atait.assignment.pages.BasePage;

public class FooterComponent extends BasePage {

    public String getSocialLinkHref(String platform) {
        String selector = getSocialSelector(platform);
        return getPage().locator(selector).first().getAttribute("href");
    }

    public String getSocialLinkTarget(String platform) {
        String selector = getSocialSelector(platform);
        return getPage().locator(selector).first().getAttribute("target");
    }

    public String getSocialLinkRel(String platform) {
        String selector = getSocialSelector(platform);
        return getPage().locator(selector).first().getAttribute("rel");
    }

    private String getSocialSelector(String platform) {
        String p = platform.toLowerCase();
        if (p.contains("twitter") || p.contains("x")) {
            return "a[data-test*='twitter'], a[data-test*='x'], .social_twitter a, a[href*='twitter.com'], a[href*='x.com']";
        } else if (p.contains("facebook")) {
            return "a[data-test*='facebook'], .social_facebook a, a[href*='facebook.com']";
        } else if (p.contains("linkedin")) {
            return "a[data-test*='linkedin'], .social_linkedin a, a[href*='linkedin.com']";
        }
        return "footer a";
    }
}
