# PR: MT6993 / Mali-G1 Ultra: game-mode background cleanup, settings, and FPS overlay

This branch includes the following changes:

- Rename project root to include MT6993 (settings.gradle)
- Add DEVICE_SPEC.md for MT6993 / Mali-G1 Ultra MP12
- Add app-level background/foreground detection to GameApplication
- Implement GameModeController cleanup helpers and preference awareness
- Add SettingsActivity + layout to toggle "Allow background running", "Show FPS overlay", "Enable high refresh" and target FPS
- Add FpsOverlay (Choreographer-based) to display FPS while gameplay is active
- Update AndroidManifest to request WAKE_LOCK and declare SettingsActivity

Testing steps are included in the PR description and in the device spec file.
