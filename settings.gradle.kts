pluginManagement {
    repositories {
        mavenLocal()
        maven ("https://maven.aliyun.com/repository/central")
        maven ("https://maven.aliyun.com/repository/public")
        maven ("https://maven.aliyun.com/repository/google")
        maven ("https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
        gradlePluginPortal()
        maven ("https://jitpack.io")
        maven ("https://oss.sonatype.org/content/repositories/snapshots/")

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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven ("https://maven.aliyun.com/repository/central")
        maven ("https://maven.aliyun.com/repository/public")
        maven ("https://maven.aliyun.com/repository/google")
        maven ("https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
        gradlePluginPortal()
        maven ("https://jitpack.io")
        maven ("https://oss.sonatype.org/content/repositories/snapshots/")

        google()
        mavenCentral()
    }
}

rootProject.name = "WebSocketDemo"

include(":app")
include(":wsgo-lib")
include(":wsgo-okwebsocket")
include(":wsgo-jwebsocket")
 