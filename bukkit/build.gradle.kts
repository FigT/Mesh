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
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}