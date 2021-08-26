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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class MetadataParser {

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(MetadataHolder.class, new MetadataHolder.Serializer())
                .registerTypeAdapter(StandardInheritable.class, new StandardInheritable.Serializer())
                .registerTypeAdapter(StandardPluginMetadata.Builder.class, new StandardPluginMetadata.Deserializer())
                .registerTypeAdapter(StandardPluginMetadata.class, new StandardPluginMetadata.Serializer())
                ;
    }

    public static MetadataHolder read(final Path path, final Gson gson) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(gson, "gson");

        try (final JsonReader reader = new JsonReader(Files.newBufferedReader(path))) {
            return gson.fromJson(reader, MetadataHolder.class);
        }
    }

    public static void write(final Path path, final MetadataHolder holder, final Gson gson, final boolean indent) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(holder, "holder");
        Objects.requireNonNull(gson, "gson");

        try (final JsonWriter writer = new JsonWriter(Files.newBufferedWriter(path))) {
            if (indent) {
                writer.setIndent("  ");
            }
            gson.toJson(holder, MetadataHolder.class, writer);
        }
    }

    private MetadataParser() {
    }
}
