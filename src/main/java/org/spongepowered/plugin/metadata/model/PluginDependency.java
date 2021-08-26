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
import org.spongepowered.plugin.metadata.Constants;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Specification for an entity considered to be a "dependency" for a plugin.
 *
 * Required: Id, Version, LoadOrder (defaults to {@link LoadOrder#UNDEFINED}),
 * IsOptional (defaults to false)
 *
 * How these values are used are not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginDependency {

    private final String id;
    private final VersionRange version;
    private final String rawVersion;
    private final LoadOrder loadOrder;
    private final boolean optional;

    private PluginDependency(final Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.rawVersion = builder.rawVersion;
        this.loadOrder = builder.loadOrder;
        this.optional = builder.optional;
    }

    /**
     * Returns a new {@link Builder} for creating a PluginDependency.
     *
     * @return A builder
     */
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

    public LoadOrder loadOrder() {
        return this.loadOrder;
    }

    public boolean optional() {
        return this.optional;
    }

    public PluginDependency.Builder toBuilder() {
        final Builder builder = PluginDependency.builder();
        builder.id = this.id;
        builder.version = this.version;
        builder.rawVersion = this.rawVersion;
        builder.loadOrder = this.loadOrder;
        builder.optional = this.optional;

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
        if (!(o instanceof PluginDependency)) {
            return false;
        }
        final PluginDependency that = (PluginDependency) o;
        return this.id.equals(that.id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginDependency.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("version=" + this.rawVersion)
                .add("loadOrder=" + this.loadOrder)
                .add("optional=" + this.optional)
                .toString();
    }

    /**
     * Represents the ordering of the dependency being loaded vs. the plugin by the implementation
     */
    public enum LoadOrder {
        /**
         * The plugin can be loaded regardless of when the dependency is loaded.
         */
        UNDEFINED,
        /**
         * The plugin must be loaded before the dependency
         */
        BEFORE,
        /**
         * The plugin must be loaded after the dependency.
         */
        AFTER
    }

    public static final class Builder {

        @Nullable String id;
        @Nullable VersionRange version;
        @Nullable String rawVersion;
        LoadOrder loadOrder = LoadOrder.UNDEFINED;
        boolean optional = false;

        private Builder() {
        }

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder version(final String version) {
            this.version = VersionRange.createFromVersion(Objects.requireNonNull(version, "version"));
            this.rawVersion = version;
            return this;
        }

        public Builder loadOrder(final LoadOrder loadOrder) {
            this.loadOrder = Objects.requireNonNull(loadOrder, "load order");
            return this;
        }

        public Builder optional(final boolean optional) {
            this.optional = optional;
            return this;
        }

        public PluginDependency build() {
            Objects.requireNonNull(this.id, "id");
            if (!Constants.VALID_ID_PATTERN.matcher(this.id).matches()) {
                throw new IllegalStateException(String.format("Dependency with supplied ID '{%s}' is invalid. %s", this.id,
                        Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
            }
            Objects.requireNonNull(this.version, "version");

            return new PluginDependency(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<PluginDependency.Builder> {

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
            out.name("version").value(value.rawVersion());
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
