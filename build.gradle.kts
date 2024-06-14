plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"
val mysqlVersion = "8.0.33"
val sqliteVersion = "3.46.0.0"
val exposedVersion = "0.51.1"
val hikariVersion = "5.1.0"
val dotenvVersion = "6.4.1"

val databaseDependencies = listOf(
    "mysql:mysql-connector-java:$mysqlVersion",
    "org.xerial:sqlite-jdbc:$sqliteVersion",
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "com.zaxxer:HikariCP:5.0.1"
)

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    databaseDependencies.forEach(this::implementation)
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenvVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}