plugins {
    id("com.gradleup.nmcp.aggregation")
}

group = "us.figt"
version = properties["meshVersion"] as String

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
    sequenceOf("common", "bukkit", "velocity").forEach {
        nmcpAggregation(project(":mesh-$it"))
    }
}