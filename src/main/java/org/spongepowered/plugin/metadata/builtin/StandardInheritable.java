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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class StandardInheritable implements Inheritable {

    protected final ArtifactVersion version;
    protected final PluginBranding branding;
    protected final PluginLinks links;
    protected final List<PluginContributor> contributors;
    protected final Map<String, PluginDependency> dependencies;
    protected final Map<String, Object> properties;

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected StandardInheritable(final AbstractBuilder builder) {
        this.version = builder.version;
        this.branding = builder.branding;
        this.links = builder.links;
        this.contributors = List.copyOf(builder.contributors);
        this.dependencies = Map.copyOf(builder.dependencies);
        this.properties = Map.copyOf(builder.properties);
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
        return this.contributors;
    }

    @Override
    public Optional<PluginDependency> dependency(final String id) {
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
    public abstract static class AbstractBuilder<T extends StandardInheritable, B extends AbstractBuilder<T, B>> {

        protected final List<PluginContributor> contributors = new LinkedList<>();
        protected final Map<String, PluginDependency> dependencies = new LinkedHashMap<>();
        protected final Map<String, Object> properties = new LinkedHashMap<>();
        protected ArtifactVersion version = NullVersion.instance();
        protected PluginBranding branding = PluginBranding.none();
        protected PluginLinks links = PluginLinks.none();

        protected AbstractBuilder() {
        }

        public B version(final @Nullable String version) {
            this.version = version == null ? NullVersion.instance() : new DefaultArtifactVersion(version);
            return (B) this;
        }

        public B version(final @Nullable ArtifactVersion version) {
            this.version = version == null ? NullVersion.instance() : version;
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

        public B contributors(final Collection<PluginContributor> contributors) {
            Objects.requireNonNull(contributors, "contributors");
            this.contributors.clear();
            this.contributors.addAll(contributors);
            return (B) this;
        }

        public B addContributors(final Collection<PluginContributor> contributors) {
            this.contributors.addAll(Objects.requireNonNull(contributors, "contributors"));
            return (B) this;
        }

        public B addContributor(final PluginContributor contributor) {
            this.contributors.add(Objects.requireNonNull(contributor, "contributor"));
            return (B) this;
        }

        public B dependencies(final Collection<PluginDependency> dependencies) {
            Objects.requireNonNull(dependencies, "dependencies");
            this.dependencies.clear();
            return this.addDependencies(dependencies);
        }

        public B addDependencies(final Collection<PluginDependency> dependencies) {
            for (final PluginDependency element : Objects.requireNonNull(dependencies, "dependencies")) {
                this.dependencies.put(Objects.requireNonNull(element, "element").id(), element);
            }
            return (B) this;
        }

        public B addDependency(final PluginDependency dependency) {
            this.dependencies.put(Objects.requireNonNull(dependency, "dependency").id(), dependency);
            return (B) this;
        }

        public B properties(final Map<String, Object> properties) {
            Objects.requireNonNull(properties, "properties");
            this.properties.clear();
            this.properties.putAll(properties);
            return (B) this;
        }

        public B addProperties(final Map<String, Object> properties) {
            this.properties.putAll(Objects.requireNonNull(properties, "properties"));
            return (B) this;
        }

        public B addProperty(final String key, final Object value) {
            this.properties.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return (B) this;
        }

        public B merge(final StandardInheritable value) {
            Objects.requireNonNull(value, "value");
            if (this.version == NullVersion.instance()) {
                this.version = value.version;
            }
            if (this.branding == PluginBranding.none()) {
                this.branding = value.branding;
            }
            if (this.links == PluginLinks.none()) {
                this.links = value.links;
            }
            if (this.contributors.isEmpty()) {
                this.contributors.addAll(value.contributors);
            }
            for (final Map.Entry<String, PluginDependency> entry : value.dependencies.entrySet()) {
                this.dependencies.putIfAbsent(entry.getKey(), entry.getValue());
            }
            for (final Map.Entry<String, Object> entry : value.properties.entrySet()) {
                this.properties.putIfAbsent(entry.getKey(), entry.getValue());
            }
            return (B) this;
        }

        public B from(final T value) {
            Objects.requireNonNull(value, "value");
            this.version = value.version;
            this.branding = value.branding;
            this.links = value.links;
            this.contributors.clear();
            this.contributors.addAll(value.contributors);
            this.dependencies.clear();
            this.dependencies.putAll(value.dependencies);
            this.properties.clear();
            this.properties.putAll(value.properties);
            return (B) this;
        }

        public final T build() {
            Objects.requireNonNull(this.version, "version");

            return this.build0();
        }

        protected abstract T build0();
    }

    public static final class Builder extends AbstractBuilder<StandardInheritable, Builder> {

        @Override
        protected StandardInheritable build0() {
            return new StandardInheritable(this);
        }
    }
}
