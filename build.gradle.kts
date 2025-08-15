buildscript {
    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
    }
}

repositories {
    maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
        credentials {
            username = extra.properties["artifactory_hosting_username"] as String
            password = extra.properties["artifactory_hosting_password"] as String
        }
    }
}

plugins {
    id ("java-library")
}

subprojects {
    val fsRuntimeVersion: String by project

    apply(plugin = "java-library")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = extra.properties["artifactory_hosting_username"] as String
                password = extra.properties["artifactory_hosting_password"] as String
            }
        }
    }

    dependencies {
        compileOnly (group = "de.espirit.firstspirit", name = "fs-isolated-runtime", version = fsRuntimeVersion)

        implementation(group = "com.google.code.gson", name = "gson", version = "2.12.0")

        testImplementation(group = "de.espirit.firstspirit", name = "fs-isolated-runtime", version = fsRuntimeVersion)
        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.2")
        testImplementation(group = "org.mockito", name = "mockito-core", version = "5.11.0")
        testImplementation(group = "org.mockito", name = "mockito-junit-jupiter", version = "5.11.0")
        testImplementation(group = "org.mockito", name = "mockito-inline", version = "5.2.0")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}
