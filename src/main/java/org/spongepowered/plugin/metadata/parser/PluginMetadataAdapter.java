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
package org.spongepowered.plugin.metadata.parser;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.plugin.metadata.PluginContributor;
import org.spongepowered.plugin.metadata.PluginDependency;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PluginMetadataAdapter extends TypeAdapter<PluginMetadata> {

    private final Gson gson;

    public PluginMetadataAdapter(final Gson gson) {
        this.gson = Preconditions.checkNotNull(gson);
    }

    @Override
    public void write(final JsonWriter out, final PluginMetadata value) throws IOException {

    }

    @Override
    public PluginMetadata read(final JsonReader in) throws IOException {
        in.beginObject();
        final Set<String> processedKeys = new HashSet<>();
        final PluginMetadata.Builder builder = PluginMetadata.builder();
        while (in.hasNext()) {
            final String key = in.nextName();
            if (!processedKeys.add(key)) {
                throw new JsonParseException("Duplicate key '" + key + "' in " + in);
            }

            switch (key) {
                case "id":
                    builder.setId(in.nextString());
                    break;
                case "name":
                    builder.setName(in.nextString());
                    break;
                case "version":
                    builder.setVersion(in.nextString());
                    break;
                case "main-class":
                    builder.setMainClass(in.nextString());
                    break;
                case "description":
                    builder.setDescription(in.nextString());
                    break;
                case "links":
                    this.readLinks(in, builder);
                    break;
                case "contributors":
                    this.readContributors(in, builder);
                    break;
                case "dependencies":
                    this.readDependencies(in, builder);
                    break;
                case "extra":
                    in.beginObject();
                    final Map<String, String> extraMetadata = new HashMap<>();
                    while (in.hasNext()) {
                        final String eKey = in.nextName();
                        final String eValue = in.nextString();
                        extraMetadata.put(eKey, eValue);
                    }
                    in.endObject();
                    // TODO Move Extra Metadata to String -> String
                    builder.setExtraMetadata((Map<String, Object>) (Object) extraMetadata);
            }
        }
        in.endObject();
        return builder.build();
    }

    private void readLinks(final JsonReader in, final PluginMetadata.Builder builder) throws IOException {
        in.beginObject();
        final Set<String> processedKeys = new HashSet<>();
        while (in.hasNext()) {
            final String key = in.nextName();
            if (!processedKeys.add(key)) {
                throw new JsonParseException("Duplicate key '" + key + "' in " + in);
            }
            switch (key) {
                case "homepage":
                    builder.setHomepage(new URL(in.nextString()));
                    break;
                case "source":
                    builder.setSource(new URL(in.nextString()));
                    break;
                case "issues":
                    builder.setIssues(new URL(in.nextString()));
                    break;
            }
        }
        in.endObject();
    }

    private void readContributors(final JsonReader in, final PluginMetadata.Builder builder) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            builder.contributor(this.readContributor(in));
        }
        in.endArray();
    }

    private PluginContributor readContributor(final JsonReader in) throws IOException {
        in.beginObject();
        final Set<String> processedKeys = new HashSet<>();
        final PluginContributor.Builder builder = PluginContributor.builder();
        while (in.hasNext()) {
            final String key = in.nextName();
            if (!processedKeys.add(key)) {
                throw new JsonParseException("Duplicate key '" + key + "' in " + in);
            }
            switch (key) {
                case "name":
                    builder.setName(in.nextString());
                    break;
                case "description":
                    builder.setDescription(in.nextString());
                    break;
            }
        }
        in.endObject();
        return builder.build();
    }

    private void readDependencies(final JsonReader in, final PluginMetadata.Builder builder) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            builder.dependency(this.readDependency(in));
        }
        in.endArray();
    }

    private PluginDependency readDependency(final JsonReader in) throws IOException {
        in.beginObject();
        final Set<String> processedKeys = new HashSet<>();
        final PluginDependency.Builder builder = PluginDependency.builder();
        while (in.hasNext()) {
            final String key = in.nextName();
            if (!processedKeys.add(key)) {
                throw new JsonParseException("Duplicate key '" + key + "' in " + in);
            }
            switch (key) {
                case "id":
                    builder.setId(in.nextString());
                    break;
                case "version":
                    builder.setVersion(in.nextString());
                    break;
                case "optional":
                    builder.setOptional(in.nextBoolean());
                    break;
                case "load-order":
                    try {
                        builder.setLoadOrder(PluginDependency.LoadOrder.valueOf(in.nextString().toUpperCase()));
                    } catch (final Exception ex) {
                        throw new JsonParseException("Invalid load order found in " + in, ex);
                    }
                    break;
            }
        }
        in.endObject();
        return builder.build();
    }
}
