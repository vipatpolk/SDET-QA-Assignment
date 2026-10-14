package com.atait.assignment;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlaywrightSampleTest {
    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    @BeforeAll
    static void setUpAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void tearDownAll() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUp() {
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        if (page != null) {
            page.close();
        }
    }

    @Test
    @DisplayName("Verify page navigation and title retrieval")
    void shouldNavigateAndCheckTitle() {
        page.navigate("https://playwright.dev/");
        String title = page.title();
        assertNotNull(title);
        assertTrue(title.contains("Playwright"), "Page title should contain 'Playwright'");
    }
}
