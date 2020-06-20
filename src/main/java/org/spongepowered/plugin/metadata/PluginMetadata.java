/*
 * This file is part of plugin-spi, licensed under the MIT License (MIT).
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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PluginMetadata {

    private final String id, name, version, mainClass, description;
    private final URL homepage, source, issues;
    private final List<PluginContributor> contributors;
    private final List<PluginDependency> dependencies;
    private final Map<String, Object> extraMetadata;

    private PluginMetadata(final Builder builder) {
        Preconditions.checkNotNull(builder);

        this.id = builder.id;
        this.name = builder.name;
        this.version = builder.version;
        this.mainClass = builder.mainClass;
        this.description = builder.description;
        this.homepage = builder.homepage;
        this.source = builder.source;
        this.issues = builder.issues;
        this.contributors = builder.contributors;
        this.dependencies = builder.dependencies;
        this.extraMetadata = builder.extraMetadata;
    }

    public static Builder builder() {
        return new Builder();
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

    public Optional<URL> getHomepage() {
        return Optional.ofNullable(this.homepage);
    }

    public Optional<URL> getSource() {
        return Optional.ofNullable(this.source);
    }

    public Optional<URL> getIssues() {
        return Optional.ofNullable(this.issues);
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
        return MoreObjects.toStringHelper(this)
            .add("id", this.id)
            .add("name", this.name)
            .add("version", this.version)
            .add("mainClass", this.mainClass)
            .add("description", this.description)
            .add("homepage", this.homepage)
            .add("source", this.source)
            .add("issues", this.issues)
            .add("contributors", this.contributors)
            .add("dependencies", this.dependencies)
            .add("extraMetadata", this.extraMetadata)
            .toString();
    }

    public static final class Builder {

        String id, name, version, mainClass, description;
        URL homepage, source, issues;
        List<PluginContributor> contributors = new ArrayList<>();
        List<PluginDependency> dependencies = new ArrayList<>();
        Map<String, Object> extraMetadata = new HashMap<>();

        public Builder setId(final String id) {
            this.id = Preconditions.checkNotNull(id);
            return this;
        }

        public Builder setName(@Nullable final String name) {
            this.name = name;
            return this;
        }

        public Builder setVersion(final String version) {
            this.version = Preconditions.checkNotNull(version);
            return this;
        }

        public Builder setMainClass(final String mainClass) {
            this.mainClass = Preconditions.checkNotNull(mainClass);
            return this;
        }

        public Builder setDescription(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public Builder setHomepage(@Nullable final URL homepage) {
            this.homepage = homepage;
            return this;
        }

        public Builder setSource(@Nullable final URL source) {
            this.source = source;
            return this;
        }

        public Builder setIssues(@Nullable final URL issues) {
            this.issues = issues;
            return this;
        }

        public Builder setContributors(final List<PluginContributor> contributors) {
            this.contributors = Preconditions.checkNotNull(contributors);
            return this;
        }

        public Builder contributor(final PluginContributor developer) {
            this.contributors.add(Preconditions.checkNotNull(developer));
            return this;
        }

        public Builder setDependencies(final List<PluginDependency> dependencies) {
            this.dependencies = Preconditions.checkNotNull(dependencies);
            return this;
        }

        public Builder dependency(final PluginDependency dependency) {
            Preconditions.checkNotNull(dependency);
            this.dependencies.add(dependency);
            return this;
        }

        public Builder setExtraMetadata(final Map<String, Object> extraMetadata) {
            this.extraMetadata = Preconditions.checkNotNull(extraMetadata);
            return this;
        }

        public Builder extraMetadata(final String key, final Object value) {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(value);

            this.extraMetadata.put(key, value);
            return this;
        }

        public PluginMetadata build() {
            Preconditions.checkNotNull(this.id);
            Preconditions.checkNotNull(this.version);
            Preconditions.checkNotNull(this.mainClass);

            return new PluginMetadata(this);
        }
    }
}
