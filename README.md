# adb

ADB script to install an app on a mobile phone, then display phone IMEI number,
battery status, signal strength, and installation duration.

## Usage

```sh
./adb_install_info.sh path/to/app.apk [device_serial]
```

You can also set `ADB_SERIAL` to target a device without passing the serial each
run.

```sh
export ADB_SERIAL=emulator-5554
./adb_install_info.sh path/to/app.apk
```

## Production-ready install tips

* Ensure USB debugging is enabled and the device is authorized for `adb`.
* Use a release-signed APK when testing production builds.
* The script uses `adb install -r -g` to reinstall and grant runtime permissions
  for immediate testing on-device.
