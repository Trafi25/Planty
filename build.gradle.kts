// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(
                mapOf(
                    "ktlint_code_style" to "android",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                )
            )
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            ktlint(libs.versions.ktlint.get())
        }
        format("misc") {
            target("**/*.gradle", "**/*.md", "**/.gitignore")
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}
