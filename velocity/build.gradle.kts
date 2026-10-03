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
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":mesh-common"))
    compileOnly(libs.velocity.api)
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        links("https://jd.papermc.io/velocity/3.4.0")
    }
}