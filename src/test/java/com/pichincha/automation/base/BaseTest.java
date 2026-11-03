package com.pichincha.automation.base;

import com.pichincha.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Clase base que inicializa y cierra la sesion de AndroidDriver para cada prueba.
 */
public abstract class BaseTest {

    protected AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        driver = new AndroidDriver(getServerUrl(), createOptions());
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicitWait", 10)));
    }

    protected UiAutomator2Options createOptions() {
        UiAutomator2Options options = createBaseOptions();

        String appPackage = ConfigReader.get("appPackage");
        String appActivity = ConfigReader.get("appActivity");
        if (appPackage != null && !appPackage.isBlank()) {
            options.setAppPackage(appPackage);
        }
        if (appActivity != null && !appActivity.isBlank()) {
            options.setAppActivity(appActivity);
        }

        return options;
    }

    protected UiAutomator2Options createBaseOptions() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.get("platformName", "Android"))
                .setAutomationName(ConfigReader.get("automationName", "UiAutomator2"))
                .setDeviceName(ConfigReader.get("deviceName", "appium-api35"))
                .setNewCommandTimeout(Duration.ofSeconds(
                        ConfigReader.getInt("newCommandTimeout", 120)));

        String platformVersion = ConfigReader.get("platformVersion");
        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }

        return options;
    }

    protected URL getServerUrl() throws MalformedURLException {
        return URI.create(
                ConfigReader.get("appium.server.url", "http://127.0.0.1:4723")).toURL();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
