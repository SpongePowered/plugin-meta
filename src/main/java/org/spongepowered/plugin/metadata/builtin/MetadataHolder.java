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

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Holder;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class MetadataHolder implements Holder {

    private final String name, loader, license;
    private final VersionRange loaderVersion;
    @Nullable private final Inheritable globalMetadata;
    private final Map<String, PluginMetadata> metadataById = new LinkedHashMap<>();
    private final List<PluginMetadata> metadata = new LinkedList<>();

    private MetadataHolder(final Builder builder) {
        this.name = builder.name;
        this.loader = builder.loader;
        this.license = builder.license;
        this.loaderVersion = builder.loaderVersion;
        this.globalMetadata = builder.globalMetadata;
        this.metadataById.putAll(builder.metadataById);
        this.metadata.addAll(builder.metadata);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String loader() {
        return this.loader;
    }

    @Override
    public VersionRange loaderVersion() {
        return this.loaderVersion;
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
        return Optional.ofNullable(this.metadataById.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public List<PluginMetadata> metadata() {
        return Collections.unmodifiableList(this.metadata);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetadataHolder.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("loaderVersion=" + this.loaderVersion)
                .add("license=" + this.license)
                .add("globalMetadata=" + this.globalMetadata)
                .toString();
    }

    public static final class Builder {
        @Nullable String name, loader, license, rawLoaderVersion = "1.0";
        @Nullable VersionRange loaderVersion;
        @Nullable Inheritable globalMetadata;
        final Map<String, PluginMetadata> metadataById = new LinkedHashMap<>();
        final List<PluginMetadata> metadata = new LinkedList<>();

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder loader(final String loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder loaderVersion(final String loaderVersion) {
            this.rawLoaderVersion = Objects.requireNonNull(loaderVersion, "loaderVersion");
            return this;
        }

        public Builder globalMetadata(final Inheritable globalMetadata) {
            this.globalMetadata = Objects.requireNonNull(globalMetadata, "globalMetadata");
            return this;
        }

        public Builder metadata(final List<PluginMetadata> metadata) {
            for (final PluginMetadata pm : Objects.requireNonNull(metadata, "metadata")) {
                this.metadataById.put(pm.id(), pm);
            }
            this.metadata.addAll(metadata);
            return this;
        }

        public Builder addMetadata(final PluginMetadata metadata) {
            this.metadataById.put(Objects.requireNonNull(metadata, "metadata").id(), metadata);
            this.metadata.remove(metadata);
            this.metadata.add(metadata);
            return this;
        }

        public MetadataHolder build() throws IllegalStateException, InvalidVersionSpecificationException {
            Objects.requireNonNull(this.name, "name");
            Objects.requireNonNull(this.license, "license");
            Objects.requireNonNull(this.rawLoaderVersion, "loaderVersion");

            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A PluginHolder must hold at least 1 PluginMetadata!");
            }
            this.loaderVersion = VersionRange.createFromVersionSpec(this.rawLoaderVersion);

            return new MetadataHolder(this);
        }
    }
}
