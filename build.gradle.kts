import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    signing
    `maven-publish`

    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.9.0"
    id("kotlinx-atomicfu")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "dev.kord"
version = "0.5.5"

kotlin {
    explicitApi()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.2")
    implementation("org.jetbrains.kotlinx", "atomicfu", "0.17.3")
    implementation("io.github.microutils", "kotlin-logging", "2.1.23")

    val ktor = "2.0.2"
    api("io.ktor", "ktor-serialization-kotlinx-json", ktor)
    api("io.ktor", "ktor-client-content-negotiation", ktor)
    api("io.ktor", "ktor-client-cio", ktor)
    api("io.ktor", "ktor-client-websockets", ktor)
    api("io.ktor", "ktor-network", ktor)
}

// by convention, java classes (TweetNaclFast) should be in their own java source.
// however, this breaks atomicfu.
// to work around it, we just make the kotlin src directory also a java src directory.
// this can be removed when https://github.com/Kotlin/kotlinx.atomicfu/commit/fe0950adcf0da67cd074503c2a78467656c5aa0f is released.
sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

tasks {
    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlin.contracts.ExperimentalContracts",
            )
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        // We don't use java, but this prevents a Gradle warning,
        // telling you to target the same java version for java and kt
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        publications {
            create<MavenPublication>("Kord") {
                from(project.components["java"])

                artifactId = "kord-voice"
                groupId = project.group as String
                version = project.version as String

                artifact(sourcesJar.get())

                repositories {
                    maven {
                        url = uri("https://maven.dimensional.fun/releases")

                        credentials {
                            username = System.getenv("REPO_ALIAS")
                            password = System.getenv("REPO_TOKEN")
                        }
                    }
                }
            }
        }
    }

    wrapper {
        gradleVersion = "7.4.2"
        distributionType = ALL
    }
}

