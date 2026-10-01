plugins {
    java
    com.gradleup.shadow
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
}