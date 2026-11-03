# Automatizaci-n-Pichincha-Appium
Repositorio para practicar automatización con página de banco pichincha en Appium

## Requisitos

- JDK 25 (LTS) para compilar y ejecutar el proyecto.
- Maven 3.9 o posterior.
- Node.js y Appium 3 con el driver UiAutomator2 instalado.
- Un emulador Android con Chrome o un dispositivo Android conectado y habilitado para depuración USB.

## Estructura

- `base/`: configuración y ciclo de vida de sesiones Appium nativas y web.
- `config/`: lectura de propiedades de prueba.
- `pages/`: objetos de página con selectores y acciones de la interfaz.
- `tests/`: casos de prueba TestNG.
- `reporting/`: reporte HTML autocontenido y registro de pasos y capturas.
- `resources/`: configuración local y suite TestNG.

## Prueba de proyecto de vivienda

La prueba abre el simulador hipotecario en Chrome móvil, comprueba que el botón **Siguiente**
esté deshabilitado mientras faltan datos, selecciona **Pareja**, **Apartamento** y **Quito**.
Ajusta los deslizadores a un mínimo de **50000** y un máximo de **1000000**, verifica ambos
valores y pulsa **Siguiente** para validar el avance a **Tu situación financiera**.

En la sección financiera completa, en este orden: **Empleado**, ingresos netos mensuales
**500000**, ahorro para cuota inicial **300000**, fecha de nacimiento **03/08/1987**
(3 de agosto de 1987) y estado civil **Casado**. Verifica los valores y el estado del botón,
toma una captura, pulsa **Siguiente** y comprueba el avance a **Personaliza tu crédito de vivienda**.
Los selectores y acciones están separados en `SituacionFinancieraPage`.
La fecha se establece en el input HTML como `1987-08-03` con eventos `input` y `change`
para evitar diferencias de idioma del calendario de Android, sin desactivar validaciones.

El flujo continua en **Personaliza tu credito de vivienda** con monto **200000000**,
plazo **20 años** y cuota **Fija**. Comprueba los valores, pulsa **Siguiente**, verifica
**Tus resultados** y pulsa **¿Quieres conocer tu capacidad real?**.
En **Tu capacidad real** completa nombre, documento, correo y telefono con los datos
del escenario, marca la autorizacion de consulta crediticia, ingresa **3** dependientes
y la empresa **SoftwareOne**. Valida cada campo sin volver a resaltarlo y pulsa
**Siguiente** hasta **Quiero que me Contacten**. No completa ni envia la seccion Contacto.
Los Page Objects son `PersonalizarCreditoPage`, `ResultadosPage` y `CapacidadRealPage`.
Las entradas conservan su orden, la pausa de 1,5 segundos y una sola entrada por paso
en el reporte, con capturas adicionales de personalizacion, resultados y capacidad real.
Las capturas de capacidad real contienen los datos introducidos; no compartas los
reportes sin revisarlos y usa este escenario solo en el entorno de pruebas autorizado.

Para ejecutar únicamente este flujo:

```shell
mvn -Dtest=ProyectoViviendaTest test
```

Instala Appium y el driver Android una vez:

```shell
npm install --global appium
appium driver install uiautomator2
```

Inicia el AVD configurado o conecta un dispositivo Android con Chrome:

```shell
emulator -avd appium-api35
```

### Presentacion del emulador

Con el emulador encendido, aplica una vez el perfil visual desde la raiz del proyecto:

```powershell
.\scripts\Configurar-Emulador.ps1
# Si adb no esta en PATH:
.\scripts\Configurar-Emulador.ps1 -AdbPath "$env:ANDROID_HOME\platform-tools\adb.exe"
```

El perfil usa **1080x1920 pixeles, 240 dpi, texto al 100% y orientacion vertical fija**.
Ofrece 720x1280 dp (Chrome dispone de menos altura por sus barras), en lugar de los
aproximadamente 411 dp de ancho originales a 420 dpi. Asi cabe el indicador de pasos
y los controles anchos sin cambiar CSS, fuentes del sitio, zoom de Chrome ni datos.
Al mostrar mas contenido los elementos se ven mas pequenos que con 420 dpi:
amplia la ventana del emulador para leerlos comodamente. El desplazamiento vertical
sigue disponible; no se fuerza toda la pagina a caber en una sola pantalla.

Android conserva los ajustes al reiniciar el mismo AVD. El script comprueba que sea
un emulador y verifica el resultado; usa `-Serial` para elegir otro AVD conectado.
No se ejecuta en cada paso ni cambia el comportamiento de las pruebas.
Para restaurar el perfil original de este AVD:

```powershell
adb -s emulator-5554 shell wm size reset
adb -s emulator-5554 shell wm density reset
adb -s emulator-5554 shell settings put system font_scale 1.0
adb -s emulator-5554 shell settings put system accelerometer_rotation 1
adb -s emulator-5554 shell settings put system user_rotation 0
```

En una terminal de PowerShell, inicia Appium y habilita la descarga automática del
ChromeDriver compatible con la versión de Chrome instalada:

```powershell
$env:NODE_USE_SYSTEM_CA = "1"
appium --address 127.0.0.1 --port 4723 --allow-insecure uiautomator2:chromedriver_autodownload
```

En otra terminal, ejecuta la suite:

```shell
mvn test
```

La suite predeterminada ejecuta solo el flujo web en **Google Chrome**. No ejecuta
`SmokeTest`, que abre Ajustes de Android; esa comprobacion nativa sigue disponible
de forma explicita con `mvn -Dtest=SmokeTest test`.
La sesion web conserva los datos de Chrome (`noReset=true`) y evita detenerlo al
iniciar (`dontStopAppOnReset=true`). Antes de conectar ChromeDriver, una sesion
Appium breve abre Chrome y se cierra sin terminar la aplicacion. La sesion web se
conecta al navegador ya abierto (`chromeUseRunningApp=true`) durante el flujo;
ambas sesiones se liberan. Este arranque usa Chrome estable
(`com.android.chrome`), que debe estar instalado y configurado sin pantallas de bienvenida.
La prueba siempre navega de nuevo al simulador.
Al finalizar cada prueba, aprobada o fallida, primero se guarda la captura final,
despues se detiene Chrome mediante Appium, se verifica que no este ejecutandose
y se vuelve a Inicio antes de liberar la sesion. En `SmokeTest` se cierra unicamente
la aplicacion indicada por `appPackage`. Los errores de limpieza hacen fallar el cierre
y aparecen como fallos de configuracion en el reporte.
No se borran datos de Chrome, certificados, configuraciones ni aplicaciones ajenas
a la prueba; tampoco se detienen los servicios de Android. Una tarjeta en Recientes
puede conservarse aunque la aplicacion este detenida: no indica un proceso activo.
El emulador y el servidor Appium permanecen encendidos para la siguiente ejecucion.
El arranque del emulador y la preparacion interna de Android/Appium pueden mostrar
transiciones del sistema antes de comenzar; no son pasos de la prueba.

Cada campo o boton se desplaza al centro del area visible antes de usarlo y se marca
con un contorno dorado. Se comprueba que su centro este dentro del viewport real
de Chrome. Solo las acciones de entrada o navegacion resaltan el elemento:
las validaciones leen los valores sin desplazar, enfocar ni remarcar los campos
anteriores. Al terminar de escribir se reencuadra el mismo campo si el teclado
cambio el area visible, sin aplicar de nuevo el resaltado.
Los precios minimo y maximo tienen pasos
separados para observar cada ajuste durante la pausa de 1,5 segundos.
El resaltado solo cambia la presentacion, no los valores ni las validaciones del sitio.

La URL del servidor Appium, el dispositivo y la URL web se pueden sobrescribir
con propiedades Maven, por ejemplo:

```shell
mvn test -Dappium.server.url=http://127.0.0.1:4723 -DdeviceName=emulator-5554
```

Cada paso registrado con `paso(...)` incluye una pausa visual de **1,5 segundos**
despues de completar correctamente su accion o validacion. La duracion del paso
en el reporte incluye esta pausa. No sustituye las esperas de disponibilidad de elementos
ni se aplica al preparar/cerrar la sesion o tomar capturas.
Se configura con `step.pause.ms=1500` en `config.properties`; acepta milisegundos
no negativos. Por ejemplo, `mvn test -Dstep.pause.ms=0` desactiva la pausa.

En redes que inspeccionan HTTPS, instala en el emulador la CA aprobada por la organización
como certificado de usuario desde **Ajustes > Seguridad y privacidad > Más seguridad y
privacidad > Seguridad > Cifrado y credenciales > Instalar un certificado > Certificado de CA**.
Android puede requerir un PIN de pantalla para instalarlo. No desactives la validación TLS.

## Reporte de la ultima ejecucion

Cada `mvn test` (tambien con `-Dtest=ProyectoViviendaTest`) genera automaticamente
el [reporte visual de ejecucion](target/surefire-reports/reporte-ejecucion.html).
Incluye:

- Tarjetas de aprobadas, fallidas, omitidas, tasa de aprobacion y errores de configuracion.
- Grafica de distribucion de resultados y barras de duracion por prueba.
- Horarios, duracion total del contexto de pruebas (incluye configuracion), Java y sistema operativo.
- Plataforma, dispositivo y navegador informados por la sesion real de Appium.
- Descripcion del escenario, pasos con estado y duracion, datos y validaciones.
  Cada paso tiene una sola entrada final (aprobado o fallido), sin duplicados de
  inicio ni entradas adicionales de resaltado. Las validaciones se conservan
  como pasos de comprobacion, sin repetir la accion en pantalla.
- Capturas embebidas antes de Siguiente y al finalizar cada prueba, incluso si falla.
- Causas y trazas de errores, diferenciando fallos de pruebas y de preparacion/cierre.

El HTML es autocontenido: no requiere CDN, servidor ni internet para ver las graficas y
capturas. Si no se puede obtener una captura, el reporte lo indica y se registra una
advertencia en consola; el resultado de la prueba no se oculta.
Las evidencias pueden contener datos de la aplicacion: revisalas antes de compartirlas.
Para agregar escenarios, usa `paso(descripcion, accion)` y `capturarEvidencia(titulo)`
heredados de `BaseTest`.

Los reportes originales de TestNG (`index.html`, `emailable-report.html`,
`testng-results.xml`) y los XML/TXT de Surefire se conservan en la misma carpeta.

Maven elimina exclusivamente la carpeta de reportes anteriores al iniciar el ciclo de
ejecucion, sin borrar las clases compiladas. Asi, los reportes reflejan solo la ultima
ejecucion, incluso cuando se ejecuta una sola clase sin usar `mvn clean`.
Si el proceso falla antes de ejecutar TestNG, no habra un reporte nuevo; revisa la consola.
No ejecutes dos procesos Maven simultaneamente sobre este mismo proyecto.

Para abrir el reporte desde PowerShell, ubicado en la raiz del proyecto:

```powershell
Start-Process ".\target\surefire-reports\reporte-ejecucion.html"
```

Esta configuracion se aplica a ejecuciones mediante Maven; no a lanzamientos directos de
TestNG desde el IDE. Los reportes son archivos generados y no se incluyen en Git.

Para validar el generador del reporte sin Appium:

```shell
mvn -Dtest=ReporteEjecucionTest test
```

Esta comprobacion usa escenarios sinteticos aprobados, fallidos y omitidos y un fallo
de configuracion intencional; no representa una ejecucion funcional del simulador.
Para regenerar despues el reporte de pruebas moviles, ejecuta `mvn test`.
