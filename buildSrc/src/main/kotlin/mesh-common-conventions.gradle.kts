plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val isShadow = project.pluginManager.hasPlugin("com.gradleup.shadow")

repositories {
    mavenCentral()
}

dependencies {
    compileOnlyApi(libs.findLibrary("jetbrains-annotations").get())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(rootProject.ext["javaVersion"] as String))
    }
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    withType<ProcessResources> {
        filteringCharset = "UTF-8"
    }

    jar {
        if (isShadow) {
            archiveClassifier.set("unshaded")
        } else {
            destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
        }
    }
}


val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")