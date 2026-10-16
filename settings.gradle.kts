import java.net.URI

include(":features:plantCare:data")


include(":features:plantCare:di")


include(":features:plantCare:device")


include(":features:plantCare:ui")


include(":features:plantCare:api")



include(":features:common:application")


include(":features:plantCare:application")


include(":features:plantCare:domain")



include(":features:plantCare:featureTest")


include(":features:home:ui")


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://jitpack.io")
        }
    }
}

rootProject.name = "PlantPulse"
include(":app")
include(":features:userGarden:featureTest")
include(":features:userGarden:domain")
include(":features:userGarden:application")
include(":features:userGarden:data")
include(":features:userGarden:ui")
include(":features:userGarden:di")
include(":features:common:ui")
