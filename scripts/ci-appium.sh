#!/usr/bin/env bash
set -euo pipefail

mkdir -p target
appium_pid=""
cleanup() {
  if [[ -n "$appium_pid" ]]; then
    kill "$appium_pid" 2>/dev/null || true
    wait "$appium_pid" 2>/dev/null || true
  fi
}
trap cleanup EXIT

adb shell wm size 1080x1920
adb shell wm density 240
adb shell settings put system font_scale 1.0
adb shell settings put system accelerometer_rotation 0
adb shell settings put system user_rotation 0
adb shell pm path com.android.chrome | grep -q '^package:'

appium --address 127.0.0.1 --port 4723 \
  --allow-insecure uiautomator2:chromedriver_autodownload \
  >target/ci-appium.log 2>&1 &
appium_pid=$!

ready=false
for attempt in {1..30}; do
  if ! kill -0 "$appium_pid" 2>/dev/null; then
    echo "::error::Appium termino antes de estar disponible."
    exit 1
  fi
  if curl --silent --fail http://127.0.0.1:4723/status \
    | python3 -c 'import json,sys; sys.exit(0 if json.load(sys.stdin)["value"]["ready"] else 1)' 2>/dev/null; then
    ready=true
    break
  fi
  sleep 2
done
if [[ "$ready" != true ]]; then
  echo "::error::Appium no estuvo disponible en 60 segundos."
  exit 1
fi

if mvn --batch-mode --no-transfer-progress \
  -Dtest=ProyectoViviendaTest \
  -DdeviceName=emulator-5554 \
  -DplatformVersion=15 \
  -Dchrome.useRunningApp=false \
  test >target/ci-maven.log 2>&1; then
  echo "Flujo funcional aprobado."
else
  echo "::error::El flujo funcional fallo. Consulta el resumen de resultados. Los logs se mantienen privados en el runner efimero."
  exit 1
fi
