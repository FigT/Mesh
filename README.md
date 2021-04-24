# Mesh


<img src="/.github/mesh-logo.svg" alt="logo" width="1024" height="256"/>


![Maven Central Version](https://img.shields.io/maven-central/v/us.figt/Mesh?style=for-the-badge)
![GitHub Code Size](https://img.shields.io/github/languages/code-size/FigT/Mesh?color=008b68&style=for-the-badge)
![GitHub License](https://img.shields.io/github/license/FigT/Mesh?style=for-the-badge)
Mesh is a library that allows you to 'mesh' together a series of tasks, whilst switching thread contexts.

Put more plainly, it's a Spigot-based abstraction of the CompletableFuture class.


## [Usage](https://github.com/FigT/Mesh/wiki/Usage)

#### Maven
Recommended to shade & relocate it to avoid conflicts with other plugins!


```xml
	<dependency>
	    <groupId>us.figt</groupId>
	    <artifactId>Mesh</artifactId>
	    <version>VERSION</version>
	</dependency>
```
<sub>Maven repository is on [Maven Central](https://repo1.maven.org/maven2/)</sub>

Browse through the code or see examples [here](https://github.com/FigT/Mesh/tree/master/src/main/java/us/figt/mesh/example), and on the wiki page [here](https://github.com/FigT/Mesh/wiki/Usage).

(More documentation and examples coming soon)

## Contributing

PRs are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](LICENSE)