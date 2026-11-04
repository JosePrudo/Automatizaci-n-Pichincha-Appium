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

public class ProyectoViviendaTest extends WebBaseTest {

    @Test(description = "Completa la simulacion hipotecaria hasta Contacto, Confirmacion y respuesta del envio")
    public void completarProyectoDeVivienda() {
        paso("Abrir el simulador hipotecario en Chrome Android", () -> driver.get(ConfigReader.get(
                "web.base.url",
                "https://brave-mud-0605ba20f.3.azurestaticapps.net/")));

        ProyectoViviendaPage pagina = new ProyectoViviendaPage(
                driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        paso("Mostrar la seccion Tu proyecto de vivienda", pagina::esperarSeccionVisible);

        paso("Validar Siguiente deshabilitado mientras faltan datos", () ->
                Assert.assertFalse(pagina.siguienteEstaHabilitado(),
                        "El botón Siguiente debe permanecer deshabilitado mientras faltan datos"));

        paso("Seleccionar aplicacion: Pareja", pagina::seleccionarPareja);
        paso("Seleccionar vivienda: Apartamento", pagina::seleccionarApartamento);
        paso("Seleccionar ciudad: Quito", () -> pagina.seleccionarCiudad("Quito"));
        paso("Ajustar precio minimo: 50000", pagina::seleccionarPrecioMinimo);
        paso("Ajustar precio maximo: 1000000", pagina::seleccionarPrecioMaximo);

        paso("Validar ciudad esperada: Quito", () ->
                Assert.assertEquals(pagina.ciudadSeleccionada(), "Quito",
                        "La ciudad seleccionada debe ser Quito"));
        paso("Validar precio minimo esperado: 50000", () ->
                Assert.assertEquals(pagina.precioMinimoSeleccionado(), 50000,
                        "El precio minimo debe ser 50000"));
        paso("Validar precio maximo esperado: 1000000", () ->
                Assert.assertEquals(pagina.precioMaximoSeleccionado(), 1000000,
                        "El precio maximo debe ser 1000000"));

        paso("Validar Siguiente habilitado con los datos completos", () ->
                Assert.assertTrue(pagina.siguienteEstaHabilitado(),
                        "El botón Siguiente debe habilitarse al completar los datos del proyecto"));
        capturarEvidencia("Proyecto completo: Pareja, Apartamento, Quito, 50000 - 1000000");

        paso("Pulsar Siguiente", pagina::continuar);

        paso("Validar avance a Tu situacion financiera", () ->
                Assert.assertTrue(pagina.seccionFinancieraVisible(),
                        "Después del proyecto debe mostrarse la sección Tu situación financiera"));

        SituacionFinancieraPage finanzas = new SituacionFinancieraPage(
                driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        paso("Mostrar la seccion Tu situacion financiera", finanzas::esperarSeccionVisible);
        paso("Validar Siguiente deshabilitado con datos financieros incompletos", () ->
                Assert.assertFalse(finanzas.siguienteEstaHabilitado(),
                        "Siguiente debe estar deshabilitado antes de completar las finanzas"));

        paso("Seleccionar situacion laboral: Empleado", finanzas::seleccionarEmpleado);
        paso("Ingresar ingresos netos mensuales: 500000", () ->
                finanzas.ingresarIngresosNetos("500000"));
        paso("Ingresar ahorro para cuota inicial: 300000", () ->
                finanzas.ingresarAhorro("300000"));
        paso("Ingresar fecha de nacimiento: 03/08/1987 (dia/mes/ano)", () ->
                finanzas.ingresarFechaNacimiento(LocalDate.of(1987, 8, 3)));
        paso("Seleccionar estado civil: Casado", () ->
                finanzas.seleccionarEstadoCivil("Casado"));

        paso("Validar situacion laboral Empleado seleccionada", () ->
                Assert.assertTrue(finanzas.empleadoSeleccionado(),
                        "La situacion laboral debe ser Empleado"));
        paso("Validar ingresos netos esperados: 500000", () ->
                Assert.assertEquals(finanzas.ingresosNetosIngresados(), "$500,000.00",
                        "Los ingresos netos deben ser 500000"));
        paso("Validar ahorro esperado: 300000", () ->
                Assert.assertEquals(finanzas.ahorroIngresado(), "$300,000.00",
                        "El ahorro para cuota inicial debe ser 300000"));
        paso("Validar fecha esperada: 3 de agosto de 1987", () ->
                Assert.assertEquals(finanzas.fechaNacimientoIngresada(), LocalDate.of(1987, 8, 3),
                        "La fecha de nacimiento debe ser 03/08/1987"));
        paso("Validar estado civil esperado: Casado", () ->
                Assert.assertEquals(finanzas.estadoCivilSeleccionado(), "Casado",
                        "El estado civil debe ser Casado"));
        paso("Validar Siguiente habilitado con los datos financieros completos", () ->
                Assert.assertTrue(finanzas.siguienteEstaHabilitado(),
                        "Siguiente debe habilitarse al completar las finanzas"));
        capturarEvidencia("Situacion financiera completa antes de Siguiente");
        paso("Pulsar Siguiente en Tu situacion financiera", finanzas::continuar);
        paso("Validar avance a Personaliza tu credito de vivienda", () ->
                Assert.assertTrue(finanzas.seccionPersonalizacionVisible(),
                        "Despues de las finanzas debe mostrarse Personaliza tu credito de vivienda"));

        Duration espera = Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15));
        PersonalizarCreditoPage credito = new PersonalizarCreditoPage(driver, espera);
        paso("Ingresar monto del credito: 200000000", () -> {
            credito.ingresarMonto("200000000");
            Assert.assertEquals(credito.montoIngresado(), "$200,000,000.00",
                    "El monto del credito debe conservar los 200000000 ingresados");
        });
        paso("Seleccionar plazo: 20 años", () -> {
            credito.seleccionarPlazo("20 años");
            Assert.assertEquals(credito.plazoSeleccionado(), "20 años");
        });
        paso("Seleccionar cuota: Fija", () -> {
            credito.seleccionarCuotaFija();
            Assert.assertTrue(credito.cuotaFijaSeleccionada(), "Debe quedar seleccionada la cuota Fija");
        });
        capturarEvidencia("Personalizacion completa antes de Siguiente");
        paso("Pulsar Siguiente y validar Tus resultados", () -> {
            credito.continuar();
            Assert.assertTrue(credito.resultadosVisibles(), "Debe mostrarse Tus resultados");
        });
        capturarEvidencia("Tus resultados");

        ResultadosPage resultados = new ResultadosPage(driver, espera);
        paso("Pulsar ¿Quieres conocer tu capacidad real?", () -> {
            resultados.conocerCapacidadReal();
            Assert.assertTrue(resultados.capacidadRealVisible(), "Debe mostrarse Tu capacidad real");
        });

        CapacidadRealPage capacidad = new CapacidadRealPage(driver, espera);
        paso("Ingresar nombre completo en Tu capacidad real", () -> {
            capacidad.ingresarNombre("Jose Prudencio Padilla Berrio");
            Assert.assertEquals(capacidad.nombreIngresado(), "Jose Prudencio Padilla Berrio");
        });
        paso("Ingresar documento de identidad en Tu capacidad real", () -> {
            capacidad.ingresarDocumento("1234567890");
            Assert.assertEquals(capacidad.documentoIngresado(), "1234567890");
        });
        paso("Ingresar correo de prueba en Tu capacidad real", () -> {
            capacidad.ingresarCorreo("prueba@ejemplo.com");
            Assert.assertEquals(capacidad.correoIngresado(), "prueba@ejemplo.com");
        });
        paso("Ingresar telefono celular en Tu capacidad real", () -> {
            capacidad.ingresarTelefono("1234567098");
            Assert.assertEquals(capacidad.telefonoIngresado(), "1234567098");
        });
        paso("Autorizar consulta del historial crediticio para esta simulacion", () -> {
            capacidad.autorizarConsultaCrediticia();
            Assert.assertTrue(capacidad.consultaAutorizada(), "La autorizacion debe quedar marcada");
        });
        paso("Ingresar personas dependientes: 3", () -> {
            capacidad.ingresarDependientes("3");
            Assert.assertEquals(capacidad.dependientesIngresados(), "3");
        });
        paso("Ingresar empresa: SoftwareOne", () -> {
            capacidad.ingresarEmpresa("SoftwareOne");
            Assert.assertEquals(capacidad.empresaIngresada(), "SoftwareOne");
        });
        capturarEvidencia("Tu capacidad real completa antes de Siguiente");
        paso("Pulsar Siguiente y validar Quiero que me Contacten", () -> {
            capacidad.continuar();
            Assert.assertTrue(capacidad.contactoVisible(), "Debe mostrarse Quiero que me Contacten");
        });

        ContactoPage contacto = new ContactoPage(driver, espera);
        paso("Seleccionar horario de contacto: Mañana", () -> {
            contacto.seleccionarManana();
            Assert.assertTrue(contacto.mananaSeleccionada(), "Debe quedar seleccionado Mañana");
        });
        paso("Seleccionar medio de contacto: WhatsApp", () -> {
            contacto.seleccionarWhatsApp();
            Assert.assertTrue(contacto.whatsAppSeleccionado(), "Debe quedar seleccionado WhatsApp");
        });
        paso("Marcar Quiero recibir informacion de otros productos del banco", () -> {
            contacto.marcarInformacionProductos();
            Assert.assertTrue(contacto.informacionProductosMarcada(),
                    "Debe quedar marcada la opcion de informacion de productos");
        });
        capturarEvidencia("Preferencias de contacto antes de Siguiente");
        paso("Pulsar Siguiente y validar Confirmacion", () -> {
            contacto.continuar();
            Assert.assertTrue(contacto.confirmacionVisible(), "Debe mostrarse Confirmacion");
        });

        ConfirmacionPage confirmacion = new ConfirmacionPage(driver, espera);
        paso("Confirmar que toda la informacion esta correcta y completa", () -> {
            confirmacion.confirmarInformacion();
            Assert.assertTrue(confirmacion.informacionConfirmada(),
                    "Debe quedar marcada la confirmacion de informacion");
        });
        capturarEvidencia("Confirmacion antes de Aceptar y Enviar");
        paso("Pulsar Aceptar y Enviar y esperar respuesta", confirmacion::aceptarYEnviar);
        capturarEvidencia("Respuesta del envio de la simulacion");
        paso("Pulsar Aceptar final y validar envio exitoso", () -> {
            boolean exitoso = confirmacion.envioExitoso();
            String respuesta = confirmacion.respuestaDelEnvio();
            confirmacion.aceptarRespuesta();
            Assert.assertTrue(confirmacion.simuladorReiniciado(),
                    "Aceptar debe cerrar la respuesta y reiniciar el simulador");
            Assert.assertTrue(exitoso, "El servidor no confirmo un envio exitoso: " + respuesta);
        });
    }
}
