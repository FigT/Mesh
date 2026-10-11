plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "mesh"

sequenceOf("common", "bukkit", "bukkit-example", "velocity", "sponge").forEach { projectName ->
    include("mesh-$projectName")
    project(":mesh-$projectName").projectDir = file(projectName)
}