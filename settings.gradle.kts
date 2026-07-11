// Settings for FMD2 Mobile multi-module project
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

dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FMD2Mobile"

include(":app")
include(":core")
include(":database")
include(":downloader")
include(":parser")
include(":localsource")
include(":settings")
