val kotlinVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val mysqlVersion: String by project
val sqliteVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val dotenvVersion: String by project
val ktorVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.11"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


val databaseDependencies = listOf(
    "mysql:mysql-connector-java:$mysqlVersion",
    "org.xerial:sqlite-jdbc:$sqliteVersion",
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "com.zaxxer:HikariCP:$hikariVersion"
)
val ktorDependencies = listOf(
    "io.ktor:ktor-server-core-jvm",
    "io.ktor:ktor-server-netty-jvm",
    "io.ktor:ktor-server-config-yaml"
)

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenvVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    databaseDependencies.forEach(this::implementation)
    ktorDependencies.forEach(this::implementation)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}