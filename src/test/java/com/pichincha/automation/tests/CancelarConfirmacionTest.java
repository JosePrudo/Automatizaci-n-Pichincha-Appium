package com.pichincha.automation.tests;

import com.pichincha.automation.base.WebBaseTest;
import com.pichincha.automation.config.ConfigReader;
import com.pichincha.automation.pages.ProyectoViviendaPage;
import com.pichincha.automation.pages.SituacionFinancieraPage;
import com.pichincha.automation.pages.PersonalizarCreditoPage;
import com.pichincha.automation.pages.ResultadosPage;
import com.pichincha.automation.pages.CapacidadRealPage;
import com.pichincha.automation.pages.ContactoPage;
import com.pichincha.automation.pages.ConfirmacionPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;

public class CancelarConfirmacionTest extends WebBaseTest {

    @Test(description = "Completa la simulacion hasta Confirmacion y pulsa Cancelar para reiniciar el wizard")
    public void cancelarEnConfirmacion() {
        paso("Abrir el simulador hipotecario en Chrome Android", () -> driver.get(ConfigReader.get(
                "web.base.url",
                "https://brave-mud-0605ba20f.3.azurestaticapps.net/")));

        Duration espera = Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15));

        ProyectoViviendaPage pagina = new ProyectoViviendaPage(driver, espera);
        paso("Mostrar la seccion Tu proyecto de vivienda", pagina::esperarSeccionVisible);
        paso("Seleccionar aplicacion: Pareja", pagina::seleccionarPareja);
        paso("Seleccionar vivienda: Apartamento", pagina::seleccionarApartamento);
        paso("Seleccionar ciudad: Quito", () -> pagina.seleccionarCiudad("Quito"));
        paso("Ajustar precio minimo: 50000", pagina::seleccionarPrecioMinimo);
        paso("Ajustar precio maximo: 1000000", pagina::seleccionarPrecioMaximo);
        paso("Pulsar Siguiente", pagina::continuar);
        paso("Validar avance a Tu situacion financiera", () ->
                Assert.assertTrue(pagina.seccionFinancieraVisible(),
                        "Después del proyecto debe mostrarse la sección Tu situación financiera"));

        SituacionFinancieraPage finanzas = new SituacionFinancieraPage(driver, espera);
        paso("Mostrar la seccion Tu situacion financiera", finanzas::esperarSeccionVisible);
        paso("Seleccionar situacion laboral: Empleado", finanzas::seleccionarEmpleado);
        paso("Ingresar ingresos netos mensuales: 500000", () ->
                finanzas.ingresarIngresosNetos("500000"));
        paso("Ingresar ahorro para cuota inicial: 300000", () ->
                finanzas.ingresarAhorro("300000"));
        paso("Ingresar fecha de nacimiento: 03/08/1987 (dia/mes/ano)", () ->
                finanzas.ingresarFechaNacimiento(LocalDate.of(1987, 8, 3)));
        paso("Seleccionar estado civil: Casado", () ->
                finanzas.seleccionarEstadoCivil("Casado"));
        paso("Pulsar Siguiente en Tu situacion financiera", finanzas::continuar);
        paso("Validar avance a Personaliza tu credito de vivienda", () ->
                Assert.assertTrue(finanzas.seccionPersonalizacionVisible(),
                        "Despues de las finanzas debe mostrarse Personaliza tu credito de vivienda"));

        PersonalizarCreditoPage credito = new PersonalizarCreditoPage(driver, espera);
        paso("Ingresar monto del credito: 200000000", () -> credito.ingresarMonto("200000000"));
        paso("Seleccionar plazo: 20 años", () -> credito.seleccionarPlazo("20 años"));
        paso("Seleccionar cuota: Fija", credito::seleccionarCuotaFija);
        paso("Pulsar Siguiente y validar Tus resultados", () -> {
            credito.continuar();
            Assert.assertTrue(credito.resultadosVisibles(), "Debe mostrarse Tus resultados");
        });

        ResultadosPage resultados = new ResultadosPage(driver, espera);
        paso("Pulsar ¿Quieres conocer tu capacidad real?", () -> {
            resultados.conocerCapacidadReal();
            Assert.assertTrue(resultados.capacidadRealVisible(), "Debe mostrarse Tu capacidad real");
        });

        CapacidadRealPage capacidad = new CapacidadRealPage(driver, espera);
        paso("Ingresar nombre completo en Tu capacidad real", () ->
                capacidad.ingresarNombre("Jose Prudencio Padilla Berrio"));
        paso("Ingresar documento de identidad en Tu capacidad real", () ->
                capacidad.ingresarDocumento("1234567890"));
        paso("Ingresar correo de prueba en Tu capacidad real", () ->
                capacidad.ingresarCorreo("prueba@ejemplo.com"));
        paso("Ingresar telefono celular en Tu capacidad real", () ->
                capacidad.ingresarTelefono("1234567098"));
        paso("Autorizar consulta del historial crediticio para esta simulacion",
                capacidad::autorizarConsultaCrediticia);
        paso("Ingresar personas dependientes: 3", () -> capacidad.ingresarDependientes("3"));
        paso("Ingresar empresa: SoftwareOne", () -> capacidad.ingresarEmpresa("SoftwareOne"));
        paso("Pulsar Siguiente y validar Quiero que me Contacten", () -> {
            capacidad.continuar();
            Assert.assertTrue(capacidad.contactoVisible(), "Debe mostrarse Quiero que me Contacten");
        });

        ContactoPage contacto = new ContactoPage(driver, espera);
        paso("Seleccionar horario de contacto: Mañana", contacto::seleccionarManana);
        paso("Seleccionar medio de contacto: WhatsApp", contacto::seleccionarWhatsApp);
        paso("Marcar Quiero recibir informacion de otros productos del banco",
                contacto::marcarInformacionProductos);
        paso("Pulsar Siguiente y validar Confirmacion", () -> {
            contacto.continuar();
            Assert.assertTrue(contacto.confirmacionVisible(), "Debe mostrarse Confirmacion");
        });
        capturarEvidencia("Seccion Confirmacion antes de pulsar Cancelar");

        ConfirmacionPage confirmacion = new ConfirmacionPage(driver, espera);
        paso("Pulsar Cancelar en Confirmacion y validar regreso a Tu proyecto de vivienda", () -> {
            confirmacion.cancelar();
            Assert.assertTrue(confirmacion.simuladorReiniciado(),
                    "Cancelar debe reiniciar el simulador a Tu proyecto de vivienda");
        });
        capturarEvidencia("Simulador reiniciado tras Cancelar");
    }
}
