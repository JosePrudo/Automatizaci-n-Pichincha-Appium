package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.time.LocalDate;

public class SituacionFinancieraPage extends PaginaWeb {

    private static final By FINANCIAL_HEADING =
            By.xpath("//h2[normalize-space()='Tu situación financiera']");
    private static final By EMPLOYEE_OPTION = By.id("empleado");
    private static final By NET_INCOME = By.cssSelector("input[placeholder='Ingresos netos']");
    private static final By SAVINGS = By.cssSelector("input[placeholder='Total ahorrado']");
    private static final By BIRTH_DATE = By.cssSelector("input[type='date']");
    private static final By MARITAL_STATUS = By.cssSelector("select");
    private static final By NEXT_BUTTON =
            By.xpath("//button[normalize-space()='Siguiente']");
    private static final By CUSTOMIZATION_HEADING =
            By.xpath("//h2[normalize-space()='Personaliza tu crédito de vivienda']");

    public SituacionFinancieraPage(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public void esperarSeccionVisible() {
        mostrar(FINANCIAL_HEADING);
    }

    public void seleccionarEmpleado() {
        campoInteractuable(EMPLOYEE_OPTION).click();
        wait.until(ExpectedConditions.elementSelectionStateToBe(EMPLOYEE_OPTION, true));
    }

    public boolean empleadoSeleccionado() {
        return driver.findElement(EMPLOYEE_OPTION).isSelected();
    }

    public void ingresarIngresosNetos(String ingresos) {
        completarCampo(NET_INCOME, ingresos);
    }

    public void ingresarAhorro(String ahorro) {
        completarCampo(SAVINGS, ahorro);
    }

    public void ingresarFechaNacimiento(LocalDate fecha) {
        WebElement campo = campoInteractuable(BIRTH_DATE);
        // El valor ISO evita depender del idioma del selector de fecha de Android.
        driver.executeScript("""
                arguments[0].value = arguments[1];
                arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
                arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
                """, campo, fecha.toString());
        wait.until(d -> fecha.toString().equals(campo.getDomProperty("value")));
    }

    public void seleccionarEstadoCivil(String estadoCivil) {
        WebElement campo = campoInteractuable(MARITAL_STATUS);
        new Select(campo).selectByVisibleText(estadoCivil);
        encuadrar(campo);
    }

    public String ingresosNetosIngresados() {
        return driver.findElement(NET_INCOME).getDomProperty("value");
    }

    public String ahorroIngresado() {
        return driver.findElement(SAVINGS).getDomProperty("value");
    }

    public LocalDate fechaNacimientoIngresada() {
        return LocalDate.parse(driver.findElement(BIRTH_DATE).getDomProperty("value"));
    }

    public String estadoCivilSeleccionado() {
        return new Select(driver.findElement(MARITAL_STATUS)).getFirstSelectedOption().getText();
    }

    public boolean siguienteEstaHabilitado() {
        return driver.findElement(NEXT_BUTTON).isEnabled();
    }

    public void continuar() {
        campoInteractuable(NEXT_BUTTON).click();
    }

    public boolean seccionPersonalizacionVisible() {
        return mostrar(CUSTOMIZATION_HEADING).isDisplayed();
    }
}
