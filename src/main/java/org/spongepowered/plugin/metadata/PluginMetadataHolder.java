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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class PluginMetadataHolder {

    private final String loader, loaderVersion, license;
    private final PluginLinks links;
    private final Map<String, PluginMetadata> metadata = new LinkedHashMap<>();
    private PluginMetadata globalMetadata;

    private PluginMetadataHolder(final Builder builder) {
        this.loader = builder.loader;
        this.loaderVersion = builder.loaderVersion;
        this.license = builder.license;
        this.links = builder.links;
        this.metadata.putAll(builder.metadata);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String loader() {
        return this.loader;
    }

    public String loaderVersion() {
        return this.loaderVersion;
    }

    public String license() {
        return this.license;
    }

    public PluginLinks links() {
        return this.links;
    }

    public Optional<PluginMetadata> metadata(final String id) {
        return Optional.ofNullable(this.metadata.get(Objects.requireNonNull(id, "id")));
    }

    public PluginMetadataHolder.Builder toBuilder() {
        final PluginMetadataHolder.Builder builder = PluginMetadataHolder.builder();
        builder.loader = this.loader;
        builder.loaderVersion = this.loaderVersion;
        builder.license = this.license;
        builder.links = this.links;
        this.metadata.forEach((k, v) -> builder.metadata.put(k, v.toBuilder().build()));
        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginMetadataHolder.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("loaderVersion=" + this.loaderVersion)
                .add("license=" + this.license)
                .add("links=" + this.links)
                .toString();
    }

    public static final class Builder {
        @Nullable String loader, loaderVersion, license;
        PluginLinks links = PluginLinks.none();
        final Map<String, PluginMetadata> metadata = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder loader(final String loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder loaderVersion(final String loaderVersion) {
            this.loaderVersion = Objects.requireNonNull(loaderVersion, "loaderVersion");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder links(final PluginLinks links) {
            this.links = Objects.requireNonNull(links, "links");
            return this;
        }

        public Builder metadata(final Collection<PluginMetadata> metadata) {
            Objects.requireNonNull(metadata, "metadata");

            for (final PluginMetadata metadatum : metadata) {
                this.metadata.put(metadatum.id(), metadatum);
            }

            return this;
        }

        public Builder addMetadata(final PluginMetadata metadata) {
            this.metadata.put(Objects.requireNonNull(metadata, "metadata").id(), metadata);
            return this;
        }

        public PluginMetadataHolder build() {
            Objects.requireNonNull(this.loader, "loader");
            Objects.requireNonNull(this.loaderVersion, "loaderVersion");
            Objects.requireNonNull(this.license, "license");
            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A plugin metadata holder requires at least one plugin metadata!");
            }

            return new PluginMetadataHolder(this);
        }
    }
}
