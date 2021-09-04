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
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.util.GsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

public final class StandardPluginLinks implements PluginLinks {

    private static final StandardPluginLinks NONE = new StandardPluginLinks();

    private final @Nullable URL homepage, source, issues;

    private StandardPluginLinks(final Builder builder) {
        this.homepage = builder.homepage;
        this.source = builder.source;
        this.issues = builder.issues;
    }

    private StandardPluginLinks() {
        this.homepage = null;
        this.source = null;
        this.issues = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StandardPluginLinks none() {
        return StandardPluginLinks.NONE;
    }

    @Override
    public Optional<URL> homepage() {
        return Optional.ofNullable(this.homepage);
    }

    @Override
    public Optional<URL> source() {
        return Optional.ofNullable(this.source);
    }

    @Override
    public Optional<URL> issues() {
        return Optional.ofNullable(this.issues);
    }

    public StandardPluginLinks.Builder toBuilder() {
        final Builder builder = new Builder();
        builder.homepage = this.homepage;
        builder.source = this.source;
        builder.issues = this.issues;

        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StandardPluginLinks.class.getSimpleName() + "[", "]")
                .add("homepage=" + this.homepage)
                .add("source=" + this.source)
                .add("issues=" + this.issues)
                .toString();
    }

    public static final class Builder {

        @Nullable URL homepage, source, issues;

        private Builder() {
        }

        public Builder homepage(@Nullable final URL homepage) {
            this.homepage = homepage;
            return this;
        }

        public Builder source(@Nullable final URL source) {
            this.source = source;
            return this;
        }

        public Builder issues(@Nullable final URL issues) {
            this.issues = issues;
            return this;
        }

        public StandardPluginLinks build() {
            return new StandardPluginLinks(this);
        }
    }

    public static final class Deserializer extends TypeAdapter<StandardPluginLinks.Builder> {

        @Override
        public void write(final JsonWriter in, final Builder builder) throws IOException {
            throw new UnsupportedOperationException("This adapter is for reading only");
        }

        @Override
        public StandardPluginLinks.Builder read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final StandardPluginLinks.Builder builder = StandardPluginLinks.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate links key '%s' in %s", key, in));
                }
                switch (key) {
                    case "homepage":
                        builder.homepage(new URL(in.nextString()));
                        break;
                    case "source":
                        builder.source(new URL(in.nextString()));
                        break;
                    case "issues":
                        builder.issues(new URL(in.nextString()));
                        break;
                }
            }
            in.endObject();

            return builder;
        }
    }

    public static final class Serializer extends TypeAdapter<StandardPluginLinks> {

        @Override
        public void write(final JsonWriter out, final StandardPluginLinks value) throws IOException {
            Objects.requireNonNull(out, "out");

            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            GsonUtils.writeIfPresent(out, "homepage", value.homepage());
            GsonUtils.writeIfPresent(out, "source", value.source());
            GsonUtils.writeIfPresent(out, "issues", value.issues());
            out.endObject();
        }

        @Override
        public StandardPluginLinks read(final JsonReader out) throws IOException {
            throw new UnsupportedOperationException("This adapter is for writing only");
        }
    }
}
