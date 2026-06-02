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
package org.spongepowered.plugin.metadata.builtin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.spongepowered.plugin.metadata.builtin.adapter.MetadataContainerAdapter;
import org.spongepowered.plugin.metadata.builtin.adapter.StandardInheritableAdapter;
import org.spongepowered.plugin.metadata.builtin.adapter.StandardPluginMetadataBuilderDeserializer;
import org.spongepowered.plugin.metadata.builtin.adapter.StandardPluginMetadataSerializer;
import org.spongepowered.plugin.metadata.builtin.adapter.model.*;
import org.spongepowered.plugin.metadata.builtin.adapter.version.ArtifactVersionAdapter;
import org.spongepowered.plugin.metadata.builtin.adapter.version.VersionRangeAdapter;
import org.spongepowered.plugin.metadata.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class MetadataParser {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ContainerLoader.class, new ContainerLoaderAdapter())
            .registerTypeAdapter(PluginBranding.class, new PluginBrandingAdapter())
            .registerTypeAdapter(PluginContributor.class, new PluginContributorAdapter())
            .registerTypeAdapter(PluginDependency.class, new PluginDependencyAdapter())
            .registerTypeAdapter(PluginLinks.class, new PluginLinksAdapter())
            .registerTypeAdapter(ArtifactVersion.class, new ArtifactVersionAdapter())
            .registerTypeAdapter(VersionRange.class, new VersionRangeAdapter())
            .registerTypeAdapter(MetadataContainer.class, new MetadataContainerAdapter())
            .registerTypeAdapter(StandardInheritable.class, new StandardInheritableAdapter())
            .registerTypeAdapter(StandardPluginMetadata.Builder.class, new StandardPluginMetadataBuilderDeserializer())
            .registerTypeAdapter(StandardPluginMetadata.class, new StandardPluginMetadataSerializer())
            .create();

    private MetadataParser() {
    }

    public static Gson gson() {
        return MetadataParser.GSON;
    }

    /**
     * Reads a {@link MetadataContainer container} from a given {@link Path path} using the default {@link Gson deserializer}
     * (retrieved from {@link #gson()}).
     *
     * @param path The path
     * @return The container
     * @throws IOException if the container fails to be read
     */
    public static MetadataContainer read(final Path path) throws IOException {
        return MetadataParser.read(path, MetadataParser.gson());
    }

    /**
     * Reads a {@link MetadataContainer container} from a given {@link Path path} with configured {@link Gson deserializer}.
     * <p>
     * To get a standard deserializer, {@link MetadataParser#gson()} is available.
     * @param path The path
     * @param gson The deserializer
     * @return The container
     * @throws IOException if the container fails to be read
     */
    public static MetadataContainer read(final Path path, final Gson gson) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(gson, "gson");

        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, MetadataContainer.class);
        }
    }

    /**
     * Reads a {@link MetadataContainer container} from a given {@link Reader reader} using the default {@link Gson deserializer}
     * (retrieved from {@link #gson()}).
     *
     * @param reader The reader
     * @return The container
     * @throws IOException if the container fails to be read
     */
    public static MetadataContainer read(final Reader reader) throws IOException {
        return MetadataParser.read(reader, MetadataParser.gson());
    }

    /**
     * Reads a {@link MetadataContainer container} from a given {@link Reader reader} with configured {@link Gson deserializer}.
     * <p>
     * To get a standard deserializer, {@link MetadataParser#gson()} is available.
     * @param reader The reader
     * @param gson The deserializer
     * @return The container
     * @throws IOException if the container fails to deserialize
     */
    public static MetadataContainer read(final Reader reader, final Gson gson) throws IOException {
        Objects.requireNonNull(reader, "reader");
        Objects.requireNonNull(gson, "gson");

        try (final JsonReader jsonReader = new JsonReader(reader)) {
            return gson.fromJson(jsonReader, MetadataContainer.class);
        }
    }

    /**
     * Writes a {@link MetadataContainer container} to the given {@link Path path} using the default {@link Gson deserializer}
     * (retrieved from {@link #gson()}).
     *
     * @param path The path
     * @param container The container
     * @param indent True to indent (pretty print) the resulting JSON, false if not
     * @throws IOException If the container fails to serialize
     */
    public static void write(final Path path, final MetadataContainer container, final boolean indent) throws IOException {
        MetadataParser.write(path, container, MetadataParser.gson(), indent);
    }

    /**
     * Writes a {@link MetadataContainer container} to the given {@link Path path} using the configured {@link Gson serializer}.
     * <p>
     * To get a standard serializer, {@link MetadataParser#gson()} is available.
     * @param path The path
     * @param container The container
     * @param gson The serializer
     * @param indent True to indent (pretty print) the resulting JSON, false if not
     * @throws IOException If the container fails to serialize
     */
    public static void write(final Path path, final MetadataContainer container, final Gson gson, final boolean indent) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(container, "container");
        Objects.requireNonNull(gson, "gson");

        try (final JsonWriter writer = new JsonWriter(Files.newBufferedWriter(path))) {
            if (indent) {
                writer.setIndent("  ");
            }
            gson.toJson(container, MetadataContainer.class, writer);
        }
    }

    /**
     * Writes a {@link MetadataContainer container} to the given {@link Path path} using the default {@link Gson deserializer}
     * (retrieved from {@link #gson()}).
     *
     * @param writer The writer
     * @param container The container
     * @param indent True to indent (pretty print) the resulting JSON, false if not
     * @throws IOException If the container fails to serialize
     */
    public static void write(final Writer writer, final MetadataContainer container, final boolean indent) throws IOException {
        MetadataParser.write(writer, container, MetadataParser.gson(), indent);
    }

    /**
     * Writes a {@link MetadataContainer container} to the given {@link Path path} using the configured {@link Gson serializer}.
     * <p>
     * To get a standard serializer, {@link MetadataParser#gson()} is available.
     * @param writer The writer
     * @param container The container
     * @param gson The serializer
     * @param indent True to indent (pretty print) the resulting JSON, false if not
     * @throws IOException If the container fails to serialize
     */
    public static void write(final Writer writer, final MetadataContainer container, final Gson gson, final boolean indent) throws IOException {
        Objects.requireNonNull(writer, "writer");
        Objects.requireNonNull(container, "container");
        Objects.requireNonNull(gson, "gson");

        try (final JsonWriter jsonWriter = new JsonWriter(writer)) {
            if (indent) {
                jsonWriter.setIndent("  ");
            }
            gson.toJson(container, MetadataContainer.class, jsonWriter);
        }
    }
}
