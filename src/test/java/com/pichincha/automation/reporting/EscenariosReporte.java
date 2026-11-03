package com.pichincha.automation.reporting;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// Escenarios sinteticos: solo se ejecutan desde ReporteEjecucionTest.
public class EscenariosReporte {
    @Test
    public void aprobada() {
        ReporteEjecucion.paso("Paso <seguro>", () -> Assert.assertTrue(true));
    }

    @Test
    public void fallida() {
        ReporteEjecucion.paso("Paso con fallo", () -> Assert.fail("Fallo controlado <esperado>"));
    }

    @Test
    public void omitida() {
        throw new SkipException("Omision controlada para validar el reporte");
    }

    public static class ConfiguracionFallida {
        @BeforeMethod
        public void preparar() {
            throw new IllegalStateException("Appium no disponible (simulado)");
        }

        @Test
        public void omitidaPorConfiguracion() {
            Assert.fail("La prueba no debe ejecutarse si falla su preparacion");
        }
    }
}
