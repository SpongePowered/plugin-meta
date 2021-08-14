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
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class StandardInheritable implements Inheritable {

    private final ArtifactVersion version;
    private final PluginBranding branding;
    private final PluginLinks links;
    private final List<PluginContributor> contributors = new LinkedList<>();
    private final Map<String, PluginDependency> dependenciesById = new LinkedHashMap<>();
    private final List<PluginDependency> dependencies = new LinkedList<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    protected StandardInheritable(final Builder builder) {
        this.version = builder.version;
        this.branding = builder.branding;
        this.links = builder.links;
        this.contributors.addAll(builder.contributors);
        this.dependenciesById.putAll(builder.dependenciesById);
        this.dependencies.addAll(builder.dependencies);
        this.properties.putAll(builder.properties);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public ArtifactVersion version() {
        return this.version;
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
        return Collections.unmodifiableList(this.contributors);
    }

    @Override
    public Optional<PluginDependency> dependency(final String id) {
        return Optional.ofNullable(this.dependenciesById.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public List<PluginDependency> dependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }

    @Override
    public Optional<Object> property(final String key) {
        return Optional.ofNullable(this.properties.get(Objects.requireNonNull(key, "key")));
    }

    @Override
    public Map<String, Object> properties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public String toString() {
        return this.stringJoiner().toString();
    }

    protected StringJoiner stringJoiner() {
        return new StringJoiner(", ", StandardInheritable.class.getSimpleName() + "[", "]")
                .add("version=" + this.version)
                .add("branding=" + this.branding)
                .add("links=" + this.links)
                .add("contributors=" + this.contributors)
                .add("dependencies=" + this.dependencies)
                .add("properties=" + this.properties)
                ;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Inheritable, B extends Builder<T, B>> {

        @Nullable ArtifactVersion version;
        PluginBranding branding = PluginBranding.none();
        PluginLinks links = PluginLinks.none();
        final List<PluginContributor> contributors = new LinkedList<>();
        final Map<String, PluginDependency> dependenciesById = new LinkedHashMap<>();
        final List<PluginDependency> dependencies = new LinkedList<>();
        final Map<String, Object> properties = new LinkedHashMap<>();

        protected Builder() {
        }

        public B version(final String version) {
            this.version = new DefaultArtifactVersion(Objects.requireNonNull(version, "version"));
            return (B) this;
        }

        public B branding(final PluginBranding branding) {
            this.branding = Objects.requireNonNull(branding, "branding");
            return (B) this;
        }

        public B links(final PluginLinks links) {
            this.links = Objects.requireNonNull(links, "links");
            return (B) this;
        }

        public B contributors(final List<PluginContributor> contributors) {
            this.contributors.addAll(Objects.requireNonNull(contributors, "contributors"));
            return (B) this;
        }

        public B addContributor(final PluginContributor contributor) {
            this.contributors.add(Objects.requireNonNull(contributor, "contributor"));
            return (B) this;
        }

        public B dependencies(final List<PluginDependency> dependencies) {
            for (final PluginDependency dependency : Objects.requireNonNull(dependencies, "dependencies")) {
                this.dependenciesById.put(dependency.id(), dependency);
            }
            this.dependencies.addAll(dependencies);
            return (B) this;
        }

        public B addDependency(final PluginDependency dependency) {
            this.dependenciesById.put(Objects.requireNonNull(dependency, "dependency").id(), dependency);
            this.dependencies.remove(dependency);
            this.dependencies.add(dependency);
            return (B) this;
        }

        public B properties(final Map<String, Object> properties) {
            this.properties.putAll(Objects.requireNonNull(properties, "properties"));
            return (B) this;
        }

        public B property(final String key, final Object value) {
            this.properties.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return (B) this;
        }

        public final T build() {
            Objects.requireNonNull(this.version, "version");

            return this.build0();
        }

        protected T build0() {
            return (T) new StandardInheritable(this);
        }
    }

    private static final class BuilderImpl extends Builder<StandardInheritable, BuilderImpl> {

    }
}
