description = "Sherlock Distributed Lock common api packages"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>
    api("org.slf4j:slf4j-api:${versions["slf4j"]}")
    api("org.jetbrains:annotations:${versions["jetbrainsAnnotations"]}")
    implementation(project(":common"))
}
