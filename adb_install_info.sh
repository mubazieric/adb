#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: adb_install_info.sh <apk_path> [device_serial]

Installs the APK via adb, then prints IMEI, battery status, signal strength,
and installation duration.

You can also set ADB_SERIAL to avoid passing a device serial each time.
USAGE
}

if [[ ${1:-} == "-h" || ${1:-} == "--help" ]]; then
  usage
  exit 0
fi

if [[ $# -lt 1 ]]; then
  usage
  exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
  echo "adb is required but was not found in PATH." >&2
  exit 1
fi

apk_path=$1
if [[ ! -f "$apk_path" ]]; then
  echo "APK not found: $apk_path" >&2
  exit 1
fi

serial=${2:-${ADB_SERIAL:-}}
serial_args=()
if [[ -n "$serial" ]]; then
  serial_args=(-s "$serial")
fi

adb "${serial_args[@]}" start-server >/dev/null

if [[ -z "$serial" ]]; then
  connected_devices=$(adb devices | awk 'NR>1 && $1 != "" && $2 == "device" {print $1}')
  device_count=$(echo "$connected_devices" | sed '/^$/d' | wc -l | tr -d ' ')
  if [[ "$device_count" -eq 0 ]]; then
    echo "No connected devices found. Provide a device serial or set ADB_SERIAL." >&2
    exit 1
  elif [[ "$device_count" -gt 1 ]]; then
    echo "Multiple devices detected. Provide a device serial or set ADB_SERIAL." >&2
    echo "Connected devices:" >&2
    echo "$connected_devices" >&2
    exit 1
  fi
fi

adb "${serial_args[@]}" wait-for-device

start_time=$(date +%s)
if ! adb "${serial_args[@]}" install -r -g "$apk_path"; then
  echo "APK installation failed." >&2
  exit 1
fi
end_time=$(date +%s)
install_duration=$((end_time - start_time))

imei="Unavailable"
if imei_raw=$(adb "${serial_args[@]}" shell "service call iphonesubinfo 1" 2>/dev/null); then
  imei_digits=$(echo "$imei_raw" | tr -cd '0-9')
  if [[ ${#imei_digits} -ge 14 ]]; then
    imei=${imei_digits:0:15}
  fi
fi

battery_dump=$(adb "${serial_args[@]}" shell dumpsys battery 2>/dev/null || true)
battery_level=$(echo "$battery_dump" | awk -F': ' '/level/ {print $2; exit}')
battery_status_code=$(echo "$battery_dump" | awk -F': ' '/status/ {print $2; exit}')
battery_status="Unavailable"
case "$battery_status_code" in
  1) battery_status="Unknown" ;;
  2) battery_status="Charging" ;;
  3) battery_status="Discharging" ;;
  4) battery_status="Not charging" ;;
  5) battery_status="Full" ;;
  *) battery_status=${battery_status_code:-Unavailable} ;;
esac

signal_dump=$(adb "${serial_args[@]}" shell dumpsys telephony.registry 2>/dev/null || true)
signal_strength=$(echo "$signal_dump" | sed -n 's/.*mSignalStrength=\([^ ]*\).*/\1/p' | head -n 1)
if [[ -z "$signal_strength" ]]; then
  signal_strength="Unavailable"
fi

cat <<OUTPUT
Installation complete.
Duration: ${install_duration}s
IMEI: ${imei}
Battery level: ${battery_level:-Unavailable}
Battery status: ${battery_status:-Unavailable}
Signal strength: ${signal_strength}
OUTPUT
