description = "A mods.toml implementation for the plugin metadata API"

dependencies {
    implementation("com.electronwill.night-config:toml:3.6.3")
    implementation("com.electronwill.night-config:core:3.6.3")
}

tasks.jar {
    manifest.attributes(
            "Automatic-Module-Name" to "org.spongepowered.plugin.metadata.modstoml"
    )
}
