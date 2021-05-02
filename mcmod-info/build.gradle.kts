description = "A mcmod.info implementation for the plugin metadata API"

dependencies {

}

tasks.jar {
    manifest.attributes(
            "Automatic-Module-Name" to "org.spongepowered.plugin.metadata.mcmodinfo"
    )
}
