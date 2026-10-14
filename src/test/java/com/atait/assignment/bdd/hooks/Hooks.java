package com.atait.assignment.bdd.hooks;

import com.atait.assignment.context.ScenarioContext;
import com.atait.assignment.driver.PlaywrightManager;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Hooks {

    private static final ThreadLocal<Boolean> screenshotTaken = ThreadLocal.withInitial(() -> false);

    @AfterStep("@ui")
    public void captureScreenshotOnStepFailure(Scenario scenario) {
        if (scenario.isFailed() && !screenshotTaken.get()) {
            takeAndAttachScreenshot(scenario);
            screenshotTaken.set(true);
        }
    }

    @After("@ui")
    public void tearDownUi(Scenario scenario) {
        try {
            if (scenario.isFailed() && !screenshotTaken.get()) {
                takeAndAttachScreenshot(scenario);
            }
        } catch (Exception ignored) {
        } finally {
            screenshotTaken.remove();
            ScenarioContext.clear();
            PlaywrightManager.cleanUp();
        }
    }

    private void takeAndAttachScreenshot(Scenario scenario) {
        try {
            Page page = PlaywrightManager.getPage();
            if (page != null && !page.isClosed()) {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

                // 1. Attach to Cucumber report / parallel reporter
                scenario.attach(screenshot, "image/png", "Failure Screenshot - " + scenario.getName());

                // 2. Save screenshot file for Serenity living documentation evidence
                Path screenshotDir = Paths.get("target", "site", "serenity", "screenshots");
                Files.createDirectories(screenshotDir);

                String cleanScenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                String fileName = cleanScenarioName + "_" + timestamp + ".png";
                Path screenshotPath = screenshotDir.resolve(fileName);

                Files.write(screenshotPath, screenshot);

                // 3. Attach file evidence to Serenity report
                Serenity.recordReportData()
                        .asEvidence()
                        .withTitle("Failure Screenshot")
                        .downloadable()
                        .fromFile(screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("[Hooks] Warning: Failed to capture failure screenshot: " + e.getMessage());
        }
    }
}
