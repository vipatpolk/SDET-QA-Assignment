package com.atait.assignment.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightManager {
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public static Page getPage() {
        if (pageThreadLocal.get() == null) {
            initPage();
        }
        return pageThreadLocal.get();
    }

    public static Page initPage() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        Page page = browser.newPage();

        playwrightThreadLocal.set(playwright);
        browserThreadLocal.set(browser);
        pageThreadLocal.set(page);
        return page;
    }

    public static void cleanUp() {
        Page page = pageThreadLocal.get();
        if (page != null) {
            page.close();
            pageThreadLocal.remove();
        }

        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            browser.close();
            browserThreadLocal.remove();
        }

        Playwright playwright = playwrightThreadLocal.get();
        if (playwright != null) {
            playwright.close();
            playwrightThreadLocal.remove();
        }
    }
}
