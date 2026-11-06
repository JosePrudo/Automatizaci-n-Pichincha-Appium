package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class ConfirmacionPage extends PaginaWeb {

    private static final By CONFIRM_INFORMATION = By.id("confirmInformation");
    private static final By SUBMIT = By.xpath("//button[normalize-space()='Aceptar y Enviar']");
    private static final By CANCEL = By.xpath("//button[normalize-space()='Cancelar']");
    private static final By RESPONSE_TITLE =
            By.xpath("//h3[normalize-space()='¡Simulación Enviada!' or normalize-space()='Error en el Envío']");
    private static final By ACCEPT = By.xpath("//button[normalize-space()='Aceptar']");
    private static final By PROJECT = By.xpath("//h2[normalize-space()='Tu proyecto de vivienda']");

    public ConfirmacionPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void confirmarInformacion() {
        var campo = campoInteractuable(CONFIRM_INFORMATION);
        if (!campo.isSelected()) {
            campo.click();
        }
        wait.until(ExpectedConditions.elementSelectionStateToBe(CONFIRM_INFORMATION, true));
    }

    public boolean informacionConfirmada() {
        return driver.findElement(CONFIRM_INFORMATION).isSelected();
    }

    public void aceptarYEnviar() {
        campoInteractuable(SUBMIT).click();
        mostrar(RESPONSE_TITLE);
    }

    public void cancelar() {
        campoInteractuable(CANCEL).click();
    }

    public boolean envioExitoso() {
        return driver.findElement(RESPONSE_TITLE).getText().strip().equals("¡Simulación Enviada!");
    }

    public String respuestaDelEnvio() {
        return driver.findElement(RESPONSE_TITLE).findElement(By.xpath("..")).getText();
    }

    public void aceptarRespuesta() {
        campoInteractuable(ACCEPT).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(ACCEPT));
    }

    public boolean simuladorReiniciado() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PROJECT)).isDisplayed();
    }
}
