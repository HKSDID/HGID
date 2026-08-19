# Game Helio G100 — Fixed Android Release
Android 16 / API 36. The workflow builds, signs, verifies, hashes, uploads, and publishes the APK on `v*` tags. Stable production signing uses GitHub secrets `RELEASE_KEYSTORE_BASE64`, `KEYSTORE_PA[...]`

## High refresh / Game mode
This release includes support for requesting the device's highest supported display refresh mode (for example, 120 Hz) while the game is in the foreground.

Key behavior:
- The app requests the highest refresh rate when the game Activity is resumed and clears the preference on pause to be a good system citizen.
- The implementation lives in `app/src/main/java/com/mediatek/game/HighRefreshHelper.java` and is invoked from `MainActivity`.
- The existing `Surface.setFrameRate(...)` call for API 33+ is preserved.
- Intended to be used for gameplay-only (high-FPS / 120 Hz) to improve smoothness. Avoid keeping the high-refresh mode enabled for menus or background states to save power and thermals.
- No Google Mobile Services (GMS) dependency is required for this feature. The helper requests the display mode using Android display APIs.

Compatibility notes:
- Some OEMs expose "Game Space" or vendor-specific game modes that provide additional anti-lag or performance optimizations; this code requests the best available display mode and should be compatible with those vendor features, but behavior may vary by device/OEM.
- On devices that do not support high refresh rates or where vendors limit mode switching, the system may ignore the request.

Testing:
1. Install the app from the `feat/high-refresh-dark-ui` branch build.
2. Launch the game and check logcat for `HighRefreshHelper` logs indicating the requested mode id and refresh rate.
3. Verify the UI is using a dark background with readable white text on the activity screen.
4. Confirm the device switches to a higher refresh rate (if supported) in Settings > Display or via a developer refresh-rate indicator.

Developer notes:
- Files changed in branch `feat/high-refresh-dark-ui`:
  - `app/src/main/java/com/mediatek/game/HighRefreshHelper.java` (new)
  - `app/src/main/java/com/mediatek/game/MainActivity.java` (modified)
- If you want an FPS counter or a Choreographer-based game-loop integration in this branch, I can add it in a follow-up commit.
