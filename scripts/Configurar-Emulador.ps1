param(
    [string]$Serial = 'emulator-5554',
    [string]$AdbPath = 'adb'
)

$ErrorActionPreference = 'Stop'

function Invoke-Adb {
    param([string[]]$Argumentos)
    $salida = & $AdbPath -s $Serial @Argumentos 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo adb ($($Argumentos -join ' ')): $salida"
    }
    return ($salida -join "`n").Trim()
}

$esEmulador = Invoke-Adb -Argumentos @('shell', 'getprop', 'ro.kernel.qemu')
if ($esEmulador -ne '1') {
    throw 'Este perfil visual solo se aplica a emuladores Android, no a dispositivos fisicos.'
}

Invoke-Adb -Argumentos @('shell', 'wm', 'size', '1080x1920')
Invoke-Adb -Argumentos @('shell', 'wm', 'density', '240')
Invoke-Adb -Argumentos @('shell', 'settings', 'put', 'system', 'font_scale', '1.0')
Invoke-Adb -Argumentos @('shell', 'settings', 'put', 'system', 'accelerometer_rotation', '0')
Invoke-Adb -Argumentos @('shell', 'settings', 'put', 'system', 'user_rotation', '0')

$tamano = Invoke-Adb -Argumentos @('shell', 'wm', 'size')
$densidad = Invoke-Adb -Argumentos @('shell', 'wm', 'density')
$fuente = Invoke-Adb -Argumentos @('shell', 'settings', 'get', 'system', 'font_scale')
$rotacionAutomatica = Invoke-Adb -Argumentos @('shell', 'settings', 'get', 'system', 'accelerometer_rotation')
$rotacion = Invoke-Adb -Argumentos @('shell', 'settings', 'get', 'system', 'user_rotation')
if ($tamano -notmatch '1080x1920\s*$' -or $densidad -notmatch '240\s*$' `
        -or $fuente -ne '1.0' -or $rotacionAutomatica -ne '0' -or $rotacion -ne '0') {
    throw "No se pudo verificar el perfil visual: $tamano; $densidad; fuente=$fuente; rotacion=$rotacion"
}

Write-Output "Perfil aplicado a ${Serial}: 1080x1920, 240 dpi, 720x1280 dp, texto 100%, vertical."
Write-Output 'Android conserva estos ajustes al reiniciar. No se modificaron los estilos del sitio.'
