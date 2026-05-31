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
package org.spongepowered.plugin.metadata.builtin.adapter;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;


public final class PluginDependencyAdapter {

    public static final class Deserializer extends TypeAdapter<PluginDependency.Builder> {

        @Override
        public void write(final JsonWriter out, final PluginDependency.Builder builder) throws IOException {
            throw new UnsupportedOperationException("This adapter is for reading only");
        }

        @Override
        public PluginDependency.Builder read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final PluginDependency.Builder builder = PluginDependency.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate dependency key '%s' in %s", key, in));
                }
                switch (key) {
                    case "id":
                        builder.id(in.nextString());
                        break;
                    case "version":
                        builder.version(in.nextString());
                        break;
                    case "optional":
                        builder.optional(in.nextBoolean());
                        break;
                    case "load-order":
                        try {
                            builder.loadOrder(PluginDependency.LoadOrder.valueOf(in.nextString().toUpperCase()));
                        } catch (final Exception ex) {
                            throw new JsonParseException(String.format("Invalid load order found in '%s'", in), ex);
                        }
                        break;
                }
            }
            in.endObject();
            return builder;
        }
    }

    public static final class Serializer extends TypeAdapter<PluginDependency> {

        @Override
        public void write(final JsonWriter out, final PluginDependency value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("id").value(value.id());
            out.name("version").value(value.version().toString());
            out.name("load-order").value(value.loadOrder().name().toLowerCase(Locale.ROOT));
            out.name("optional").value(value.optional());
            out.endObject();
        }

        @Override
        public PluginDependency read(final JsonReader in) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
