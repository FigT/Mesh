import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("mesh-common-conventions")
    id("mesh-shadow-conventions")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    implementation(project(":mesh-common"))
    implementation(project(":mesh-bukkit"))
}

tasks {
    shadowJar {
        // this is dumb, but it works, mesh-bukkit needs to be built first, so that the example can use it
        mustRunAfter(project(":mesh-bukkit").tasks.withType<ShadowJar>())

        // don't include the example in the root libs dir, it should be in the example's build/libs dir
        destinationDirectory.set(project.layout.buildDirectory.dir("libs"))
    }

    jar {
        // don't include the example in the root libs dir, it should be in the example's build/libs dir
        destinationDirectory.set(project.layout.buildDirectory.dir("libs"))
    }

    processResources {
        charset("UTF-8")

        filesMatching("plugin.yml") {
            expand(
                "version" to project.version
            )
        }
    }


}