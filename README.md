# PulseCast – React Native bulk SMS broadcaster

PulseCast is a production-ready React Native (Expo) starter that allows Android devices to send personalized SMS broadcasts to multiple recipients at once. It focuses on a polished operator experience featuring rich gradients, actionable analytics, reusable templates, and optional scheduling reminders.

## Highlights

- 📇 **Powerful recipient management** – Add contacts individually or paste lists for high-volume imports with automatic validation and duplicate detection.
- 🧠 **Message intelligence** – Preview SMS segments, keep character counts in check, and reuse pre-loaded templates for consistent campaigns.
- 🌓 **Elegant UI/UX** – Gradient surfaces, elevated cards, and responsive layout create an immersive workspace ready for stakeholders.
- 📅 **Scheduling support** – Set future delivery reminders directly in the app (hand-offs happen when the user returns at the scheduled time).
- 📱 **Android-first SMS flow** – Uses `expo-sms` to hand delivery off to the native Android composer, ensuring compliance with platform policies.

## Getting started

1. **Install dependencies**

   ```bash
   npm install
   # or
   yarn install
   ```

2. **Verify the Expo environment**

   ```bash
   npx expo doctor
   ```

   Resolve any warnings about missing Android SDKs, Expo CLI login, or native module compatibility before proceeding.

3. **Run the development server**

   ```bash
   npx expo start
   ```

   - Press `a` to launch the Expo app inside an Android emulator, or scan the QR code with Expo Go on a physical Android handset connected to the same network.
   - Grant SMS permissions when prompted. iOS devices can render the UI for design review, but the `expo-sms` bridge is limited to the native composer and will not broadcast to multiple recipients.

4. **Smoke-test the SMS experience**

   - From the Compose screen, add recipients manually or paste them into the **Bulk Import** box. The UI flags invalid numbers in real time.
   - Choose a template or write a custom message, confirm the segment counter stays within your target limit, and tap **Send SMS**.
   - The native Android SMS composer opens with the recipients and body prefilled; send a message to verify delivery from the device.
   - Use the in-app analytics preview to validate that schedule selections, personalization fields, and character counts reflect your expectations.

## Environment variables

No runtime environment variables are required. Expo handles permissions for SMS and contacts through `app.json`.

## Testing the SMS flow

1. Add a few recipients manually or paste a comma/newline/tab separated list in the **Bulk Import** box.
2. Select a message template or compose a custom body.
3. Tap **Send SMS**. Expo will open the native Android SMS composer with your recipients and message populated.
4. Review, customize further if required, and press send in the native UI.

> ℹ️ For programmatic, background delivery you should integrate an SMS gateway (e.g., Twilio) via server-side APIs. The present MVP complies with Google Play policies by always surfacing the system composer before dispatch.

## Building installable Android packages

Expo's managed workflow uses [EAS Build](https://docs.expo.dev/build/introduction/) to produce signed artifacts. Two common profiles cover internal QA and Play Store submission:

1. **Configure credentials**

   ```bash
   npx expo login          # if you are not already authenticated
   npx eas login           # optional, but keeps the EAS CLI in sync
   npx eas build:configure # generates eas.json with default profiles
   ```

   - Follow the prompts to let Expo manage Android keystores or provide your own.
   - Update `app.json` (package name, icons, version codes) before triggering a build.

2. **Generate an installable APK for QA**

   ```bash
   npx eas build --platform android --profile preview --local  # produces a debug-friendly .apk
   ```

   - Remove `--local` to offload the build to Expo's cloud workers if you prefer not to install Android build tooling locally.
   - Once complete, download the `.apk` artifact from the CLI output or Expo dashboard and install it on test devices.

3. **Create a Play Store-ready bundle**

   ```bash
   npx eas build --platform android --profile production
   ```

   - The `production` profile yields an `.aab` bundle signed with the keystore from step 1.
   - Use `npx eas submit --platform android` to upload directly to Google Play, or retrieve the bundle from the Expo dashboard for manual submission.

## Project structure

```
App.tsx                # Navigation bootstrap
src/
  components/          # Reusable UI blocks (recipient chips, preview, modals)
  hooks/               # Stateful logic (recipients, templates)
  screens/             # Screen containers (Compose)
  theme/               # Palette and typography tokens
  utils/               # Helpers for validation and SMS segment estimates
```

## Roadmap ideas

- Integrate device contacts permission and contact picker for quicker recipient selection.
- Persist recipient groups and message templates via secure storage.
- Allow exporting campaign analytics and delivery status once integrated with an SMS provider.

## License

This project is provided as-is for MVP acceleration. Customize and extend to match your production deployment requirements.
