package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class ContactoPage extends PaginaWeb {

    private static final By MORNING =
            By.cssSelector("input[name='preferredTime'][value='mañana']");
    private static final By WHATSAPP = By.cssSelector("input[name='contactMethod'][value='whatsapp']");
    private static final By PRODUCT_INFO = By.id("wantsProductInfo");
    private static final By NEXT = By.xpath("//button[normalize-space()='Siguiente']");
    private static final By CONFIRMATION = By.xpath("//h2[normalize-space()='Confirmación']");

    public ContactoPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void seleccionarManana() {
        seleccionar(MORNING);
    }

    public void seleccionarWhatsApp() {
        seleccionar(WHATSAPP);
    }

    public void marcarInformacionProductos() {
        if (!driver.findElement(PRODUCT_INFO).isSelected()) {
            campoInteractuable(By.cssSelector("label[for='wantsProductInfo']")).click();
        }
        wait.until(ExpectedConditions.elementSelectionStateToBe(PRODUCT_INFO, true));
    }

    private void seleccionar(By selector) {
        var campo = campoInteractuable(selector);
        if (!campo.isSelected()) {
            campo.click();
        }
        wait.until(ExpectedConditions.elementSelectionStateToBe(selector, true));
    }

    public boolean mananaSeleccionada() {
        return driver.findElement(MORNING).isSelected();
    }

    public boolean whatsAppSeleccionado() {
        return driver.findElement(WHATSAPP).isSelected();
    }

    public boolean informacionProductosMarcada() {
        return driver.findElement(PRODUCT_INFO).isSelected();
    }

    public void continuar() {
        campoInteractuable(NEXT).click();
    }

    public boolean confirmacionVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRMATION)).isDisplayed();
    }
}
