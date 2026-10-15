pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GifSearch"

include(":app")
include(":core:ui")
include(":core:network")
include(":domain")
include(":data")
include(":feature:search")
include(":feature:details")
