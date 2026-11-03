package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class ResultadosPage extends PaginaWeb {

    private static final By REAL_CAPACITY =
            By.xpath("//button[normalize-space()='¿Quieres conocer tu capacidad real?']");
    private static final By CAPACITY_HEADING =
            By.xpath("//h2[normalize-space()='Tu capacidad real']");

    public ResultadosPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void conocerCapacidadReal() {
        campoInteractuable(REAL_CAPACITY).click();
    }

    public boolean capacidadRealVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CAPACITY_HEADING)).isDisplayed();
    }
}
