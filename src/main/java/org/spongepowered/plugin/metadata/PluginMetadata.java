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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The specification of a typical plugin's metadata as defined by this library
 *
 * Consult the documentation of the implementation to determine exactly what
 * they classify as a plugin and how this metadata is used.
 */
public final class PluginMetadata {

    private final String loader, id, version, mainClass;
    @Nullable private final String name, description;
    private final PluginLinks links;
    private final List<PluginContributor> contributors;
    private final List<PluginDependency> dependencies;
    private final Map<String, Object> extraMetadata;

    private PluginMetadata(final Builder builder) {
        Objects.requireNonNull(builder);
        Objects.requireNonNull(builder.loader);
        Objects.requireNonNull(builder.id);
        Objects.requireNonNull(builder.version);
        Objects.requireNonNull(builder.mainClass);

        this.loader = builder.loader;
        this.id = builder.id;
        this.name = builder.name;
        this.version = builder.version;
        this.mainClass = builder.mainClass;
        this.description = builder.description;
        this.links = builder.links;
        this.contributors = builder.contributors;
        this.dependencies = builder.dependencies;
        this.extraMetadata = builder.extraMetadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLoader() {
        return this.loader;
    }

    public String getId() {
        return this.id;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public String getVersion() {
        return this.version;
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    public PluginLinks getLinks() {
        return this.links;
    }

    public List<PluginContributor> getContributors() {
        return Collections.unmodifiableList(this.contributors);
    }

    public List<PluginDependency> getDependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }

    public Map<String, Object> getExtraMetadata() {
        return Collections.unmodifiableMap(this.extraMetadata);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginMetadata.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("id=" + this.id)
                .add("name=" + this.name)
                .add("version=" + this.version)
                .add("mainClass=" + this.mainClass)
                .add("description=" + this.description)
                .add("links=" + this.links)
                .add("contributors=" + this.contributors)
                .add("dependencies=" + this.dependencies)
                .add("extraMetadata=" + this.extraMetadata)
                .toString();
    }

    public static final class Builder {

        @Nullable String loader, id, name, version, mainClass, description;
        PluginLinks links = new PluginLinks();
        List<PluginContributor> contributors = new ArrayList<>();
        List<PluginDependency> dependencies = new ArrayList<>();
        Map<String, Object> extraMetadata = new HashMap<>();

        private Builder() {
        }

        public Builder setLoader(final String loader) {
            this.loader = Objects.requireNonNull(loader);
            return this;
        }
        
        public Builder setId(final String id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder setName(@Nullable final String name) {
            this.name = name;
            return this;
        }

        public Builder setVersion(final String version) {
            this.version = Objects.requireNonNull(version);
            return this;
        }

        public Builder setMainClass(final String mainClass) {
            this.mainClass = Objects.requireNonNull(mainClass);
            return this;
        }

        public Builder setDescription(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public Builder setLinks(final PluginLinks links) {
            this.links = Objects.requireNonNull(links);
            return this;
        }

        public Builder setContributors(final List<PluginContributor> contributors) {
            this.contributors = Objects.requireNonNull(contributors);
            return this;
        }

        public Builder contributor(final PluginContributor developer) {
            this.contributors.add(Objects.requireNonNull(developer));
            return this;
        }

        public Builder setDependencies(final List<PluginDependency> dependencies) {
            this.dependencies = Objects.requireNonNull(dependencies);
            return this;
        }

        public Builder dependency(final PluginDependency dependency) {
            Objects.requireNonNull(dependency);
            this.dependencies.add(dependency);
            return this;
        }

        public Builder setExtraMetadata(final Map<String, Object> extraMetadata) {
            this.extraMetadata = Objects.requireNonNull(extraMetadata);
            return this;
        }

        public Builder extraMetadata(final String key, final Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);

            this.extraMetadata.put(key, value);
            return this;
        }

        public PluginMetadata build() {
            Objects.requireNonNull(this.loader);
            Objects.requireNonNull(this.id);
            Objects.requireNonNull(this.version);
            Objects.requireNonNull(this.mainClass);

            return new PluginMetadata(this);
        }
    }
}
