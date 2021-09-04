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
package org.spongepowered.plugin.metadata.builtin.model;

import com.google.gson.TypeAdapter;

public final class Adapters {

    private Adapters() {
    }

    public static final class Deserializers {

        public static final TypeAdapter<StandardContainerLoader.Builder> CONTAINER_LOADER = new StandardContainerLoader.Deserializer();

        public static final TypeAdapter<StandardPluginBranding.Builder> PLUGIN_BRANDING = new StandardPluginBranding.Deserializer();

        public static final TypeAdapter<StandardPluginContributor.Builder> PLUGIN_CONTRIBUTOR = new StandardPluginContributor.Deserializer();

        public static final TypeAdapter<StandardPluginDependency.Builder> PLUGIN_DEPENDENCY = new StandardPluginDependency.Deserializer();

        public static final TypeAdapter<StandardPluginLinks.Builder> PLUGIN_LINKS = new StandardPluginLinks.Deserializer();

        private Deserializers() {
        }
    }

    public static final class Serializers {

        public static final TypeAdapter<StandardContainerLoader> CONTAINER_LOADER = new StandardContainerLoader.Serializer();

        public static final TypeAdapter<StandardPluginBranding> PLUGIN_BRANDING = new StandardPluginBranding.Serializer();

        public static final TypeAdapter<StandardPluginContributor> PLUGIN_CONTRIBUTOR = new StandardPluginContributor.Serializer();

        public static final TypeAdapter<StandardPluginDependency> PLUGIN_DEPENDENCY = new StandardPluginDependency.Serializer();

        public static final TypeAdapter<StandardPluginLinks> PLUGIN_LINKS = new StandardPluginLinks.Serializer();

        private Serializers() {
        }
    }
}
