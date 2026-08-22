# Fix Build, Update .gitignore, and Setup Spotless

This plan addresses build errors in `app/build.gradle.kts`, improves the `.gitignore` file, and ensures Spotless is correctly configured for code formatting.

## User Review Required

> [!IMPORTANT]
> The `compileSdk` and `targetSdk` will be downgraded from 37 to 35, as 37 is not a standard stable SDK version at this time.
> Java versions will be unified to JDK 17 for both Kotlin and Java compilation.

## Proposed Changes

### Build Configuration

#### [MODIFY] [root build.gradle.kts](file:///C:/Users/a345l/AndroidStudioProjects/polanty/build.gradle.kts)
- Remove invalid/shadowed import.
- Ensure Spotless is correctly applied to subprojects.

#### [MODIFY] [app build.gradle.kts](file:///C:/Users/a345l/AndroidStudioProjects/polanty/app/build.gradle.kts)
- Fix `isCoreLibraryDesugaringEnabled`.
- Fix `useJUnitPlatform()` configuration.
- Set `compileSdk` and `targetSdk` to 35.
- Unify Java/JVM target to 17.
- Remove duplicate dependency declarations.

### Project Structure

#### [MODIFY] [.gitignore](file:///C:/Users/a345l/AndroidStudioProjects/polanty/.gitignore)
- Add comprehensive Android, Gradle, and IDE ignore patterns.

### Formatting

#### [MODIFY] [libs.versions.toml](file:///C:/Users/a345l/AndroidStudioProjects/polanty/gradle/libs.versions.toml)
- Ensure all plugin versions are correctly defined. (Existing looks mostly fine, but will double-check).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the build fixes.
- Run `./gradlew spotlessApply` to verify formatting.
- Run `gradle_sync` to ensure IDE compatibility.

### Manual Verification
- Check that `.gitignore` correctly filters out build artifacts and IDE-specific files.
