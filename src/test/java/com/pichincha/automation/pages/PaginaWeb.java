package com.pichincha.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class PaginaWeb {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected PaginaWeb(AndroidDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    protected WebElement mostrar(By selector) {
        WebElement elemento = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
        encuadrar(elemento);
        wait.until(ExpectedConditions.visibilityOf(elemento));
        driver.executeScript("""
                if (document.activeElement && document.activeElement !== arguments[0]) {
                    document.activeElement.blur();
                }
                if (!document.getElementById('appium-campo-activo-estilo')) {
                    const estilo = document.createElement('style');
                    estilo.id = 'appium-campo-activo-estilo';
                    estilo.textContent = '[data-appium-campo-activo] { outline: 3px solid #e8ad00 !important; outline-offset: 3px !important; }';
                    document.head.appendChild(estilo);
                }
                document.querySelectorAll('[data-appium-campo-activo]').forEach(
                    campo => campo.removeAttribute('data-appium-campo-activo'));
                arguments[0].setAttribute('data-appium-campo-activo', '');
                """, elemento);
        encuadrar(elemento);
        return elemento;
    }

    protected void encuadrar(WebElement elemento) {
        driver.executeScript("""
                arguments[0].scrollIntoView({
                    block: 'center', inline: 'center', behavior: 'instant'
                });
                """, elemento);
        wait.until(d -> Boolean.TRUE.equals(driver.executeScript("""
                const r = arguments[0].getBoundingClientRect();
                const v = window.visualViewport;
                const x = r.left + r.width / 2;
                const y = r.top + r.height / 2;
                return x >= v.offsetLeft && x < v.offsetLeft + v.width
                    && y >= v.offsetTop && y < v.offsetTop + v.height;
                """, elemento)));
    }

    protected WebElement campoInteractuable(By selector) {
        return wait.until(ExpectedConditions.elementToBeClickable(mostrar(selector)));
    }

    protected void completarCampo(By selector, String valor) {
        WebElement campo = campoInteractuable(selector);
        campo.clear();
        campo.sendKeys(valor);
        driver.executeScript("arguments[0].blur();", campo);
        encuadrar(campo);
    }
}
