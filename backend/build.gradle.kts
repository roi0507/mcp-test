import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.4.4"

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-web-client:4.4.4")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-config")
    implementation("io.vertx:vertx-jdbc-client:$vertxVersion")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.flywaydb:flyway-core:9.21.1")
    implementation("org.flywaydb:flyway-mysql:9.21.1")
    testImplementation(kotlin("test"))
    testImplementation("io.vertx:vertx-junit5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("vertx-api-${version}-fat.jar")
    manifest {
        attributes["Main-Class"] = "com.example.api.MainKt"
    }
    mergeServiceFiles()
}

application {
    mainClass.set("com.example.api.MainKt")
} 