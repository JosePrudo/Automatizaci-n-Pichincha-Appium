package com.pichincha.automation.reporting;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteEjecucion implements IReporter {

    private static final String EVIDENCIAS = "reporte.evidencias";
    private static final Logger LOGGER = Logger.getLogger(ReporteEjecucion.class.getName());
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

    private record Evidencia(String titulo, String base64) {
    }

    public static void paso(String descripcion, Runnable accion) {
        long inicio = System.nanoTime();
        try {
            accion.run();
            Reporter.log("APROBADO | " + descripcion + " | "
                    + segundos((System.nanoTime() - inicio) / 1_000_000) + " s");
        } catch (RuntimeException | AssertionError error) {
            Reporter.log("FALLIDO | " + descripcion + " | "
                    + segundos((System.nanoTime() - inicio) / 1_000_000) + " s | " + error);
            throw error;
        }
    }

    public static void capturar(AndroidDriver driver, String titulo, ITestResult resultado) {
        if (driver == null || driver.getSessionId() == null) {
            resultado.setAttribute("reporte.capturaError",
                    "Sin captura: no se pudo disponer de una sesion Appium activa.");
            return;
        }
        var capacidades = driver.getCapabilities();
        resultado.setAttribute("reporte.entorno",
                "Plataforma: " + capacidades.getPlatformName()
                        + " | Version: " + capacidades.getCapability("platformVersion")
                        + " | Dispositivo: " + capacidades.getCapability("deviceName")
                        + " | Navegador: " + capacidades.getBrowserName()
                        + " " + capacidades.getBrowserVersion());
        try {
            Evidencia evidencia = new Evidencia(titulo, driver.getScreenshotAs(OutputType.BASE64));
            List<Evidencia> evidencias = new ArrayList<>(evidencias(resultado));
            evidencias.add(evidencia);
            resultado.setAttribute(EVIDENCIAS, List.copyOf(evidencias));
        } catch (WebDriverException error) {
            resultado.setAttribute("reporte.capturaError", "No se pudo capturar: " + error);
            LOGGER.log(Level.WARNING, "No se pudo obtener la evidencia visual de " + titulo, error);
        }
    }

    private static List<Evidencia> evidencias(ITestResult resultado) {
        if (resultado.getAttribute(EVIDENCIAS) instanceof List<?> valores) {
            return valores.stream().filter(Evidencia.class::isInstance)
                    .map(Evidencia.class::cast).toList();
        }
        return List.of();
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        List<ITestResult> pruebas = new ArrayList<>();
        List<ITestResult> configuracionesFallidas = new ArrayList<>();
        for (ISuite suite : suites) {
            suite.getResults().values().forEach(resultado -> {
                var contexto = resultado.getTestContext();
                agregar(pruebas, contexto.getPassedTests());
                agregar(pruebas, contexto.getFailedTests());
                agregar(pruebas, contexto.getSkippedTests());
                agregar(pruebas, contexto.getFailedButWithinSuccessPercentageTests());
                agregar(configuracionesFallidas, contexto.getFailedConfigurations());
            });
        }
        pruebas.sort(Comparator.comparingLong(ITestResult::getStartMillis));
        long aprobadas = pruebas.stream().filter(p -> p.getStatus() == ITestResult.SUCCESS).count();
        long omitidas = pruebas.stream().filter(p -> p.getStatus() == ITestResult.SKIP).count();
        long fallidas = pruebas.size() - aprobadas - omitidas;
        long inicio = suites.stream().flatMap(s -> s.getResults().values().stream())
                .mapToLong(r -> r.getTestContext().getStartDate().getTime())
                .min().orElse(System.currentTimeMillis());
        long fin = suites.stream().flatMap(s -> s.getResults().values().stream())
                .mapToLong(r -> r.getTestContext().getEndDate().getTime()).max().orElse(inicio);
        StringBuilder html = new StringBuilder("""
                <!doctype html><html lang="es"><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Reporte de ejecucion - Pichincha</title><style>
                :root{font-family:Segoe UI,Arial,sans-serif;color:#142747;background:#eef2f7}
                body{max-width:1250px;margin:auto;padding:24px}h1,h2,h3{color:#10275b}
                header{background:#10275b;color:white;padding:28px;border-radius:14px}
                header h1{color:white;margin-top:0}section,.card{background:white;border-radius:12px;padding:22px;margin:18px 0}
                .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:16px}
                .card{margin:0;border-top:5px solid #10275b}.card strong{display:block;font-size:30px}
                .pass{color:#08784b}.fail{color:#b42336}.skip{color:#946200}
                table{width:100%;border-collapse:collapse}td,th{text-align:left;padding:12px;border-bottom:1px solid #dde4ee}
                .charts{display:grid;grid-template-columns:1fr 1fr;gap:20px}
                .legend span{display:inline-block;margin:8px}pre{white-space:pre-wrap;overflow-wrap:anywhere;background:#f3f5f8;padding:14px;border-radius:8px}
                .step{padding:10px;border-left:4px solid #9daac0;margin:6px 0;overflow-wrap:anywhere}
                .step.pass{border-color:#08784b}.step.fail{border-color:#b42336}
                img{max-width:320px;width:100%;border:1px solid #ccd5e2;border-radius:8px}
                figure{display:inline-block;vertical-align:top;margin:12px}figcaption{max-width:320px;margin-bottom:8px}
                summary{cursor:pointer;font-weight:600;padding:12px}a{color:#184eb0}
                .bar{background:#e5eaf2;border-radius:8px;overflow:hidden}.bar div{background:#285bbb;height:22px;min-width:2px}
                @media(max-width:700px){.charts{grid-template-columns:1fr}body{padding:10px}table{font-size:12px}}
                </style></head><body><header><h1>Resultados de automatizacion movil</h1>
                <p>Banco Pichincha | Appium + Java + TestNG | Ultima ejecucion</p>
                """);
        html.append("<p>Inicio: ").append(FECHA.format(Instant.ofEpochMilli(inicio)))
                .append("<br>Fin: ").append(FECHA.format(Instant.ofEpochMilli(fin)))
                .append("<br>Duracion total (incluye configuracion): ").append(segundos(fin - inicio))
                .append(" s</p></header><section><h2>Resumen ejecutivo</h2><div class='grid'>");
        tarjeta(html, "Pruebas", pruebas.size(), "");
        tarjeta(html, "Aprobadas", aprobadas, "pass");
        tarjeta(html, "Fallidas", fallidas, "fail");
        tarjeta(html, "Omitidas", omitidas, "skip");
        tarjeta(html, "Errores de configuracion", configuracionesFallidas.size(), "fail");
        html.append("</div><p>Tasa de aprobacion: <strong>")
                .append(pruebas.isEmpty() ? "N/A" : String.format(Locale.ROOT, "%.1f%%",
                        aprobadas * 100.0 / pruebas.size()))
                .append("</strong> (sobre todas las pruebas, incluidas las omitidas).</p>")
                .append("<p>Una prueba omitida no es una prueba aprobada. Los fallos de preparacion o cierre se muestran por separado.</p></section>")
                .append("<section class='charts'><div><h2>Distribucion de resultados</h2>");
        graficaEstados(html, aprobadas, fallidas, omitidas);
        html.append("</div><div><h2>Duracion por prueba</h2><p>Solo el metodo de prueba; excluye preparacion y cierre.</p>");
        long maxima = pruebas.stream().mapToLong(p -> p.getEndMillis() - p.getStartMillis())
                .max().orElse(1);
        for (ITestResult prueba : pruebas) {
            long duracion = Math.max(0, prueba.getEndMillis() - prueba.getStartMillis());
            html.append("<p>").append(escape(prueba.getName())).append(": ")
                    .append(segundos(duracion)).append(" s</p><div class='bar'><div style='width:")
                    .append(String.format(Locale.ROOT, "%.2f", duracion * 100.0 / Math.max(1, maxima)))
                    .append("%'></div></div>");
        }
        html.append("</div></section><section><h2>Entorno</h2><table>");
        fila(html, "Java", System.getProperty("java.version"));
        fila(html, "Sistema operativo", System.getProperty("os.name"));
        html.append("</table></section><section><h2>Detalle de pruebas</h2>");
        for (ITestResult prueba : pruebas) {
            detalle(html, prueba);
        }
        html.append("</section><section><h2>Fallos de configuracion</h2>");
        if (configuracionesFallidas.isEmpty()) {
            html.append("<p>No se registraron fallos de preparacion ni de cierre.</p>");
        } else {
            configuracionesFallidas.forEach(p -> detalle(html, p));
        }
        html.append("</section><footer>Reporte autocontenido: graficas y capturas embebidas, sin CDN ni recursos externos.</footer></body></html>");
        Path archivo = Path.of(outputDirectory).resolve("reporte-ejecucion.html");
        try {
            Files.createDirectories(archivo.getParent());
            Files.writeString(archivo, html, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new UncheckedIOException("No se pudo escribir el reporte " + archivo, error);
        }
    }

    private static void agregar(List<ITestResult> destino, IResultMap resultados) {
        destino.addAll(resultados.getAllResults());
    }

    private static void tarjeta(StringBuilder html, String titulo, long valor, String estilo) {
        html.append("<div class='card ").append(estilo).append("'>").append(titulo)
                .append("<strong>").append(valor).append("</strong></div>");
    }

    private static void graficaEstados(StringBuilder html, long aprobadas, long fallidas, long omitidas) {
        long total = aprobadas + fallidas + omitidas;
        html.append("<svg viewBox='0 0 240 240' width='240' role='img' aria-label='Distribucion de resultados'>")
                .append("<circle cx='120' cy='120' r='85' fill='none' stroke='#e5eaf2' stroke-width='30'/>");
        long acumulado = 0;
        long[] valores = {aprobadas, fallidas, omitidas};
        String[] colores = {"#08784b", "#b42336", "#d39719"};
        for (int i = 0; i < valores.length && total > 0; i++) {
            double porcentaje = valores[i] * 100.0 / total;
            html.append(String.format(Locale.ROOT,
                    "<circle cx='120' cy='120' r='85' fill='none' stroke='%s' stroke-width='30' pathLength='100' stroke-dasharray='%.4f %.4f' stroke-dashoffset='%.4f' transform='rotate(-90 120 120)'/>",
                    colores[i], porcentaje, 100 - porcentaje, -acumulado * 100.0 / total));
            acumulado += valores[i];
        }
        html.append("<text x='120' y='126' text-anchor='middle' font-size='28'>").append(total)
                .append("</text></svg><div class='legend'><span class='pass'>Aprobadas: ").append(aprobadas)
                .append("</span><span class='fail'>Fallidas: ").append(fallidas)
                .append("</span><span class='skip'>Omitidas: ").append(omitidas).append("</span></div>");
    }

    private static void detalle(StringBuilder html, ITestResult prueba) {
        String estado = switch (prueba.getStatus()) {
            case ITestResult.SUCCESS -> "APROBADA";
            case ITestResult.SKIP -> "OMITIDA";
            default -> "FALLIDA";
        };
        html.append("<details open><summary>").append(estado).append(" | ")
                .append(escape(prueba.getName())).append(" | ")
                .append(segundos(prueba.getEndMillis() - prueba.getStartMillis()))
                .append(" s</summary><table>");
        fila(html, "Clase", prueba.getTestClass().getName());
        fila(html, "Escenario", prueba.getMethod().getDescription());
        fila(html, "Inicio", FECHA.format(Instant.ofEpochMilli(prueba.getStartMillis())));
        if (prueba.getAttribute("reporte.entorno") instanceof String entorno) {
            fila(html, "Sesion real", entorno);
        }
        if (prueba.getAttribute("reporte.limpieza") instanceof String limpieza) {
            fila(html, "Limpieza final", limpieza);
        }
        html.append("</table><h3>Pasos y validaciones</h3>");
        List<String> pasos = Reporter.getOutput(prueba);
        if (pasos.isEmpty()) {
            html.append("<p>No se registraron pasos (revisa posibles fallos de configuracion).</p>");
        }
        for (String paso : pasos) {
            String estilo = paso.startsWith("APROBADO") ? "pass" : paso.startsWith("FALLIDO") ? "fail" : "";
            html.append("<div class='step ").append(estilo).append("'>").append(escape(paso)).append("</div>");
        }
        for (Evidencia evidencia : evidencias(prueba)) {
            html.append("<figure><figcaption>").append(escape(evidencia.titulo()))
                    .append("</figcaption><a download='evidencia.png' href='data:image/png;base64,")
                    .append(evidencia.base64()).append("'>Descargar en tamano original</a><br><img alt='")
                    .append(escape(evidencia.titulo())).append("' src='data:image/png;base64,")
                    .append(evidencia.base64()).append("'></figure>");
        }
        if (prueba.getAttribute("reporte.capturaError") instanceof String errorCaptura) {
            html.append("<p class='fail'>").append(escape(errorCaptura)).append("</p>");
        }
        if (prueba.getThrowable() != null) {
            StringWriter error = new StringWriter();
            prueba.getThrowable().printStackTrace(new PrintWriter(error));
            html.append("<h3>Causa y traza</h3><pre>").append(escape(error.toString())).append("</pre>");
        }
        html.append("</details>");
    }

    private static void fila(StringBuilder html, String etiqueta, String valor) {
        html.append("<tr><th>").append(escape(etiqueta)).append("</th><td>")
                .append(escape(valor)).append("</td></tr>");
    }

    private static String segundos(long milisegundos) {
        return String.format(Locale.ROOT, "%.2f", Math.max(0, milisegundos) / 1000.0);
    }

    private static String escape(String valor) {
        return valor == null ? "" : valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
