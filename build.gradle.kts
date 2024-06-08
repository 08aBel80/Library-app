plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"
val sqliteVersion = "3.46.0.0"
val exposedVersion = "0.51.1"
val hikariVersion = "5.1.0"

val databaseDependencies = listOf(
    "org.xerial:sqlite-jdbc:$sqliteVersion",
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "com.zaxxer:HikariCP:5.0.1"
)

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    databaseDependencies.forEach(this::implementation)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}