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

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.util.GsonUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

public final class StandardPluginContributor implements PluginContributor {

    private final String name;
    @Nullable private final String description;

    private StandardPluginContributor(final Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    public StandardPluginContributor.Builder toBuilder() {
        final Builder builder = new Builder();
        builder.name = this.name;
        builder.description = this.description;

        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StandardPluginContributor.class.getSimpleName() + "[", "]")
                .add("name=" + this.name)
                .add("description=" + this.description)
                .toString();
    }

    public static final class Builder {

        @Nullable String name, description;

        private Builder() {
        }

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder description(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public StandardPluginContributor build() {
            Objects.requireNonNull(this.name, "name");

            return new StandardPluginContributor(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<StandardPluginContributor.Builder> {

        @Override
        public void write(final JsonWriter out, final Builder builder) throws IOException {
            throw new UnsupportedOperationException("This adapter is for reading only");
        }

        @Override
        public Builder read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final StandardPluginContributor.Builder builder = StandardPluginContributor.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate contributor key '%s' in %s", key, in));
                }
                switch (key) {
                    case "name":
                        builder.name(in.nextString());
                        break;
                    case "description":
                        builder.description(in.nextString());
                        break;
                }
            }
            in.endObject();
            return builder;
        }
    }

    public static final class Serializer extends TypeAdapter<StandardPluginContributor> {

        @Override
        public void write(final JsonWriter out, final StandardPluginContributor value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("name").value(value.name());
            GsonUtils.writeIfPresent(out, "description", value.description());
            out.endObject();
        }

        @Override
        public StandardPluginContributor read(final JsonReader in) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
