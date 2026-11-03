package com.pichincha.automation.base;

import com.pichincha.automation.config.ConfigReader;
import com.pichincha.automation.reporting.ReporteEjecucion;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.appmanagement.ApplicationState;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Clase base que inicializa y cierra la sesion de AndroidDriver para cada prueba.
 */
@Listeners(ReporteEjecucion.class)
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
    public void tearDown(ITestResult resultado) {
        try {
            ReporteEjecucion.capturar(driver, "Pantalla al finalizar: " + resultado.getName(), resultado);
        } finally {
            if (driver != null) {
                try {
                    try {
                        cerrarAplicacionDePrueba();
                        resultado.setAttribute("reporte.limpieza",
                                "Aplicacion de prueba detenida y verificada. Emulador en Inicio.");
                    } finally {
                        driver.quit();
                    }
                } finally {
                    driver = null;
                }
            }
        }
    }

    protected String paqueteAplicacionDePrueba() {
        return ConfigReader.get("appPackage");
    }

    protected void cerrarAplicacionDePrueba() {
        if (driver.getSessionId() == null) {
            throw new IllegalStateException("No hay sesion Appium para limpiar la aplicacion");
        }
        String paquete = paqueteAplicacionDePrueba();
        if (paquete == null || paquete.isBlank()) {
            throw new IllegalStateException("No se configuro el paquete de la aplicacion para cerrar");
        }
        driver.context("NATIVE_APP");
        driver.terminateApp(paquete);
        if (driver.queryAppState(paquete) != ApplicationState.NOT_RUNNING) {
            throw new IllegalStateException("La aplicacion sigue ejecutandose tras el cierre: " + paquete);
        }
        driver.pressKey(new KeyEvent(AndroidKey.HOME));
    }

    protected void paso(String descripcion, Runnable accion) {
        ReporteEjecucion.paso(descripcion, () -> {
            int pausaMilisegundos = ConfigReader.getInt("step.pause.ms", 1500);
            if (pausaMilisegundos < 0) {
                throw new IllegalArgumentException("step.pause.ms no puede ser negativo");
            }
            accion.run();
            try {
                Thread.sleep(pausaMilisegundos);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Se interrumpio la pausa entre pasos", error);
            }
        });
    }

    protected void capturarEvidencia(String titulo) {
        ReporteEjecucion.capturar(driver, titulo, Reporter.getCurrentTestResult());
    }
}
