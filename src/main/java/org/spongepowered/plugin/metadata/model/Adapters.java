package org.spongepowered.plugin.metadata.model;

import com.google.gson.TypeAdapter;

public final class Adapters {

    public static final TypeAdapter<PluginBranding> PLUGIN_BRANDING = new PluginBranding.Adapter();

    public static final TypeAdapter<PluginContributor> PLUGIN_CONTRIBUTOR = new PluginContributor.Adapter();

    public static final TypeAdapter<PluginDependency> PLUGIN_DEPENDENCY = new PluginDependency.Adapter();

    public static final TypeAdapter<PluginLinks> PLUGIN_LINKS = new PluginLinks.Adapter();

    private Adapters() {
    }
}
