# css4j - agent module

This provides user agent-related functionality to CSS4J. Licence is BSD 3-clause, but includes a file with another licence
(see `LICENSES.txt`).

## Build from source
To build css4j-agent from the code that is currently at the Git repository, you need a current JDK (the build is tested with
version 16). You can run a variety of Gradle tasks with the Gradle wrapper (on Unix-like systems you may need to type `./gradlew`):

- `gradlew build` (normal build)
- `gradlew build publishToMavenLocal` (to install in local Maven repository)
- `gradlew copyJars` (to copy jar files into a top-level _jar_ directory)
- `gradlew lineEndingConversion` (to convert line endings of top-level text files to CRLF)
- `gradlew publish` (to deploy to a Maven repository, as described in the `publishing.repositories.maven` block of
[build.gradle](https://github.com/css4j/css4j/blob/master/build.gradle))

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

## Website
For more information please visit https://css4j.github.io/
