# Android CI and APK downloads

The workflow in `workflows/android.yml` runs on every push, pull request, and manual run.
It uses Java 21 (matching the Gradle daemon toolchain) and Android SDK 37.

Each run checks Spotless formatting, runs JVM unit tests and Android lint, then builds a
debug APK. The APK is uploaded only if all checks pass. Test and lint reports are uploaded
even when a check fails, when those reports exist. Artifacts are retained for 14 days.

## Download an APK

1. Push the workflow and source changes to GitHub.
2. Open **Actions → Android CI and APK** and select a successful run.
3. Download `planty-debug-<run number>` from **Artifacts**, unzip it, and install the APK.

For a manual build, use **Run workflow** once this YAML exists on the default branch.
These are debug builds for development, delivered through Actions artifacts.

## Optional live-service configuration

No secrets are required for checks or compilation. By default, CI generates empty PlantNet
and Perenual keys and a placeholder Firebase configuration. Such APKs support local app
testing, but plant recognition and Firebase-backed AI require live configuration.

To enable live services for push and manual runs, add these repository secrets under
**Settings → Secrets and variables → Actions**:

| Secret | Value |
| --- | --- |
| `GOOGLE_SERVICES_JSON` | Entire contents of the Firebase Android configuration for `com.traffipart.polanty` |
| `PLANT_NET_API_KEY` | PlantNet API key |
| `PERENUAL_API_KEY` | Perenual API key |

All pull request builds use placeholders. Optional secrets are passed through environment
variables and written to ignored files; they are not printed. Configured API keys are
embedded in the resulting APK, as in local builds. Firebase App Check configuration is
still required for Gemini access on an installed debug build.

`scripts/prepare_android_ci.py` creates the configuration on the fresh runner. It refuses to
overwrite existing `local.properties` or `app/google-services.json` files. The Gradle wrapper
`gradlew` is tracked with executable mode for direct invocation on Ubuntu CI runners.

Instrumented tests requiring an emulator are not part of this workflow. The workflow has
read-only repository permissions and does not publish to Google Play or Firebase Distribution.
