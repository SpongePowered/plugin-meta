/*
 * This file is part of plugin-meta, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.plugin.metadata.model;

import com.google.gson.TypeAdapter;

public final class Adapters {

    public static final class Deserializers {

        public static final TypeAdapter<PluginBranding.Builder> PLUGIN_BRANDING = new PluginBranding.Deserializer();

        public static final TypeAdapter<PluginContributor.Builder> PLUGIN_CONTRIBUTOR = new PluginContributor.Deserializer();

        public static final TypeAdapter<PluginDependency.Builder> PLUGIN_DEPENDENCY = new PluginDependency.Deserializer();

        public static final TypeAdapter<PluginLinks.Builder> PLUGIN_LINKS = new PluginLinks.Deserializer();

        public static final TypeAdapter<PluginLoader.Builder> PLUGIN_LOADER = new PluginLoader.Deserializer();

        private Deserializers() {
        }
    }

    public static final class Serializers {
        public static final TypeAdapter<PluginBranding> PLUGIN_BRANDING = new PluginBranding.Serializer();

        public static final TypeAdapter<PluginContributor> PLUGIN_CONTRIBUTOR = new PluginContributor.Serializer();

        public static final TypeAdapter<PluginDependency> PLUGIN_DEPENDENCY = new PluginDependency.Serializer();

        public static final TypeAdapter<PluginLinks> PLUGIN_LINKS = new PluginLinks.Serializer();

        public static final TypeAdapter<PluginLoader> PLUGIN_LOADER = new PluginLoader.Serializer();

        private Serializers() {
        }
    }

    private Adapters() {
    }
}
