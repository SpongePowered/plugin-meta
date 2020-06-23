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
package org.spongepowered.plugin.metadata.util;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.PluginMetadataContainer;
import org.spongepowered.plugin.metadata.parser.PluginMetadataAdapter;
import org.spongepowered.plugin.metadata.parser.PluginMetadataCollectionAdapter;
import org.spongepowered.plugin.metadata.parser.PluginMetadataContainerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * A utility class for operating against this library's standard metadata
 * specification, the plugins.json.
 *
 * @see <a href="https://github.com/SpongePowered/plugin-meta/wiki/Plugin-Metadata-Specification">Plugin Metadata Specification</a>
 */
public final class PluginMeta {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final PluginMetadataContainerAdapter adapter;

    private PluginMeta(final PluginMetadataContainerAdapter adapter) {
        this.adapter = adapter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<PluginMetadata> read(final InputStream in) throws IOException {
        final JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in)));
        reader.beginObject();
        try {
            while (reader.hasNext()) {
                if (reader.nextName().equals("plugins")) {
                    return Collections.unmodifiableCollection(this.adapter.getCollectionAdapter().read(reader));
                }
            }
            return Collections.emptyList();
        } finally {
            reader.endObject();
        }
    }

    public PluginMetadataContainer readAsContainer(final InputStream in) throws IOException {
        final Collection<PluginMetadata> pluginMetadata = this.read(in);
        return new PluginMetadataContainer(pluginMetadata);
    }

    public static class Builder {

        final GsonBuilder gsonBuilder = new GsonBuilder();

        public Builder configureGson(Consumer<GsonBuilder> consumer) {
            consumer.accept(this.gsonBuilder);
            return this;
        }

        public PluginMeta build() {
            return new PluginMeta(new PluginMetadataContainerAdapter(new PluginMetadataCollectionAdapter(new PluginMetadataAdapter(this.gsonBuilder.create()))));
        }

    }
}
