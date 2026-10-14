package com.atait.assignment.bdd.hooks;

import com.atait.assignment.driver.PlaywrightManager;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

public class Hooks {

    @After("@ui")
    public void tearDownUi(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = PlaywrightManager.getPage().screenshot();
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
            Allure.addAttachment("Failure Screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
        }
        PlaywrightManager.cleanUp();
    }
}
