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
import org.apache.maven.artifact.versioning.VersionRange;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.model.PluginLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public final class StandardPluginLoader implements PluginLoader {

    private final String name;
    private final String rawVersion;
    private final VersionRange version;

    private StandardPluginLoader(final Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.rawVersion = builder.rawVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public VersionRange version() {
        return this.version;
    }

    public StandardPluginLoader.Builder toBuilder() {
        final Builder builder = StandardPluginLoader.builder();
        builder.name = this.name;
        builder.version = this.version;
        builder.rawVersion = this.rawVersion;

        return builder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StandardPluginLoader)) {
            return false;
        }
        final StandardPluginLoader that = (StandardPluginLoader) o;
        return this.name.equals(that.name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StandardPluginLoader.class.getSimpleName() + "[", "]")
                .add("name=" + this.name)
                .add("version=" + this.rawVersion)
                .toString();
    }

    public static final class Builder {

        @Nullable String name, rawVersion;
        @Nullable VersionRange version;

        private Builder() {
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder version(final String version) {
            this.version = VersionRange.createFromVersion(Objects.requireNonNull(version, "version"));
            this.rawVersion = version;
            return this;
        }

        public StandardPluginLoader build() {
            Objects.requireNonNull(this.name, "name");
            Objects.requireNonNull(this.version, "version");

            return new StandardPluginLoader(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<StandardPluginLoader.Builder> {

        @Override
        public void write(final JsonWriter out, final Builder builder) throws IOException {
            throw new UnsupportedOperationException("This adapter is for reading only");
        }

        @Override
        public StandardPluginLoader.Builder read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final StandardPluginLoader.Builder builder = StandardPluginLoader.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate id key '%s' in %s", key, in));
                }
                switch (key) {
                    case "name":
                        builder.name(in.nextString());
                        break;
                    case "version":
                        builder.version(in.nextString());
                        break;
                }
            }
            in.endObject();

            return builder;
        }
    }

    public static final class Serializer extends TypeAdapter<StandardPluginLoader> {

        @Override
        public void write(final JsonWriter out, final StandardPluginLoader value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("name").value(value.name);
            out.name("version").value(value.rawVersion);
            out.endObject();
        }

        @Override
        public StandardPluginLoader read(final JsonReader in) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
