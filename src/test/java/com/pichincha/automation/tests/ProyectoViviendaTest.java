package com.pichincha.automation.tests;

import com.pichincha.automation.base.WebBaseTest;
import com.pichincha.automation.config.ConfigReader;
import com.pichincha.automation.pages.ProyectoViviendaPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class ProyectoViviendaTest extends WebBaseTest {

    @Test(description = "Completa el proyecto de vivienda y avanza a la sección financiera")
    public void completarProyectoDeVivienda() {
        driver.get(ConfigReader.get(
                "web.base.url",
                "https://brave-mud-0605ba20f.3.azurestaticapps.net/"));

        ProyectoViviendaPage pagina = new ProyectoViviendaPage(
                driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        pagina.esperarSeccionVisible();

        Assert.assertFalse(pagina.siguienteEstaHabilitado(),
                "El botón Siguiente debe permanecer deshabilitado mientras faltan datos");

        pagina.seleccionarAplicacionIndividual();
        pagina.seleccionarCasa();
        pagina.seleccionarCiudad(ConfigReader.get("test.city", "Quito"));

        Assert.assertTrue(pagina.siguienteEstaHabilitado(),
                "El botón Siguiente debe habilitarse al completar los datos del proyecto");

        pagina.continuar();

        Assert.assertTrue(pagina.seccionFinancieraVisible(),
                "Después del proyecto debe mostrarse la sección Tu situación financiera");
    }
}
