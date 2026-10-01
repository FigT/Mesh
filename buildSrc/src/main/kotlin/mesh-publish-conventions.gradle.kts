import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
}

extra["isReleaseVersion"] = !project.version.toString().endsWith("SNAPSHOT")

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    sequenceOf("sourcesJar", "javadocJar").forEach {
        named<Jar>(it) {
            destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["meshVersion"] as String

            if (project.pluginManager.hasPlugin("com.gradleup.shadow")) {
                artifact(project.tasks.withType<ShadowJar>().getByName("shadowJar").archiveFile)

                artifact(tasks.named("sourcesJar"))
                artifact(tasks.named("javadocJar"))
            } else {
                from(components["java"])
            }


            pom {
                name.set(project.name)
                description.set(rootProject.description)
                url.set("https://github.com/FigT/Mesh")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("figt")
                        name.set("FigT")
                        email.set("figt@figt.us")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/FigT/Mesh.git")
                    developerConnection.set("scm:git:ssh://github.com/FigT/Mesh.git")
                    url.set("https://github.com/FigT/Mesh")
                }
            }
        }
    }
}

signing {
    setRequired( {
        (project.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish")
    })

    val signingKeyId = project.findProperty("signingKeyId") as String?
    val signingKey = project.findProperty("signingKey") as String?
    val signingPassword = project.findProperty("signingPassword") as String?

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)

    sign(publishing.publications["mavenJava"])
}