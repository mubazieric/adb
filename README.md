# SciCalc (Android)

A production-ready scientific calculator built with Jetpack Compose, MVVM, and a pure Kotlin calculation engine.

## Features
- Full scientific calculator engine (shunting-yard parser + RPN evaluator).
- History with persistence (DataStore JSON).
- Settings (angle mode, fullscreen, haptics, ads toggle, sound toggle, keep screen on).
- AdMob banner integration with safe fallback when not configured.
- Google Play Billing one-time donation placeholder.

## Project Structure
- `app/src/main/java/com/example/scicalc/engine`: pure Kotlin calculation engine.
- `app/src/main/java/com/example/scicalc/ui`: Compose UI + ViewModel.
- `app/src/main/java/com/example/scicalc/data`: DataStore repositories.
- `app/src/main/java/com/example/scicalc/ads`: AdMob banner.
- `app/src/main/java/com/example/scicalc/billing`: Billing integration.

## AdMob Configuration
- **App ID**: `app/src/main/res/values/strings.xml` → `admob_app_id`
- **Banner Unit ID**: `app/src/main/res/values/strings.xml` → `admob_banner_id`
- Default values use Google test IDs, so the app runs without configuration.
- The banner auto-hides if the ID is blank or if "Show ads" is disabled in Settings.

## Billing Configuration
- Product ID placeholder: `donation_tier1` (see `SettingsScreen.kt`).
- Configure the product in Play Console and update the ID if needed.
- If billing isn't configured, the UI shows "Billing not configured" and no crashes occur.

## RUN STEPS
1. Open Android Studio.
2. Select **Open** and choose this project folder (`/workspace/adb`).
3. Let Gradle sync.
4. Choose a device/emulator and press **Run**.

## Tests
- Unit tests for the calculation engine are in `app/src/test/java/com/example/scicalc/engine/CalculatorEngineTest.kt`.
- Run via: `./gradlew test`.
