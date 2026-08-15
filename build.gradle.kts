// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "7.4.0.8496"
}

sonar {
    properties {
        property("sonar.projectKey", "ShravanSk123_android-repo-explorer")
        property("sonar.organization", "shravansk123")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}