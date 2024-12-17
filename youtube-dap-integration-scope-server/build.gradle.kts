val fcafVersion: String by project

plugins {
    id("de.espirit.firstspirit-module-annotations") version "6.2.0"
}

dependencies {
    api(group = "com.espirit.moddev.fcaf", name = "fcaf-server-scope", version = fcafVersion)
}
