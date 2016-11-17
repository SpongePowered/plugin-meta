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

package org.spongepowered.plugin.meta.gson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.plugin.meta.PluginDependency;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public final class ModMetadataAdapter extends TypeAdapter<PluginMetadata> {

    public static final ModMetadataAdapter DEFAULT = new ModMetadataAdapter(new Gson(), ImmutableMap.of());

    private static final char VERSION_SEPARATOR = '@';

    private final Gson gson;
    private final ImmutableMap<String, Class<?>> extensions;

    public ModMetadataAdapter(Gson gson, ImmutableMap<String, Class<?>> extensions) {
        this.gson = gson;
        this.extensions = extensions;
    }

    public Gson getGson() {
        return this.gson;
    }

    public ImmutableMap<String, Class<?>> getExtensions() {
        return this.extensions;
    }

    public Class<?> getExtension(String key) {
        Class<?> result = this.extensions.get(key);
        return result != null ? result : Object.class;
    }

    @Override
    public PluginMetadata read(JsonReader in) throws IOException {
        in.beginObject();

        final Set<String> processedKeys = new HashSet<>();

        final PluginMetadata result = new PluginMetadata("unknown");
        String id = null;

        Set<String> requiredDependencies = new HashSet<>();

        while (in.hasNext()) {
            final String name = in.nextName();
            if (!processedKeys.add(name)) {
                throw new JsonParseException("Duplicate key '" + name + "' in " + in);
            }

            switch (name) {
                case "modid":
                    id = in.nextString();
                    result.setId(id);
                    break;
                case "name":
                    result.setName(in.nextString());
                    break;
                case "version":
                    result.setVersion(in.nextString());
                    break;
                case "description":
                    result.setDescription(in.nextString());
                    break;
                case "url":
                    result.setUrl(in.nextString());
                    break;
                case "authorList":
                    in.beginArray();
                    while (in.hasNext()) {
                        result.addAuthor(in.nextString());
                    }
                    in.endArray();
                    break;
                case "requiredMods":
                    in.beginArray();
                    while (in.hasNext()) {
                        // The version in requiredMods is redundant, we can just ignore it
                        PluginDependency dependency = readDependency(in);

                        // Attempt to update existing dependency
                        PluginDependency existing = result.getDependency(dependency.getId());
                        if (existing != null) {
                            // Make existing dependency required
                            result.replaceDependency(existing.required());
                        } else {
                            // Register dependency as required
                            requiredDependencies.add(dependency.getId());
                        }
                    }
                    in.endArray();
                    break;
                case "dependencies":
                    in.beginArray();
                    while (in.hasNext()) {
                        PluginDependency dependency = readDependency(in);

                        // Make dependency required if we already know it is required
                        if (requiredDependencies.contains(dependency.getId())) {
                            dependency = dependency.required();
                        }

                        result.addDependency(dependency);
                    }
                    in.endArray();
                    break;
                default:
                    result.setExtension(name, this.gson.fromJson(in, getExtension(name)));
            }
        }

        in.endObject();

        if (id == null) {
            throw new JsonParseException("Mod metadata is missing required element 'modid'");
        }

        return result;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void write(JsonWriter out, PluginMetadata meta) throws IOException {
        out.beginObject();
        out.name("modid").value(meta.getId());
        writeIfPresent(out, "name", meta.getName());
        writeIfPresent(out, "version", meta.getVersion());
        writeIfPresent(out, "description", meta.getDescription());
        writeIfPresent(out, "url", meta.getUrl());

        if (!meta.getAuthors().isEmpty()) {
            out.name("authorList").beginArray();
            for (String author : meta.getAuthors()) {
                out.value(author);
            }
            out.endArray();
        }

        Set<PluginDependency> requiredDependencies = new HashSet<>(meta.getDependencies());
        requiredDependencies.removeIf(PluginDependency::isOptional);
        writeDependencies(out, "requiredMods", requiredDependencies);

        for (Map.Entry<String, Object> entry : meta.getExtensions().entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            out.name(key);
            this.gson.toJson(value, getExtension(key), out);
        }

        out.endObject();
    }

    private static void writeIfPresent(JsonWriter out, String key, @Nullable String value) throws IOException {
        if (value != null) {
            out.name(key).value(value);
        }
    }

    private static void writeDependencies(JsonWriter out, String key, Collection<PluginDependency> dependencies) throws IOException {
        if (!dependencies.isEmpty()) {
            out.name(key).beginArray();
            for (PluginDependency dependency : dependencies) {
                writeDependency(out, dependency);
            }
            out.endArray();
        }
    }

    private static PluginDependency readDependency(JsonReader in) throws IOException {
        final String version = in.nextString();
        int pos = version.indexOf(VERSION_SEPARATOR);
        if (pos < 0) {
            return new PluginDependency(version, null, false);
        } else {
            return new PluginDependency(version.substring(0, pos), version.substring(pos + 1), false);
        }
    }

    private static void writeDependency(JsonWriter out, PluginDependency dependency) throws IOException {
        if (dependency.getVersion() == null) {
            out.value(dependency.getId());
        } else {
            out.value(dependency.getId() + VERSION_SEPARATOR + dependency.getVersion());
        }
    }

}
