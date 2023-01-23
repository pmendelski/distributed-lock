description = "Sherlock Distributed Lock implementation for coroutine using SQL asynchronous connector R2DBC"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-coroutine"))
    api("io.r2dbc:r2dbc-spi:${versions["r2dbc"]}")

    // implementation
    implementation(project(":api:api-coroutine-connector"))
    implementation(project(":common"))
    implementation(project(":sql:sql-common"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${versions["coroutines"]}"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("io.r2dbc:r2dbc-pool:${versions["r2dbc"]}")
    // integration: postgres
    integrationImplementation("org.postgresql:postgresql:${versions["postgresql"]}")
    integrationImplementation("org.postgresql:r2dbc-postgresql:${versions["r2dbc"]}")
    integrationImplementation("org.testcontainers:postgresql:${versions["testContainers"]}")
    // integration: mysql
    integrationImplementation("mysql:mysql-connector-java:${versions["mysql"]}")
    // integrationImplementation("dev.miku:r2dbc-mysql:${versions["r2dbc"]}") // waiting fo release
    integrationImplementation("org.testcontainers:mysql:${versions["testContainers"]}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
}