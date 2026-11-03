package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProyectoViviendaPage {

    private static final By PROJECT_HEADING =
            By.xpath("//h2[normalize-space()='Tu proyecto de vivienda']");
    private static final By INDIVIDUAL_OPTION = By.id("individual");
    private static final By HOUSE_OPTION = By.id("casa");
    private static final By CITY_SELECT = By.cssSelector("select");
    private static final By NEXT_BUTTON =
            By.xpath("//button[normalize-space()='Siguiente']");
    private static final By FINANCIAL_HEADING =
            By.xpath("//h2[normalize-space()='Tu situación financiera']");

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public ProyectoViviendaPage(AndroidDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public void esperarSeccionVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(PROJECT_HEADING));
    }

    public boolean siguienteEstaHabilitado() {
        return driver.findElement(NEXT_BUTTON).isEnabled();
    }

    public void seleccionarAplicacionIndividual() {
        wait.until(ExpectedConditions.elementToBeClickable(INDIVIDUAL_OPTION)).click();
    }

    public void seleccionarCasa() {
        wait.until(ExpectedConditions.elementToBeClickable(HOUSE_OPTION)).click();
    }

    public void seleccionarCiudad(String ciudad) {
        wait.until(ExpectedConditions.elementToBeClickable(CITY_SELECT));
        new Select(driver.findElement(CITY_SELECT)).selectByVisibleText(ciudad);
    }

    public void continuar() {
        wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
    }

    public boolean seccionFinancieraVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(FINANCIAL_HEADING))
                .isDisplayed();
    }
}
