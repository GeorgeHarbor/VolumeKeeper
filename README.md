# Volume Keeper

A tiny Android TV / set-top-box app that pins the **media volume to maximum** and re-asserts it
whenever an app comes to the foreground.

## Why

Some set-top boxes (e.g. the ZTE MediaBox B866V2) zero the HDMI media-volume index every time an
app takes the foreground, so sound drops to silence on each app switch. CEC volume control is off,
so nothing holds at runtime — the firmware even rewrites the saved `volume_music_hdmi` setting back
to 0. Volume Keeper listens for those window transitions via an accessibility service and sets the
media stream back to max, with a couple of delayed retries to win the race after the transition.

## Install

1. Install the APK from the [latest release](../../releases/latest):
   ```sh
   adb -s <serial> install -r volumekeeper.apk
   ```
2. Enable the service: **Settings → Accessibility → Volume Keeper → On**, or over ADB:
   ```sh
   adb -s <serial> shell settings put secure enabled_accessibility_services \
     app.volumekeeper/app.volumekeeper.VolumeService
   adb -s <serial> shell settings put secure accessibility_enabled 1
   ```
   (Append to, don't overwrite, any services already listed — separate with `:`.)

## Notes

- Holds **STREAM_MUSIC** at maximum. Volume keys still work *within* an app; the service re-maxes
  on the next app change.
- No network, no ads, no analytics. Only `MODIFY_AUDIO_SETTINGS`.
- Requires Android 8.0 (API 26)+.

## Build & release

Mirrors the Metak setup: `./gradlew assembleDebug` builds locally (release falls back to the debug
key when `keystore.properties` is absent). CI builds a debug APK on push/PR; the release workflow
builds a signed APK on every push to `main` and publishes a versioned release plus a rolling
`latest`. It expects repo secrets `SIGNING_KEY` (base64 of the keystore), `KEY_STORE_PASSWORD`,
`ALIAS`, `KEY_PASSWORD`.
