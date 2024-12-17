val guavaVersion: String by project
val ytApiVersion: String by project
val fcafVersion: String by project
val googleJackson2Version: String by project

plugins {
    id("de.espirit.firstspirit-module-annotations") version "6.2.0"
}

dependencies {
    implementation(project(":youtube-dap-integration-scope-module"))
    implementation(project(":youtube-dap-integration-scope-server"))

    implementation(group = "com.espirit.moddev.fcaf", name = "fcaf-module-scope", version = fcafVersion)
    implementation(group = "info.clearthought", name = "table-layout", version = "4.3.0")
    implementation(group = "com.google.apis", name = "google-api-services-youtube", version = ytApiVersion)
    implementation(group = "com.google.http-client", name = "google-http-client-jackson2", version = googleJackson2Version)
    implementation(group = "com.google.guava", name = "guava", version = guavaVersion)
}
