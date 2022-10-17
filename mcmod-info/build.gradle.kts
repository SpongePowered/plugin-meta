description = "A mcmod.info implementation for the plugin metadata API"

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.26.0")
    api("com.google.code.gson:gson:2.8.9")
}

sourceSets.main {
    multirelease.moduleName("org.spongepowered.plugin.metadata.mcmodinfo")
}
