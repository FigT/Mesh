plugins {
    id("mesh-common-conventions")
    id("mesh-shadow-conventions")
    id("mesh-publish-conventions")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    implementation(project(":mesh-common"))
    compileOnly(libs.spigot.api)
}