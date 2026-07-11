# Release Compilation Instructions: FMD2 Mobile

This document details how to compile, sign, and build the release-ready APK package for FMD2 Mobile.

## Prerequisites

1. **JDK 17** or higher installed.
2. **Android SDK** configured on system.
3. Access to `gradlew` or an Android Studio environment.

---

## Build Steps

### 1. Generate Signed Keystore
If you do not have a release keystore, generate one using Java's `keytool`:

```bash
keytool -genkey -v -keystore fmd2-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias fmd2-alias
```

### 2. Configure gradle.properties
Create or edit `gradle.properties` (or setup environment variables) to point gradle to the generated signing details:

```properties
RELEASE_STORE_FILE=fmd2-release-key.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=fmd2-alias
RELEASE_KEY_PASSWORD=your_key_password
```

### 3. Build Release Bundle / APK
Run the Gradle task using the terminal wrapper to compile the application and bundle all submodules:

```bash
# Clean project and compile release APK
./gradlew clean assembleRelease
```

Once the compilation completes successfully, the signed production APK can be retrieved from:
`app/build/outputs/apk/release/app-release.apk`

---

## Pro-Tips for Optimizations
- Enable Proguard/R8 shrinking by updating `isMinifyEnabled = true` inside `app/build.gradle.kts` for release builds.
- Run lint inspections prior to release compiles using `./gradlew lint` to check for security vulnerabilities.
