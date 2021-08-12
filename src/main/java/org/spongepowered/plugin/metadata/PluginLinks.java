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
package org.spongepowered.plugin.metadata;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.util.AdapterUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Specification for an entity representing the links to "web resources" of a plugin.
 *
 * How these values are used is not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginLinks {

    private final @Nullable URL homepage, source, issues;

    private PluginLinks(final Builder builder) {
        this.homepage = builder.homepage;
        this.source = builder.source;
        this.issues = builder.issues;
    }

    private PluginLinks() {
        this.homepage = null;
        this.source = null;
        this.issues = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PluginLinks none() {
        return new PluginLinks();
    }

    public Optional<URL> homepage() {
        return Optional.ofNullable(this.homepage);
    }

    public Optional<URL> source() {
        return Optional.ofNullable(this.source);
    }

    public Optional<URL> issues() {
        return Optional.ofNullable(this.issues);
    }

    public PluginLinks.Builder toBuilder() {
        final Builder builder = PluginLinks.builder();
        builder.homepage = this.homepage;
        builder.source = this.source;
        builder.issues = this.issues;

        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginLinks.class.getSimpleName() + "[", "]")
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

        public PluginLinks build() {
            return new PluginLinks(this);
        }
    }

    public static final class Adapter extends TypeAdapter<PluginLinks> {

        private static final Adapter INSTANCE = new Adapter();

        public static Adapter instance() {
            return Adapter.INSTANCE;
        }

        @Override
        public void write(final JsonWriter out, final PluginLinks links) throws IOException {
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(links, "links");

            out.beginObject();
            AdapterUtils.writeURLIfPresent(out, "homepage", links.homepage());
            AdapterUtils.writeURLIfPresent(out, "source", links.source());
            AdapterUtils.writeURLIfPresent(out, "issues", links.issues());
            out.endObject();
        }

        @Override
        public PluginLinks read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final PluginLinks.Builder builder = PluginLinks.builder();
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

            return builder.build();
        }
    }
}
