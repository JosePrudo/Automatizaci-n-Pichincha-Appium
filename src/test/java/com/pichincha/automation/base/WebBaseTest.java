package com.pichincha.automation.base;

import com.pichincha.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;

public abstract class WebBaseTest extends BaseTest {

    @Override
    protected String paqueteAplicacionDePrueba() {
        return "com.android.chrome";
    }

    @Override
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        UiAutomator2Options inicioChrome = createBaseOptions()
                .setAppPackage("com.android.chrome")
                .setAppActivity("com.google.android.apps.chrome.Main")
                .setNoReset(true);
        inicioChrome.setCapability("appium:dontStopAppOnReset", true);
        inicioChrome.setCapability("appium:shouldTerminateApp", false);
        AndroidDriver preparacion = new AndroidDriver(getServerUrl(), inicioChrome);
        try {
            preparacion.activateApp("com.android.chrome");
        } finally {
            preparacion.quit();
        }
        super.setUp();
    }

    @Override
    protected UiAutomator2Options createOptions() {
        UiAutomator2Options options = createBaseOptions();
        options.setCapability("browserName", ConfigReader.get("browserName", "Chrome"));
        options.setNoReset(true);
        options.setCapability("appium:dontStopAppOnReset", true);
        options.setCapability("appium:shouldTerminateApp", false);
        options.setCapability("appium:chromeUseRunningApp", true);
        return options;
    }
}
