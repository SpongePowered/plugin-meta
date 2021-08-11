pluginManagement {
    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
}

val name: String by settings
rootProject.name = name

include("mcmod-info")