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
        // Pleos Connect SDK Repository
        maven { url = java.net.URI("https://maven.auto-hmg.io/artifactory/pleos-sdk") }
    }
}
rootProject.name = "SafeDrive"
include(":app")
