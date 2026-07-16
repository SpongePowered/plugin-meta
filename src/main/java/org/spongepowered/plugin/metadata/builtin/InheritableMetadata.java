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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginConflict;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class InheritableMetadata {
    private static final InheritableMetadata NONE = new InheritableMetadata();

    private final @Nullable ArtifactVersion version;
    private final @Nullable PluginLoaderSpecification loader;
    private final @Nullable String name;
    private final @Nullable String description;
    private final @Nullable String license;
    private final PluginBranding branding;
    private final PluginLinks links;
    private final List<PluginContributor> contributors;
    private final List<PluginConflict> conflicts;
    private final Map<String, PluginDependency> dependencies;
    private final Map<String, Object> properties;

    private InheritableMetadata() {
        this.version = null;
        this.loader = null;
        this.name = null;
        this.description = null;
        this.license = null;
        this.branding = PluginBranding.none();
        this.links = PluginLinks.none();
        this.contributors = List.of();
        this.conflicts = List.of();
        this.dependencies = Map.of();
        this.properties = Map.of();
    }

    private InheritableMetadata(final Builder builder) {
        this.version = builder.version;
        this.loader = builder.loader;
        this.name = builder.name;
        this.description = builder.description;
        this.license = builder.license;
        this.branding = builder.branding;
        this.links = builder.links;
        this.contributors = List.copyOf(builder.contributors);
        this.conflicts = List.copyOf(builder.conflicts);
        this.dependencies = Collections.unmodifiableMap(new LinkedHashMap<>(builder.dependencies));
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(builder.properties));
    }

    public Optional<ArtifactVersion> version() {
        return Optional.ofNullable(this.version);
    }

    public Optional<PluginLoaderSpecification> loader() {
        return Optional.ofNullable(this.loader);
    }

    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    public Optional<String> license() {
        return Optional.ofNullable(this.license);
    }

    public PluginBranding branding() {
        return this.branding;
    }

    public PluginLinks links() {
        return this.links;
    }

    public List<PluginContributor> contributors() {
        return this.contributors;
    }

    public List<PluginConflict> conflicts() {
        return this.conflicts;
    }

    public Map<String, PluginDependency> dependencies() {
        return this.dependencies;
    }

    public Map<String, Object> properties() {
        return this.properties;
    }

    public InheritableMetadata with(final InheritableMetadata override) {
        return this.toBuilder().with(override).build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof InheritableMetadata other)) {
            return false;
        }

        return Objects.equals(this.version, other.version)
                && Objects.equals(this.loader, other.loader)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.license, other.license)
                && this.branding.equals(other.branding)
                && this.links.equals(other.links)
                && this.contributors.equals(other.contributors)
                && this.conflicts.equals(other.conflicts)
                && this.dependencies.equals(other.dependencies)
                && this.properties.equals(other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.version, this.loader, this.name, this.description, this.license,
                this.branding, this.links, this.contributors, this.conflicts, this.dependencies, this.properties);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", InheritableMetadata.class.getSimpleName() + "[", "]")
                .add("version=" + this.version)
                .add("loader=" + this.loader)
                .add("license=" + this.license)
                .add("branding=" + this.branding)
                .add("links=" + this.links)
                .add("contributors=" + this.contributors)
                .add("conflicts=" + this.conflicts)
                .add("dependencies=" + this.dependencies)
                .add("properties=" + this.properties)
                .toString();
    }

    public InheritableMetadata.Builder toBuilder() {
        return new InheritableMetadata.Builder().from(this);
    }

    public static InheritableMetadata.Builder builder() {
        return new InheritableMetadata.Builder();
    }

    public static InheritableMetadata none() {
        return InheritableMetadata.NONE;
    }

    public static final class Builder {
        private @Nullable ArtifactVersion version;
        private @Nullable PluginLoaderSpecification loader;
        private @Nullable String name;
        private @Nullable String description;
        private @Nullable String license;
        private PluginBranding branding = PluginBranding.none();
        private PluginLinks links = PluginLinks.none();
        private final List<PluginContributor> contributors = new LinkedList<>();
        private final List<PluginConflict> conflicts = new LinkedList<>();
        private final Map<String, PluginDependency> dependencies = new LinkedHashMap<>();
        private final Map<String, Object> properties = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder version(final @Nullable ArtifactVersion version) {
            this.version = version;
            return this;
        }

        public Builder loader(final @Nullable PluginLoaderSpecification loader) {
            this.loader = loader;
            return this;
        }

        public Builder name(final @Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder description(final @Nullable String description) {
            this.description = description;
            return this;
        }

        public Builder license(final @Nullable String license) {
            this.license = license;
            return this;
        }

        public Builder branding(final PluginBranding branding) {
            this.branding = Objects.requireNonNull(branding, "branding");
            return this;
        }

        public Builder links(final PluginLinks links) {
            this.links = Objects.requireNonNull(links, "links");
            return this;
        }

        public Builder contributors(final Collection<PluginContributor> contributors) {
            Objects.requireNonNull(contributors, "contributors");
            this.contributors.clear();
            this.contributors.addAll(contributors);
            return this;
        }

        public Builder addContributors(final Collection<PluginContributor> contributors) {
            this.contributors.addAll(Objects.requireNonNull(contributors, "contributors"));
            return this;
        }

        public Builder addContributor(final PluginContributor contributor) {
            this.contributors.add(Objects.requireNonNull(contributor, "contributor"));
            return this;
        }

        public Builder conflicts(final Collection<PluginConflict> conflicts) {
            Objects.requireNonNull(conflicts, "conflicts");
            this.conflicts.clear();
            this.conflicts.addAll(conflicts);
            return this;
        }

        public Builder addConflicts(final Collection<PluginConflict> conflicts) {
            this.conflicts.addAll(Objects.requireNonNull(conflicts, "conflicts"));
            return this;
        }

        public Builder addConflict(final PluginConflict conflict) {
            this.conflicts.add(Objects.requireNonNull(conflict, "conflict"));
            return this;
        }

        public Builder dependencies(final Collection<PluginDependency> dependencies) {
            Objects.requireNonNull(dependencies, "dependencies");
            this.dependencies.clear();
            return this.addDependencies(dependencies);
        }

        public Builder addDependencies(final Collection<PluginDependency> dependencies) {
            for (final PluginDependency element : Objects.requireNonNull(dependencies, "dependencies")) {
                this.dependencies.put(Objects.requireNonNull(element, "element").id(), element);
            }
            return this;
        }

        public Builder addDependency(final PluginDependency dependency) {
            this.dependencies.put(Objects.requireNonNull(dependency, "dependency").id(), dependency);
            return this;
        }

        public Builder properties(final Map<String, Object> properties) {
            Objects.requireNonNull(properties, "properties");
            this.properties.clear();
            this.properties.putAll(properties);
            return this;
        }

        public Builder addProperties(final Map<String, Object> properties) {
            this.properties.putAll(Objects.requireNonNull(properties, "properties"));
            return this;
        }

        public Builder addProperty(final String key, final Object value) {
            this.properties.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return this;
        }

        public Builder from(final InheritableMetadata value) {
            Objects.requireNonNull(value, "value");
            this.version = value.version;
            this.loader = value.loader;
            this.name = value.name;
            this.description = value.description;
            this.license = value.license;
            this.branding = value.branding;
            this.links = value.links;
            this.contributors.clear();
            this.contributors.addAll(value.contributors);
            this.conflicts.clear();
            this.conflicts.addAll(value.conflicts);
            this.dependencies.clear();
            this.dependencies.putAll(value.dependencies);
            this.properties.clear();
            this.properties.putAll(value.properties);
            return this;
        }

        public Builder with(final InheritableMetadata override) {
            if (override.version != null) {
                this.version = override.version;
            }
            if (override.loader != null) {
                this.loader = override.loader;
            }
            if (override.name != null) {
                this.name = override.name;
            }
            if (override.description != null) {
                this.description = override.description;
            }
            if (override.license != null) {
                this.license = override.license;
            }
            if (!override.branding.equals(PluginBranding.none())) {
                this.branding = override.branding;
            }
            if (!override.links.equals(PluginLinks.none())) {
                this.links = override.links;
            }
            this.contributors.addAll(override.contributors);
            this.conflicts.addAll(override.conflicts);
            this.dependencies.putAll(override.dependencies);
            this.properties.putAll(override.properties);
            return this;
        }

        public InheritableMetadata build() {
            return new InheritableMetadata(this);
        }
    }
}
