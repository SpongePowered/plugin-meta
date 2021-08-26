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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.util.GsonUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Specification for an entity representing the branding of a plugin.
 *
 * How these values are used are not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginBranding {

    private static final PluginBranding NONE = new PluginBranding();

    private final @Nullable String logo, icon;

    private PluginBranding(final Builder builder) {
        this.logo = builder.logo;
        this.icon = builder.icon;
    }

    private PluginBranding() {
        this.logo = null;
        this.icon = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PluginBranding none() {
        return PluginBranding.NONE;
    }

    public Optional<String> logo() {
        return Optional.ofNullable(this.logo);
    }

    public Optional<String> icon() {
        return Optional.ofNullable(this.icon);
    }

    public PluginBranding.Builder toBuilder() {
        final Builder builder = PluginBranding.builder();
        builder.logo = this.logo;
        builder.icon = this.icon;

        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginBranding.class.getSimpleName() + "[", "]")
                .add("logo=" + this.logo)
                .add("icon=" + this.icon)
                .toString();
    }

    public static final class Builder {

        @Nullable String logo, icon;

        private Builder() {
        }

        public Builder logo(@Nullable final String logo) {
            this.logo = logo;
            return this;
        }

        public Builder icon(@Nullable final String icon) {
            this.icon = icon;
            return this;
        }

        public PluginBranding build() {
            return new PluginBranding(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<PluginBranding.Builder> {

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
            final PluginBranding.Builder builder = PluginBranding.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate branding key '%s' in %s", key, in));
                }
                switch (key) {
                    case "logo":
                        builder.logo(in.nextString());
                        break;
                    case "icon":
                        builder.icon(in.nextString());
                        break;
                }
            }
            in.endObject();

            return builder;
        }
    }

    public static final class Serializer extends TypeAdapter<PluginBranding> {

        @Override
        public void write(final JsonWriter out, final PluginBranding value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            GsonUtils.writeIfPresent(out, "logo", value.logo());
            GsonUtils.writeIfPresent(out, "icon", value.icon());
            out.endObject();
        }

        @Override
        public PluginBranding read(final JsonReader in) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
