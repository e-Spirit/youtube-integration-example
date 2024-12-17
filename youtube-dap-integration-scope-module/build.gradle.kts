val fcafVersion: String by project

plugins {
    id("de.espirit.firstspirit-module-annotations") version "6.2.0"
}

dependencies {
    implementation(project(":youtube-dap-integration-scope-server"))
    implementation(group = "com.espirit.moddev.fcaf", name = "fcaf-module-scope", version = fcafVersion)
}
