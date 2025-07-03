plugins {
    // Provides the 'java' extension for compiling Java code.
    id("java")
    // The main plugin for building IntelliJ Platform plugins. Use a modern version.
    id("org.jetbrains.intellij") version "1.17.3"
}

// Define your plugin's group and version.
group = "com.yourname.tapestry"
version = "1.0.0-2025.1"

// The repository where plugin dependencies are located.
repositories {
    mavenCentral()
}

// This is the main configuration block for the IntelliJ Gradle Plugin.
intellij {
    // The version of the IntelliJ Platform to build against.
    version.set("2025.1")
    // The target IDE type. "IU" for IntelliJ IDEA Ultimate, "IC" for Community.
    type.set("IU")

    // Declare dependencies on plugins that are bundled with the IDE.
    // The plugin needs APIs from these modules to function.
    plugins.set(listOf(
        "com.intellij.java", // Note: "javaee" is now part of the "java" plugin
        "com.intellij.css",
        "com.intellij.properties",
        "com.intellij.javaee"
    ))
}

// Configure the Java compiler settings for the project.
tasks.withType<JavaCompile> {
    // Modern IntelliJ Platforms (2022.2+) require Java 17.
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

// This block tells Gradle where to find your source code, since it's in a subdirectory.
sourceSets {
    main {
        java.srcDirs("tapestry/src/main/java", "tapestry/src/main/gen")
        resources.srcDir("tapestry/src/main/resources")
    }
    test {
        java.srcDir("tapestry/src/test/java")
        resources.srcDir("tapestry/src/test/resources")
    }
}

// This task automatically patches your plugin.xml file with correct version info.
tasks.patchPluginXml {
    // The build number for 2025.1 starts with "251".
    sinceBuild.set("251")
    // Restricts compatibility to 2025.1.x versions. Use "252.*" for 2025.2, etc.
    untilBuild.set("251.*")

    // Optional: Update the description to note that this is a modernized version.
    pluginDescription.set("Tapestry framework support. Modernized build for IntelliJ 2025.1+.")
}

// Your plugin's external library dependencies.
dependencies {
    implementation("commons-chain:commons-chain:1.2")

    testImplementation("org.testng:testng:7.6.1")
    testImplementation("org.easymock:easymock:4.0.2")
    testImplementation("org.objenesis:objenesis:3.2")
    testImplementation("org.xmlunit:xmlunit-core:2.9.0")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
}
