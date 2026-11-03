package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

public class PersonalizarCreditoPage extends PaginaWeb {

    private static final By AMOUNT =
            By.cssSelector("input[placeholder='Monto total del préstamo']");
    private static final By TERM = By.cssSelector("select");
    private static final By FIXED_PAYMENT = By.id("fija");
    private static final By NEXT = By.xpath("//button[normalize-space()='Siguiente']");
    private static final By RESULTS = By.xpath("//h2[normalize-space()='Tus resultados']");

    public PersonalizarCreditoPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void ingresarMonto(String monto) {
        completarCampo(AMOUNT, monto);
    }

    public String montoIngresado() {
        return driver.findElement(AMOUNT).getDomProperty("value");
    }

    public void seleccionarPlazo(String plazo) {
        var campo = campoInteractuable(TERM);
        new Select(campo).selectByVisibleText(plazo);
        encuadrar(campo);
    }

    public String plazoSeleccionado() {
        return new Select(driver.findElement(TERM)).getFirstSelectedOption().getText();
    }

    public void seleccionarCuotaFija() {
        campoInteractuable(FIXED_PAYMENT).click();
        wait.until(ExpectedConditions.elementSelectionStateToBe(FIXED_PAYMENT, true));
    }

    public boolean cuotaFijaSeleccionada() {
        return driver.findElement(FIXED_PAYMENT).isSelected();
    }

    public void continuar() {
        campoInteractuable(NEXT).click();
    }

    public boolean resultadosVisibles() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(RESULTS)).isDisplayed();
    }
}
