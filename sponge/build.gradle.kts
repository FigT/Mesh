plugins {
    id("mesh-common-conventions")
    id("mesh-shadow-conventions")
    id("mesh-publish-conventions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    implementation(project(":mesh-common"))
    compileOnly(libs.sponge.api)
}

tasks.withType<Javadoc>().configureEach {
    options.source("17")

    (options as StandardJavadocDocletOptions).apply {
        links("https://jd.spongepowered.org/spongeapi/9.0.0/")
    }
}