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
- `resources/`: configuración local y suite TestNG.

## Prueba de proyecto de vivienda

La prueba abre el simulador hipotecario en Chrome móvil, comprueba que el botón **Siguiente**
esté deshabilitado mientras faltan datos, selecciona **Individual**, **Casa** y una ciudad,
y valida el avance a **Tu situación financiera**.

Instala Appium y el driver Android una vez:

```shell
npm install --global appium
appium driver install uiautomator2
```

Inicia el AVD configurado o conecta un dispositivo Android con Chrome:

```shell
emulator -avd appium-api35
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

La URL del servidor Appium, el dispositivo, la URL web y la ciudad se pueden sobrescribir
con propiedades Maven, por ejemplo:

```shell
mvn test -Dappium.server.url=http://127.0.0.1:4723 -DdeviceName=emulator-5554 -Dtest.city=Quito
```

En redes que inspeccionan HTTPS, instala en el emulador la CA aprobada por la organización
como certificado de usuario desde **Ajustes > Seguridad y privacidad > Más seguridad y
privacidad > Seguridad > Cifrado y credenciales > Instalar un certificado > Certificado de CA**.
Android puede requerir un PIN de pantalla para instalarlo. No desactives la validación TLS.
