package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class CapacidadRealPage extends PaginaWeb {

    private static final By NAME = By.cssSelector("input[placeholder='Nombre completo']");
    private static final By DOCUMENT = By.cssSelector("input[placeholder='Documento de identidad']");
    private static final By EMAIL = By.cssSelector("input[type='email']");
    private static final By PHONE = By.cssSelector("input[type='tel']");
    private static final By AUTHORIZATION = By.id("authorizeCreditCheck");
    private static final By DEPENDENTS = By.cssSelector("input[placeholder='Cargas familiares']");
    private static final By COMPANY =
            By.cssSelector("input[placeholder='Compañía de tecnología S.A.S']");
    private static final By NEXT = By.xpath("//button[normalize-space()='Siguiente']");
    private static final By CONTACT =
            By.xpath("//h2[normalize-space()='Quiero que me Contacten']");

    public CapacidadRealPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void ingresarNombre(String valor) {
        completarCampo(NAME, valor);
    }

    public void ingresarDocumento(String valor) {
        completarCampo(DOCUMENT, valor);
    }

    public void ingresarCorreo(String valor) {
        completarCampo(EMAIL, valor);
    }

    public void ingresarTelefono(String valor) {
        completarCampo(PHONE, valor);
    }

    public void autorizarConsultaCrediticia() {
        var casilla = campoInteractuable(AUTHORIZATION);
        if (!casilla.isSelected()) {
            casilla.click();
        }
        wait.until(ExpectedConditions.elementSelectionStateToBe(AUTHORIZATION, true));
    }

    public void ingresarDependientes(String valor) {
        completarCampo(DEPENDENTS, valor);
    }

    public void ingresarEmpresa(String valor) {
        completarCampo(COMPANY, valor);
    }

    public String nombreIngresado() {
        return valor(NAME);
    }

    public String documentoIngresado() {
        return valor(DOCUMENT);
    }

    public String correoIngresado() {
        return valor(EMAIL);
    }

    public String telefonoIngresado() {
        return valor(PHONE);
    }

    public boolean consultaAutorizada() {
        return driver.findElement(AUTHORIZATION).isSelected();
    }

    public String dependientesIngresados() {
        return valor(DEPENDENTS);
    }

    public String empresaIngresada() {
        return valor(COMPANY);
    }

    private String valor(By selector) {
        return driver.findElement(selector).getDomProperty("value");
    }

    public void continuar() {
        campoInteractuable(NEXT).click();
    }

    public boolean contactoVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CONTACT)).isDisplayed();
    }
}
