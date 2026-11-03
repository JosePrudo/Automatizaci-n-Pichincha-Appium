package com.pichincha.automation.reporting;

import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReporteEjecucionTest {

    @Test
    public void reportarEstadosErroresYPasosSinRecursosExternos() throws IOException {
        Path directorio = Files.createTempDirectory(Path.of("target"), "validacion-reporte-");
        Path archivo = directorio.resolve("reporte-ejecucion.html");
        try {
            TestNG ejecucion = new TestNG(false);
            ejecucion.setUseDefaultListeners(false);
            ejecucion.setOutputDirectory(directorio.toString());
            ejecucion.setTestClasses(new Class<?>[]{
                    EscenariosReporte.class, EscenariosReporte.ConfiguracionFallida.class});
            ejecucion.addListener(new ReporteEjecucion());
            ejecucion.setVerbose(0);
            ejecucion.run();

            Assert.assertTrue(ejecucion.hasFailure(), "Los fallos sinteticos deben conservarse");
            String html = Files.readString(archivo);
            Assert.assertTrue(html.contains("Pruebas<strong>4</strong>"));
            Assert.assertTrue(html.contains("Aprobadas<strong>1</strong>"));
            Assert.assertTrue(html.contains("Fallidas<strong>1</strong>"));
            Assert.assertTrue(html.contains("Omitidas<strong>2</strong>"));
            Assert.assertTrue(html.contains("Errores de configuracion<strong>1</strong>"));
            Assert.assertTrue(html.contains("25.0%"));
            Assert.assertTrue(html.contains("APROBADO | Paso &lt;seguro&gt;"));
            Assert.assertTrue(html.contains("FALLIDO | Paso con fallo"));
            Assert.assertEquals(html.split("<div class='step pass'>", -1).length - 1, 1,
                    "Cada paso aprobado debe tener una sola entrada");
            Assert.assertEquals(html.split("<div class='step fail'>", -1).length - 1, 1,
                    "Cada paso fallido debe tener una sola entrada");
            Assert.assertFalse(html.contains("INICIO |"),
                    "El reporte no debe duplicar el paso con una entrada de inicio");
            Assert.assertFalse(html.contains("CAMPO VISIBLE |"),
                    "El reporte no debe agregar pasos extra de resaltado");
            Assert.assertTrue(html.contains("Fallo controlado &lt;esperado&gt;"));
            Assert.assertTrue(html.contains("Appium no disponible (simulado)"));
            Assert.assertTrue(html.contains("<svg"));
            Assert.assertTrue(html.contains("stroke-dasharray='25.0000 75.0000'"));
            Assert.assertFalse(html.contains("<seguro>"));
            Assert.assertFalse(html.contains("<script"));
            Assert.assertFalse(html.contains("<link"));
        } finally {
            Files.deleteIfExists(archivo);
            Files.delete(directorio);
        }
    }

}
