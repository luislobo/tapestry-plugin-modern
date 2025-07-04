plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "ar.lobo.tapestry"
version = "1.0.0-2025.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IU", "2025.1")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.properties")
        bundledPlugin("com.intellij.javaee")
    }

    implementation("commons-chain:commons-chain:1.2")
    testImplementation("org.testng:testng:7.6.1")
    testImplementation("org.easymock:easymock:4.0.2")
    testImplementation("org.objenesis:objenesis:3.2")
    testImplementation("org.xmlunit:xmlunit-core:2.9.0")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("251")
            // untilBuild is no longer needed for modern platform versions.
            // untilBuild.set("251.*") // <-- REMOVE THIS LINE
        }
        description.set("Tapestry framework support. Modernized build for IntelliJ 2025.1+.")
    }

    pluginVerification {
        ides {
            ide("IU", "2025.1")
        }
    }
}

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
    withType<JavaCompile> {
        // Update to Java 21 to match the target platform requirement.
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.encoding = "UTF-8"
    }

    named("buildSearchableOptions") {
        enabled = false
    }
}
