package com.pichincha.automation.tests;

import com.pichincha.automation.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Prueba de humo que verifica que la sesion de Appium se establece correctamente
 * contra el dispositivo/emulador Android.
 */
public class SmokeTest extends BaseTest {

    @Test(description = "La sesion de Appium se inicia y el dispositivo es Android")
    public void sesionAndroidActiva() {
        String platform = driver.getCapabilities()
                .getCapability("platformName").toString();
        Assert.assertEquals(platform.toLowerCase(), "android",
                "La plataforma de la sesion deberia ser Android");

        Assert.assertNotNull(driver.getSessionId(),
                "La sesion de Appium deberia estar activa");
    }
}
