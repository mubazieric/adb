# DropDroid (by Iwat Digital)

DropDroid is an Android 10+ (API 29) offline, secure file-sharing app inspired by AirDrop. It prioritizes Google Nearby Connections when available and falls back to Wi-Fi Direct/QR workflows, with encrypted transfers and a foreground service for reliability.

## Project layout

- `app`: Jetpack Compose UI, navigation, permissions, share-sheet entry, foreground service, and Hilt wiring.
- `core`: Models, crypto utilities (X25519 + AES-GCM), metadata builder, and protocol primitives.
- `data`: Room database for history and trusted peers, repositories, and DI.
- `transport-nearby`: Nearby Connections transport manager and handshake plumbing.
- `transport-wifidirect`: Stubbed Wi-Fi Direct/QR fallback surface for later milestones.

## Build requirements

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK platforms 29–34
- Google Play Services on target devices for Nearby Connections
- Gradle wrapper: if the wrapper JAR fails to download in restricted networks, regenerate via `gradle wrapper --gradle-version 8.7`

## Modules & Milestones

This repository is organized to satisfy the requested milestones:

1. **Milestone A** – App scaffold, Compose navigation (Home, History, Settings), theming, and state holders.
2. **Milestone B** – SAF multi-file picking hook, metadata builder, Room schema for transfer history and trusted peers, and repositories.
3. **Milestone C** – Nearby Connections transport skeleton with discovery/advertising, metadata exchange, and progress hooks; foreground service shell.
4. **Milestone D** – Crypto layer with X25519 ECDH, AES-GCM chunk helpers, deterministic auth code, and unit tests.
5. **Milestone E** – Wi-Fi Direct/QR fallback skeleton for non-GMS environments.
6. **Milestone F** – Permission UX, notifications, history polish, and diagnostics placeholders.

## Key features (MVP)

- Offline nearby discovery and advertising (Nearby Connections preferred)
- Multi-file selection via Storage Access Framework with metadata aggregation
- Receiver confirmation flow scaffolding and foreground service for background resilience
- Encrypted payload helpers (ECDH + AES-GCM) and integrity hashing
- Share sheet entry point to start DropDroid from other apps
- Room-backed history/trusted peers (UI wiring in progress)

## Permissions map

- Bluetooth scan/connect (Android 12+), legacy Bluetooth for <=30
- Nearby Wi-Fi devices + Wi-Fi state for Wi-Fi Direct
- Foreground service (data sync) + notifications
- SAF/MediaStore is used for storage access (no broad storage permissions)
- Location (coarse/fine) only when required by discovery stacks on Android 10/11

## How to run

1. Open the project in Android Studio.
2. If the Gradle wrapper jar is missing (common in offline CI), regenerate via:
   ```bash
   gradle wrapper --gradle-version 8.7
   ```
3. Sync Gradle, choose a device/emulator with Google Play Services (for Nearby), and run the `app` configuration.

## How to test on two phones

1. Install the debug build on both devices (Android 10+).
2. Ensure Bluetooth and Wi‑Fi are enabled. Grant Bluetooth/notification permissions on Android 12+.
3. Launch DropDroid. Verify both devices show “Receiving: On”.
4. On sender: tap “Pick files to send” and select images/docs. Nearby devices appear in the grid.
5. Tap a device to initiate the request. Receiver should see the incoming prompt; accept to start transfer.
6. Keep the app/background service alive; progress is shown via in-app status and foreground notification.
7. Retrieved files will be written to the chosen SAF/Downloads location (media-handling polish pending).

## Testing

Unit tests cover crypto round-trips, auth code derivation, SHA-256 hashing, and metadata aggregation:

- `./gradlew :core:test`

## Security notes

- Session keys are derived with X25519 + PBKDF2 salt, and AES-GCM is used per-chunk with monotonic nonces.
- A 6-digit authentication code is derived from the shared secret for out-of-band verification.
- APK sharing is off by default—surface enabling only in a trusted setting (future toggle).

## Monetization placeholder

Space is reserved for AdMob integration in the `app` module (to be added once ad unit IDs are available).

## Troubleshooting

- If Nearby discovery fails, toggle Bluetooth/Wi‑Fi and ensure Play Services is up to date.
- On non-GMS devices, use the fallback Wi‑Fi Direct/QR pairing flow (module scaffold in place).
- Battery optimizations may pause discovery; whitelist DropDroid during testing.
