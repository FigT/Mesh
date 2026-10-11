plugins {
    id("com.gradleup.nmcp.aggregation")
}

group = "us.figt"
version = properties["meshVersion"] as String
description = "A library that allows you to 'mesh' together a series of tasks, whilst switching thread contexts."

val publishProjects = sequenceOf("common", "bukkit", "velocity", "sponge")

nmcpAggregation {
    centralPortal {
        val nmcpUsername = properties["nmcpUsername"]
        val nmcpPassword = properties["nmcpPassword"]

        if (nmcpUsername != null && nmcpPassword != null) {
            username = nmcpUsername as String
            password = nmcpPassword as String
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    publishProjects.forEach {
        nmcpAggregation(project(":mesh-$it"))
    }
}