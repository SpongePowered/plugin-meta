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

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Container;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.ContainerLoader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class MetadataContainer implements Container {

    private final String license;
    private final ContainerLoader loader;
    @Nullable private final Inheritable globalMetadata;
    private final Map<String, StandardPluginMetadata> metadata;

    private MetadataContainer(final Builder builder) {
        this.loader = builder.loader;
        this.license = builder.license;
        this.globalMetadata = builder.globalMetadata;
        this.metadata = Map.copyOf(builder.metadata);

        for (final StandardPluginMetadata element : this.metadata.values()) {
            element.setContainer(this);
        }
    }

    @Override
    public ContainerLoader loader() {
        return this.loader;
    }

    @Override
    public String license() {
        return this.license;
    }

    @Override
    public Optional<Inheritable> globalMetadata() {
        return Optional.ofNullable(this.globalMetadata);
    }

    @Override
    public Optional<PluginMetadata> metadata(final String id) {
        return Optional.ofNullable(this.metadata.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public Collection<StandardPluginMetadata> metadata() {
        return this.metadata.values();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetadataContainer.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("license=" + this.license)
                .add("globalMetadata=" + this.globalMetadata)
                .toString();
    }

    public MetadataContainer.Builder toBuilder() {
        return new MetadataContainer.Builder().from(this);
    }

    public static MetadataContainer.Builder builder() {
        return new MetadataContainer.Builder();
    }

    public static final class Builder {

        private final Map<String, StandardPluginMetadata> metadata = new LinkedHashMap<>();
        private @MonotonicNonNull String license;
        private @MonotonicNonNull ContainerLoader loader;
        private @Nullable Inheritable globalMetadata;

        private Builder() {}

        public Builder loader(final ContainerLoader loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder globalMetadata(final @Nullable Inheritable globalMetadata) {
            this.globalMetadata = globalMetadata;
            return this;
        }

        public Builder metadata(final Collection<? extends StandardPluginMetadata> metadata) {
            Objects.requireNonNull(metadata, "metadata");
            this.metadata.clear();
            return this.addMetadata(metadata);
        }

        public Builder addMetadata(final Collection<? extends StandardPluginMetadata> metadata) {
            for (final StandardPluginMetadata element : Objects.requireNonNull(metadata, "metadata")) {
                this.metadata.put(Objects.requireNonNull(element, "element").id(), element);
            }
            return this;
        }

        public Builder addMetadata(final StandardPluginMetadata metadata) {
            this.metadata.put(Objects.requireNonNull(metadata, "metadata").id(), metadata);
            return this;
        }

        public Builder from(final MetadataContainer value) {
            Objects.requireNonNull(value, "value");
            this.loader = value.loader;
            this.license = value.license;
            this.globalMetadata = value.globalMetadata;
            this.metadata.clear();
            this.metadata.putAll(value.metadata);
            return this;
        }

        public MetadataContainer build() throws IllegalStateException {
            Objects.requireNonNull(this.license, "license");
            Objects.requireNonNull(this.loader, "loader");

            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A MetadataHolder must hold at least 1 PluginMetadata!");
            }

            return new MetadataContainer(this);
        }
    }
}
