plugins {
    // Standard Java support
    id("java")
    // The NEW, MODERN IntelliJ Platform Gradle Plugin
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "ar.lobo.tapestry"
version = "1.0.0-2025.1"

repositories {
    mavenCentral()
    // The new plugin has its own helper for repository configuration
    intellijPlatform {
        defaultRepositories()
    }
}

// All platform-related dependencies now go inside this block
dependencies {
    intellijPlatform {
        // Defines the target IDE to build against
        create("IU", "2025.1")

        // Defines dependencies on bundled IDE plugins
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.properties")
        bundledPlugin("com.intellij.javaee")
    }

    // Your plugin's external library dependencies remain here
    implementation("commons-chain:commons-chain:1.2")
    testImplementation("org.testng:testng:7.6.1")
    testImplementation("org.easymock:easymock:4.0.2")
    testImplementation("org.objenesis:objenesis:3.2")
    testImplementation("org.xmlunit:xmlunit-core:2.9.0")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
}

// The new, top-level block for all plugin configuration.
// This replaces the old 'intellij' block and the 'patchPluginXml' task.
intellijPlatform {
    pluginConfiguration {
        // Replaces the old 'patchPluginXml' properties
        ideaVersion {
            sinceBuild.set("251")
            untilBuild.set("251.*")
        }

        // Replaces the old pluginDescription property
        description.set("Tapestry framework support. Modernized build for IntelliJ 2025.1+.")

        // You can also set the plugin name and vendor here if needed
        // name = "Tapestry"
        // vendor {
        //     name.set("Your Name or Company")
        //     email.set("your.email@example.com")
        // }
    }
}

// This block tells Gradle where to find your source code, which doesn't change.
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

tasks {
    // Set the JVM compatibility versions for Java
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    // Still disable this task, as it's the correct workaround for older plugins
    named("buildSearchableOptions") {
        enabled = false
    }
}
