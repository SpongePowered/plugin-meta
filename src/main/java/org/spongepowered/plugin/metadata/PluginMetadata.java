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

    public String loader() {
        return this.loader;
    }

    public String id() {
        return this.id;
    }

    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    public String version() {
        return this.version;
    }

    public String mainClass() {
        return this.mainClass;
    }

    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    public PluginLinks links() {
        return this.links;
    }

    public List<PluginContributor> contributors() {
        return Collections.unmodifiableList(this.contributors);
    }

    public List<PluginDependency> dependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }

    public Map<String, Object> extraMetadata() {
        return Collections.unmodifiableMap(this.extraMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PluginMetadata)) {
            return false;
        }

        final PluginMetadata that = (PluginMetadata) o;
        return this.id.equals(that.id);
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
        final List<PluginContributor> contributors = new ArrayList<>();
        final List<PluginDependency> dependencies = new ArrayList<>();
        final Map<String, Object> extraMetadata = new HashMap<>();

        private Builder() {
        }

        public Builder loader(final String loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }
        
        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder name(@Nullable final String name) {
            this.name = name;
            return this;
        }

        public Builder version(final String version) {
            this.version = Objects.requireNonNull(version, "version");
            return this;
        }

        public Builder mainClass(final String mainClass) {
            this.mainClass = Objects.requireNonNull(mainClass, "main class");
            return this;
        }

        public Builder description(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public Builder links(final PluginLinks links) {
            this.links = Objects.requireNonNull(links, "links");
            return this;
        }

        public Builder contributors(final List<PluginContributor> contributors) {
            this.contributors.addAll(Objects.requireNonNull(contributors, "contributors"));
            return this;
        }

        public Builder addContributor(final PluginContributor contributor) {
            this.contributors.add(Objects.requireNonNull(contributor, "contributor"));
            return this;
        }

        public Builder dependencies(final List<PluginDependency> dependencies) {
            this.dependencies.addAll(Objects.requireNonNull(dependencies, "dependencies"));
            return this;
        }

        public Builder addDependency(final PluginDependency dependency) {
            this.dependencies.add(Objects.requireNonNull(dependency, "dependency"));
            return this;
        }

        public Builder extraMetadata(final Map<String, Object> extraMetadata) {
            this.extraMetadata.putAll(Objects.requireNonNull(extraMetadata, "extra metadata"));
            return this;
        }

        public Builder addExtraMetadata(final String key, final Object value) {
            this.extraMetadata.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return this;
        }

        public PluginMetadata build() {
            Objects.requireNonNull(this.loader, "loader");
            Objects.requireNonNull(this.id, "id");
            Objects.requireNonNull(this.version, "version");
            Objects.requireNonNull(this.mainClass, "main class");

            return new PluginMetadata(this);
        }
    }
}
