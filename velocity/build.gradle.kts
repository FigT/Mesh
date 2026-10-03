plugins {
    id("mesh-common-conventions")
    id("mesh-shadow-conventions")
    id("mesh-publish-conventions")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":mesh-common"))
    compileOnly(libs.velocity.api)
}