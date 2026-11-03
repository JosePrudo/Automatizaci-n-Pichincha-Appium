package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

public class ProyectoViviendaPage extends PaginaWeb {

    private static final By PROJECT_HEADING =
            By.xpath("//h2[normalize-space()='Tu proyecto de vivienda']");
    private static final By INDIVIDUAL_OPTION = By.id("individual");
    private static final By HOUSE_OPTION = By.id("casa");
    private static final By COUPLE_OPTION = By.id("pareja");
    private static final By APARTMENT_OPTION = By.id("apartamento");
    private static final By CITY_SELECT = By.cssSelector("select");
    private static final By MINIMUM_PRICE = By.xpath("(//input[@type='range'])[1]");
    private static final By MAXIMUM_PRICE = By.xpath("(//input[@type='range'])[2]");
    private static final By NEXT_BUTTON =
            By.xpath("//button[normalize-space()='Siguiente']");
    private static final By FINANCIAL_HEADING =
            By.xpath("//h2[normalize-space()='Tu situación financiera']");

    public ProyectoViviendaPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void esperarSeccionVisible() {
        mostrar(PROJECT_HEADING);
    }

    public boolean siguienteEstaHabilitado() {
        return driver.findElement(NEXT_BUTTON).isEnabled();
    }

    public void seleccionarAplicacionIndividual() {
        campoInteractuable(INDIVIDUAL_OPTION).click();
    }

    public void seleccionarCasa() {
        campoInteractuable(HOUSE_OPTION).click();
    }

    public void seleccionarPareja() {
        campoInteractuable(COUPLE_OPTION).click();
        wait.until(ExpectedConditions.elementSelectionStateToBe(COUPLE_OPTION, true));
    }

    public void seleccionarApartamento() {
        campoInteractuable(APARTMENT_OPTION).click();
        wait.until(ExpectedConditions.elementSelectionStateToBe(APARTMENT_OPTION, true));
    }

    public void seleccionarCiudad(String ciudad) {
        var campo = campoInteractuable(CITY_SELECT);
        new Select(campo).selectByVisibleText(ciudad);
        encuadrar(campo);
    }

    public String ciudadSeleccionada() {
        return new Select(driver.findElement(CITY_SELECT)).getFirstSelectedOption().getText();
    }

    public void seleccionarRangoCompleto() {
        seleccionarPrecioMinimo();
        seleccionarPrecioMaximo();
    }

    public void seleccionarPrecioMinimo() {
        campoInteractuable(MINIMUM_PRICE).sendKeys(Keys.HOME);
    }

    public void seleccionarPrecioMaximo() {
        campoInteractuable(MAXIMUM_PRICE).sendKeys(Keys.END);
    }

    public int precioMinimoSeleccionado() {
        return Integer.parseInt(driver.findElement(MINIMUM_PRICE).getDomProperty("value"));
    }

    public int precioMaximoSeleccionado() {
        return Integer.parseInt(driver.findElement(MAXIMUM_PRICE).getDomProperty("value"));
    }

    public void continuar() {
        campoInteractuable(NEXT_BUTTON).click();
    }

    public boolean seccionFinancieraVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(FINANCIAL_HEADING)).isDisplayed();
    }
}
