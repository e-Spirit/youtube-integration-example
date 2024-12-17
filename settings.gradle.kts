pluginManagement {
    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "my-youtube-module"
include("youtube-dap-integration-module")
include("youtube-dap-integration-scope-module")
include("youtube-dap-integration-scope-server")
include("youtube-dap-integration-server-only")
