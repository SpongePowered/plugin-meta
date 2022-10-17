module org.spongepowered.plugin.metadata {
    exports org.spongepowered.plugin.metadata;
    exports org.spongepowered.plugin.metadata.builtin;
    exports org.spongepowered.plugin.metadata.builtin.model;
    exports org.spongepowered.plugin.metadata.model;
    exports org.spongepowered.plugin.metadata.util;

    requires transitive com.google.gson;
    requires static transitive org.checkerframework.checker.qual;
    requires static transitive maven.artifact; // Generated module name, may change?
}