# TESTING.md

This document explains how to build, sign, verify, and scan release artifacts for the GameHelioG100 project so Play Protect/third-party scanners are less likely to flag your APK/AAB.

1) Create a release keystore (run locally)

  keytool -genkeypair -v \
    -keystore release-keystore.jks \
    -alias release \
    -keyalg RSA -keysize 2048 -validity 9125

  - Keep the generated `release-keystore.jks` private and do NOT check it in to the repository.

2) Create `signing.properties` from the template

  - Copy `signing.properties.template` → `signing.properties` in the project root and fill values:
    storeFile=release-keystore.jks
    storePassword=...
    keyAlias=release
    keyPassword=...

  - Ensure `signing.properties` is gitignored (it should be; verify `.gitignore`).

3) Build a signed release APK or AAB

  - From Android Studio: Build -> Generate Signed Bundle / APK and choose your `signing.properties` values.
  - From the command line (Gradle):
    ./gradlew :app:assembleRelease    # builds signed APK if signingConfig is wired
    ./gradlew :app:bundleRelease      # builds AAB

  Note: This project supports signing via environment variables or a local `signing.properties` file. Verify your `app/build.gradle` contains a `signingConfigs` block that reads your values.

4) Verify signature with apksigner (Android build-tools)

  - Sign (if you created an unsigned APK):
    $ANDROID_HOME/build-tools/<version>/apksigner sign --ks release-keystore.jks --ks-key-alias release app-release-unsigned.apk

  - Verify:
    $ANDROID_HOME/build-tools/<version>/apksigner verify --verbose app-release.apk

  - Example output should include certificate fingerprints and confirmation the signature scheme is v2/v3 (where supported).

5) Extract certificate fingerprint (optional, useful to prove you used the same key)

  jarsigner -verify -certs -verbose app-release.apk
  # or use keytool on the keystore
  keytool -list -v -keystore release-keystore.jks -alias release

  Copy the SHA-256 fingerprint and keep it as a record for future releases.

6) Scan before distribution

  - Upload the APK/AAB to VirusTotal (https://www.virustotal.com/) to see which scanners flag it and why.
  - If a vendor flags a false positive, follow their process to submit a false-positive report.

7) Publish with Google Play (recommended)

  - Upload an AAB to the Play Console and enroll in Play App Signing. Play-managed signing makes upgrades easier and can reduce Play Protect warnings for known/published apps.
  - Use internal testing or internal app sharing to distribute test builds to QA; Play will sign the final APKs it delivers.

8) Reduce heuristics that raise flags

  - Avoid unnecessary dynamic code loading, installing helper APKs, unpacking and executing DEX at runtime, or requesting broad privileges that are not required.
  - Keep native libs minimal and well documented; obfuscated native code sometimes increases false-positive risk.

9) Test upgrade flows for sideloading

  - If you distribute a test APK (sideload), and later publish an app with a different signing key or package name, Android will treat it as a different app. To avoid user confusion:
    - Use the same signing key for official releases and test builds where possible.
    - Alternatively use a different applicationId for debug/test builds (e.g., com.mediatek.game.debug) so testers install alongside release.

10) Helpful commands quick reference

  - Generate keystore: keytool -genkeypair -v -keystore release-keystore.jks -alias release -keyalg RSA -keysize 2048 -validity 9125
  - Build AAB: ./gradlew :app:bundleRelease
  - apksigner verify: $ANDROID_HOME/build-tools/<ver>/apksigner verify --verbose app-release.apk
  - Upload to VirusTotal: https://www.virustotal.com/

If you want, I can:
- Add a small README section linking to TESTING.md and signing.properties.template.
- Add a CI snippet (GitHub Actions) that uses an encrypted keystore in secrets to sign an AAB and run apksigner verify.

