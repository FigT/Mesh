# Mesh


<img src="/.github/mesh-logo.svg" alt="logo" width="1024" height="256"/>


![Maven Central Version](https://img.shields.io/maven-central/v/us.figt/mesh-common?style=for-the-badge)
![GitHub Code Size](https://img.shields.io/github/languages/code-size/FigT/Mesh?color=008b68&style=for-the-badge)
![GitHub License](https://img.shields.io/github/license/FigT/Mesh?style=for-the-badge)

Mesh is a library that allows you to 'mesh' together a series of tasks, whilst switching thread contexts.

Put more plainly, it's a Minecraft-based abstraction of the CompletableFuture class.


## [Usage](https://github.com/FigT/Mesh/wiki/Usage)

It's recommended to shade & relocate it to avoid conflicts with other plugins!

Browse through the code or see examples [here](https://github.com/FigT/Mesh/tree/master/src/main/java/us/figt/mesh/example), and on the wiki page [here](https://github.com/FigT/Mesh/wiki/Usage).

<sub>Maven repository is on [Maven Central](https://repo1.maven.org/maven2/)</sub>

(More documentation and examples coming soon)

## Platforms
### Bukkit
This implementation of Mesh uses the BukkitScheduler to execute tasks.

Requirements:
- Java 8+

Dependency:
```kotlin
    implementation("us.figt:mesh-bukkit:<version>")
```
<br>

### Sponge
This implementation of Mesh uses the Sponge API's Scheduler to execute tasks.

Requirements:
- Java 17+

Dependency:
```kotlin
    implementation("us.figt:mesh-sponge:<version>")
```
<br>

### Velocity
This implementation of Mesh uses Velocity's Scheduler to execute tasks, although as Velocity doesn't have a "main" thread, all tasks are executed "asynchronously" (that is, not on a main thread). This exists just to allow Mesh to be used in multiple scenarios.

Requirements:
- Java 17+

Dependency:
```kotlin
    implementation("us.figt:mesh-velocity:<version>")
```
<br>


## Building
### Requirements
- Java 17 (even though some implementations use Java 8, we use Java 17 for building with Gradle)

Use `gradlew build` to build all modules

## Contributing

PRs are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](LICENSE)
