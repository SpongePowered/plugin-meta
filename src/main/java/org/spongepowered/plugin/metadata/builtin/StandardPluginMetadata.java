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
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Constants;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginConflict;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginEntrypoints;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class StandardPluginMetadata implements PluginMetadata {
    private final String id;
    private final PluginEntrypoints entrypoints;
    private final InheritableMetadata global, override;

    private final ArtifactVersion version;
    private final PluginLoaderSpecification loader;
    private final @Nullable String name;
    private final @Nullable String description;
    private final @Nullable String license;
    private final PluginBranding branding;
    private final PluginLinks links;
    private final List<PluginContributor> contributors;
    private final List<PluginConflict> conflicts;
    private final Map<String, PluginDependency> dependencies;
    private final Map<String, Object> properties;

    private StandardPluginMetadata(final Builder builder) {
        this.id = builder.id;
        this.entrypoints = builder.entrypoints;
        this.global = builder.global;
        this.override = builder.override;
        InheritableMetadata metadata = this.global.with(this.override);
        this.version = metadata.version().orElseThrow(() -> new NoSuchElementException("version"));
        this.loader = metadata.loader().orElseThrow(() -> new NoSuchElementException("loader"));
        this.name = metadata.name().orElse(null);
        this.description = metadata.description().orElse(null);
        this.license = metadata.license().orElse(null);
        this.branding = metadata.branding();
        this.links = metadata.links();
        this.contributors = metadata.contributors();
        this.conflicts = metadata.conflicts();
        this.dependencies = metadata.dependencies();
        this.properties = metadata.properties();
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public PluginEntrypoints entrypoints() {
        return this.entrypoints;
    }

    public InheritableMetadata global() {
        return this.global;
    }

    public InheritableMetadata override() {
        return this.override;
    }

    @Override
    public ArtifactVersion version() {
        return this.version;
    }

    @Override
    public PluginLoaderSpecification loader() {
        return this.loader;
    }

    @Override
    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Optional<String> license() {
        return Optional.ofNullable(this.license);
    }

    @Override
    public PluginBranding branding() {
        return this.branding;
    }

    @Override
    public PluginLinks links() {
        return this.links;
    }

    @Override
    public List<PluginContributor> contributors() {
        return this.contributors;
    }

    @Override
    public List<PluginConflict> conflicts() {
        return this.conflicts;
    }

    @Override
    public Optional<PluginDependency> dependency(String id) {
        return Optional.ofNullable(this.dependencies.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public Collection<PluginDependency> dependencies() {
        return this.dependencies.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> property(final String key) {
        return Optional.ofNullable((T) this.properties.get(Objects.requireNonNull(key, "key")));
    }

    @Override
    public Map<String, Object> properties() {
        return this.properties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.entrypoints, this.global, this.override);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof StandardPluginMetadata other)) {
            return false;
        }

        return this.id.equals(other.id) && this.entrypoints.equals(other.entrypoints)
                && this.global.equals(other.global) && this.override.equals(other.override);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StandardPluginMetadata.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("entrypoints=" + this.entrypoints)
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

    public StandardPluginMetadata.Builder toBuilder() {
        return new StandardPluginMetadata.Builder().from(this);
    }

    public static StandardPluginMetadata.Builder builder() {
        return new StandardPluginMetadata.Builder();
    }

    public static final class Builder {

        private @MonotonicNonNull String id;
        private PluginEntrypoints entrypoints = PluginEntrypoints.none();
        private InheritableMetadata global = InheritableMetadata.none(), override = InheritableMetadata.none();

        private Builder() {}

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder entrypoints(final PluginEntrypoints entrypoints) {
            this.entrypoints = Objects.requireNonNull(entrypoints, "entrypoints");
            return this;
        }

        public Builder global(final InheritableMetadata global) {
            this.global = Objects.requireNonNull(global, "global");
            return this;
        }

        public Builder override(final InheritableMetadata override) {
            this.override = Objects.requireNonNull(override, "override");
            return this;
        }

        public Builder from(final StandardPluginMetadata value) {
            this.id = value.id;
            this.entrypoints = value.entrypoints;
            this.global = value.global;
            this.override = value.override;
            return this;
        }

        public StandardPluginMetadata build() {
            if (!Constants.VALID_ID_PATTERN.matcher(Objects.requireNonNull(this.id, "id")).matches()) {
                throw new IllegalStateException(String.format("PluginMetadata with supplied ID '{%s}' is invalid. %s", this.id,
                        Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
            }
            return new StandardPluginMetadata(this);
        }
    }
}
