plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.6.1")
    implementation("com.gradleup.nmcp:com.gradleup.nmcp.gradle.plugin:1.6.2")
}