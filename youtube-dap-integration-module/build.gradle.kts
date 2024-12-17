val fsModuleName: String by project
val fsDisplayName: String by project
val fsDescription: String by project
val fsVendor: String by project

plugins {
    id("de.espirit.firstspirit-module") version "6.0.0"
    id("maven-publish")
}

dependencies {
    fsServerCompile(project(":youtube-dap-integration-scope-server"))

    fsModuleCompile(project(":youtube-dap-integration-scope-module"))
    fsModuleCompile(project(":youtube-dap-integration-server-only"))

    fsWebCompile(project(":youtube-dap-integration-scope-server"))
    fsWebCompile(project(":youtube-dap-integration-scope-module"))
}

firstSpiritModule {
    moduleName = fsModuleName
    displayName = fsDisplayName
    vendor = fsVendor
    description = fsDescription
}
