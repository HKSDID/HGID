# Game Helio G100 — Fixed Android Release
Android 16 / API 36. The workflow builds, signs, verifies, hashes, uploads, and publishes the APK on `v*` tags. Stable production signing uses GitHub secrets `RELEASE_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Without them, a CI-only key is generated; it is signed but is not a Google Play production identity.
