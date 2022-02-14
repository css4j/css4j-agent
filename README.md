# css4j - agent module

This provides user agent-related functionality to CSS4J.

[License](LICENSE.txt) is BSD 3-clause, but includes a file with another license
(see [`LICENSES.txt`](LICENSES.txt)).

<br/>

## Java™ Runtime Environment requirements
All the classes in the binary package have been compiled with a [Java compiler](https://adoptium.net/)
set to 1.7 compiler compliance level, except the `module-info.java` file.

Building this module requires JDK 11 or higher.

<br/>

## Build from source
To build css4j-agent from the code that is currently at the Git repository, Java 11 or later is needed.
You can run a variety of Gradle tasks with the Gradle wrapper (on Windows shells you can omit the `./`):

- `./gradlew build` (normal build)
- `./gradlew build publishToMavenLocal` (to install in local Maven repository)
- `./gradlew lineEndingConversion` (to convert line endings of top-level text files to CRLF)
- `./gradlew publish` (to deploy to a Maven repository, as described in the `publishing.repositories.maven` block of
[build.gradle](https://github.com/css4j/css4j-agent/blob/1-stable/build.gradle))

<br/>

## Usage from a Gradle project
If your Gradle project depends on css4j-agent, you can use this project's own Maven repository in a `repositories` section of
your build file:
```groovy
repositories {
    maven {
        url "https://css4j.github.io/maven/"
        mavenContent {
            releasesOnly()
        }
        content {
            includeGroup 'io.sf.carte'
            includeGroup 'io.sf.jclf'
        }
    }
}
```
please use this repository **only** for the artifact groups listed in the `includeGroup` statements.

Then, in your `build.gradle` file:
```groovy
dependencies {
    api "io.sf.carte:css4j-agent:${css4jAgentVersion}"
}
```
where `css4jAgentVersion` would be defined in a `gradle.properties` file.

<br/>

## Software dependencies

In case that you do not use a Gradle or Maven build (which would manage the
dependencies according to the relevant `.module` or `.pom` files), the required
and optional library packages are the following:

### Compile-time dependencies

- The [css4j](https://github.com/css4j/css4j/releases) library (and its transitive
  dependencies); version 1.3.1 or higher (but below 2.x) is recommended.

- The [validator.nu html5 parser](https://about.validator.nu/htmlparser/).

- [SLF4J](http://www.slf4j.org/), which is a logging package.

### Test dependencies

- A recent version of [JUnit 4](https://junit.org/junit4/).

- The [validator.nu html5 parser](https://about.validator.nu/htmlparser/).

<br/>

## Website
For more information please visit https://css4j.github.io/
