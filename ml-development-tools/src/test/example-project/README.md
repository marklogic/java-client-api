This is a simple project for testing out the ml-development-tools Gradle plugin.

To try this out, first run `./gradlew publishToMavenLocal` from the root directory of your clone of this
repository. Then update the `build.gradle` file in this directory to reference the version number of the 
plugin you published. You can then try each of the tasks in the `build.gradle` file to verify that Data Services
Java classes are generated correctly.
