import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.incremental.deleteDirectoryContents
import java.io.FileInputStream
import java.nio.file.Paths
import java.nio.file.Path
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.10"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.9.10")
    }
}

class PathFileBuilder(first: Any) {
    private var component: String = first.toString()
    constructor() : this("")

    private fun join(path: Any) {
        val pathString = path.toString()
        if(pathString.isEmpty())
            return
        component += "${File.separator}$pathString"
    }

    fun add(path: Any, vararg paths: Any): PathFileBuilder {
        if(component.isEmpty())
            component = path.toString()
        else join(path)

        if(paths.isEmpty())
            return this

        add(paths.toList())

        return this
    }

    fun add(paths: List<Any>): PathFileBuilder = this.apply {
        join(paths.joinToString(separator = File.separator))
    }

    fun copy(): PathFileBuilder {
        return PathFileBuilder(this.component)
    }

    fun build(): Path {
        if(component.isEmpty())
            throw IllegalAccessException("The components path is empty")

        return Paths.get(component)
    }
}

class LibraryProperties {
    var compileSdk = 34
    var group = "com.yoimerdr.android"
    var name = "virtualjoystick"
    var version = "2.0.0"
    var copyright = "© 2024 Yoimer Davila"
    var minSdk = 21

    fun toPath(): Path {
        return PathFileBuilder()
            .add(group.split("."))
            .add(name.split("."))
            .add(version)
            .build()
    }
}

val libraryProperties = LibraryProperties()

val localRepository = PathFileBuilder(project.buildDir)
    .add("localRepository")
    .build()

val githubProperties = Properties()

try {
    githubProperties.load(FileInputStream(rootProject.file("github.properties")))
} catch (e: Exception) {
    println(e)
}

val githubPckTarget = "https://maven.pkg.github.com/yoimerdr/AndroidVirtualJoystick"

val dokkaIncludes = PathFileBuilder(project.rootDir)
    .add("dokka")
    .add("includes")

val dokkaStyles = dokkaIncludes.copy()
    .add("styles")

val libraryDocs: File = PathFileBuilder(project.rootDir)
    .add("docs")
    .add(libraryProperties.version)
    .build()
    .toFile()

android {
    namespace = "${libraryProperties.group}.${libraryProperties.name}"
    compileSdk = libraryProperties.compileSdk

    defaultConfig {
        minSdk = libraryProperties.minSdk
        consumerProguardFiles("consumer-rules.pro")
        aarMetadata {
            minCompileSdk = libraryProperties.minSdk
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

// Dokka docs configuration
tasks.withType<DokkaTask>().configureEach {
    doFirst {
        if(libraryDocs.exists())
            libraryDocs.deleteDirectoryContents()
    }

    moduleName.set(libraryProperties.name)
    moduleVersion.set(libraryProperties.version)
    outputDirectory.set(libraryDocs)

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customStyleSheets = listOf(
            dokkaStyles
                .add("vs-dark.css")
                .build()
                .toFile()
        )

        footerMessage = libraryProperties.copyright
    }
    dokkaSourceSets {
        configureEach {
            perPackageOption {
                documentedVisibilities.set(setOf(DokkaConfiguration.Visibility.PUBLIC, DokkaConfiguration.Visibility.PROTECTED))
            }
        }
    }
}

// publish packages configuration
publishing {
    publications {
        afterEvaluate {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = libraryProperties.group
                artifactId = libraryProperties.name
                version = libraryProperties.version
            }
        }
    }

    repositories {
        maven {
            name = "local"
            url = uri(localRepository)
        }

        maven {
            name = "GithubPackages"
            url = uri(githubPckTarget)
            credentials {
                username = githubProperties.getProperty("gpr.usr") ?: System.getenv("GPR_USER")
                password = githubProperties.getProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
            }
        }
    }
}

tasks.register<Zip>("distLocalLibrary") {
    description = "Copy the generated files in the local repo to distributions folder on root."
    dependsOn("publishReleasePublicationToLocalRepository")

    val sourceDir = PathFileBuilder(localRepository)
        .add(libraryProperties.toPath())
        .build()

    from(fileTree(mapOf("dir" to sourceDir.toString(), "include" to listOf("**/*.aar", "**/*.jar"))))
    into("${libraryProperties.name}/${libraryProperties.version}")

    val outFolder = PathFileBuilder(project.rootDir)
        .add("distributions")
        .add(libraryProperties.version)
        .build()
        .toFile()

    destinationDirectory.set(outFolder)
    archiveBaseName.set("${libraryProperties.name}-${libraryProperties.version}")
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.10")
}