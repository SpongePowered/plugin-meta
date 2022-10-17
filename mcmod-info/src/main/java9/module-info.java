module org.spongepowered.plugin.metadata.mcmodinfo {
    exports org.spongepowered.plugin.meta;
    exports org.spongepowered.plugin.meta.gson;
    exports org.spongepowered.plugin.meta.version;

    requires transitive com.google.gson;
    requires static transitive org.checkerframework.checker.qual;
}