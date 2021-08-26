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

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Specification for an entity representing the loader of a holder.
 *
 * How these values are used are not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginLoader {

    private final String id;
    private final String rawVersion;
    private final VersionRange version;

    private PluginLoader(final Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.rawVersion = builder.rawVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return this.id;
    }

    public VersionRange version() {
        return this.version;
    }

    protected String rawVersion() {
        return this.rawVersion;
    }

    public PluginLoader.Builder toBuilder() {
        final Builder builder = PluginLoader.builder();
        builder.id = this.id;
        builder.version = this.version;
        builder.rawVersion = this.rawVersion;

        return builder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginLoader)) {
            return false;
        }
        final PluginLoader that = (PluginLoader) o;
        return this.id.equals(that.id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginLoader.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("version=" + this.rawVersion)
                .toString();
    }

    public static final class Builder {

        @Nullable String id, rawVersion;
        @Nullable VersionRange version;

        private Builder() {
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder version(final String version) {
            this.version = VersionRange.createFromVersion(Objects.requireNonNull(version, "version"));
            this.rawVersion = version;
            return this;
        }

        public PluginLoader build() {
            Objects.requireNonNull(this.id, "id");
            Objects.requireNonNull(this.version, "version");

            return new PluginLoader(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<PluginLoader.Builder> {

        @Override
        public void write(final JsonWriter out, final Builder builder) throws IOException {
            throw new UnsupportedOperationException("This adapter is for reading only");
        }

        @Override
        public PluginLoader.Builder read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final PluginLoader.Builder builder = PluginLoader.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate id key '%s' in %s", key, in));
                }
                switch (key) {
                    case "id":
                        builder.id(in.nextString());
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

    public static final class Serializer extends TypeAdapter<PluginLoader> {

        @Override
        public void write(final JsonWriter out, final PluginLoader value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("id").value(value.id());
            out.name("version").value(value.rawVersion());
            out.endObject();
        }

        @Override
        public PluginLoader read(final JsonReader in) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
