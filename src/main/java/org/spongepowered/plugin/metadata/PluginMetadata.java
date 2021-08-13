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

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.util.AdapterUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    private final PluginBranding branding;
    private final List<PluginContributor> contributors = new LinkedList<>();
    private final List<PluginDependency> dependencies = new LinkedList<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    private PluginMetadata(final Builder builder) {
        this.loader = builder.loader;
        this.id = builder.id;
        this.name = builder.name;
        this.version = builder.version;
        this.mainClass = builder.mainClass;
        this.description = builder.description;
        this.links = builder.links;
        this.branding = builder.branding;
        this.contributors.addAll(builder.contributors);
        this.dependencies.addAll(builder.dependencies);
        this.properties.putAll(builder.properties);
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

    public PluginBranding branding() {
        return this.branding;
    }

    public List<PluginContributor> contributors() {
        return this.contributors;
    }

    public List<PluginDependency> dependencies() {
        return this.dependencies;
    }

    public Map<String, Object> properties() {
        return this.properties;
    }

    public PluginMetadata.Builder toBuilder() {
        final Builder builder = PluginMetadata.builder();
        builder.loader = this.loader;
        builder.id = this.id;
        builder.name = this.name;
        builder.version = this.version;
        builder.mainClass = this.mainClass;
        builder.description = this.description;
        builder.links = this.links.toBuilder().build();
        builder.branding = this.branding.toBuilder().build();
        for (final PluginContributor contributor : this.contributors) {
            builder.contributors.add(contributor.toBuilder().build());
        }
        for (final PluginDependency dependency : this.dependencies) {
            builder.dependencies.add(dependency.toBuilder().build());
        }
        // TODO Copy the properties objects somehow?
        builder.properties.putAll(this.properties);
        return builder;
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
                .add("branding=" + this.branding)
                .add("contributors=" + this.contributors)
                .add("dependencies=" + this.dependencies)
                .add("properties=" + this.properties)
                .toString();
    }

    public static final class Builder {

        @Nullable String loader, id, name, version, mainClass, description;
        PluginLinks links = PluginLinks.none();
        PluginBranding branding = PluginBranding.none();
        final List<PluginContributor> contributors = new LinkedList<>();
        final List<PluginDependency> dependencies = new LinkedList<>();
        final Map<String, Object> properties = new LinkedHashMap<>();

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

        public Builder branding(final PluginBranding branding) {
            this.branding = Objects.requireNonNull(branding, "branding");
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

        public Builder properties(final Map<String, Object> properties) {
            this.properties.putAll(Objects.requireNonNull(properties, "properties"));
            return this;
        }

        public Builder property(final String key, final Object value) {
            this.properties.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
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

    public static final class Adapter extends TypeAdapter<PluginMetadata> {

        private static final Adapter INSTANCE = new Adapter(PluginContributor.Adapter.instance(), PluginDependency.Adapter
                .instance(), PluginLinks.Adapter.instance(), PluginBranding.Adapter.instance());

        public static Adapter instance() {
            return Adapter.INSTANCE;
        }

        private final TypeAdapter<PluginContributor> contributorAdapter;
        private final TypeAdapter<PluginDependency> dependencyAdapter;
        private final TypeAdapter<PluginLinks> linksAdapter;
        private final TypeAdapter<PluginBranding> brandingAdapter;

        public Adapter(final TypeAdapter<PluginContributor> contributorAdapter, final TypeAdapter<PluginDependency> dependencyAdapter,
                final TypeAdapter<PluginLinks> linksAdapter, final TypeAdapter<PluginBranding> brandingAdapter) {

            this.contributorAdapter = contributorAdapter;
            this.dependencyAdapter = dependencyAdapter;
            this.linksAdapter = linksAdapter;
            this.brandingAdapter = brandingAdapter;
        }

        @Override
        public void write(final JsonWriter out, final PluginMetadata value) throws IOException {
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(value, "value");

            out.beginObject();
            out.name("loader").value(value.loader());
            out.name("id").value(value.id());
            AdapterUtils.writeStringIfPresent(out, "name", value.name());
            out.name("version").value(value.version());
            out.name("main-class").value(value.mainClass());
            AdapterUtils.writeStringIfPresent(out, "description", value.description());
            this.linksAdapter.write(out.name("links"), value.links());
            this.brandingAdapter.write(out.name("branding"), value.branding);
            this.writeContributors(out.name("contributors"), value.contributors());
            this.writeDependencies(out.name("dependencies"), value.dependencies());
            this.writeProperties(out.name("properties"), value.properties());
            out.endObject();
        }

        @Override
        public PluginMetadata read(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            in.beginObject();
            final Set<String> processedKeys = new HashSet<>();
            final PluginMetadata.Builder builder = PluginMetadata.builder();
            while (in.hasNext()) {
                final String key = in.nextName();
                if (!processedKeys.add(key)) {
                    throw new JsonParseException(String.format("Duplicate key '%s' in %s", key, in));
                }

                switch (key) {
                    case "loader":
                        builder.loader(in.nextString());
                        break;
                    case "id":
                        builder.id(in.nextString());
                        break;
                    case "name":
                        builder.name(in.nextString());
                        break;
                    case "version":
                        builder.version(in.nextString());
                        break;
                    case "main-class":
                        builder.mainClass(in.nextString());
                        break;
                    case "description":
                        builder.description(in.nextString());
                        break;
                    case "links":
                        builder.links(this.linksAdapter.read(in));
                        break;
                    case "branding":
                        builder.branding(this.brandingAdapter.read(in));
                        break;
                    case "contributors":
                        builder.contributors(this.readContributors(in));
                        break;
                    case "dependencies":
                        builder.dependencies(this.readDependencies(in));
                        break;
                    case "properties":
                        builder.properties(this.readProperties(in));
                        break;
                }
            }
            in.endObject();
            return builder.build();
        }

        public List<PluginContributor> readContributors(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            final List<PluginContributor> contributors = new ArrayList<>();

            in.beginArray();
            while (in.hasNext()) {
                contributors.add(this.contributorAdapter.read(in));
            }
            in.endArray();

            return Collections.unmodifiableList(contributors);
        }

        private List<PluginDependency> readDependencies(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            final List<PluginDependency> dependencies = new ArrayList<>();

            in.beginArray();
            while (in.hasNext()) {
                dependencies.add(this.dependencyAdapter.read(in));
            }
            in.endArray();

            return Collections.unmodifiableList(dependencies);
        }

        public Map<String, Object> readProperties(final JsonReader in) throws IOException {
            Objects.requireNonNull(in, "in");

            in.beginObject();
            final Map<String, Object> properties = new HashMap<>();
            while (in.hasNext()) {
                properties.put(in.nextName(), in.nextString());
            }
            in.endObject();
            return Collections.unmodifiableMap(properties);
        }

        public void writeContributors(final JsonWriter out, final List<PluginContributor> contributors) throws IOException {
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(contributors, "contributors");

            out.beginArray();
            for (final PluginContributor contributor : contributors) {
                this.contributorAdapter.write(out.name(contributor.name()), contributor);
            }
            out.endArray();
        }

        public void writeDependencies(final JsonWriter out, final List<PluginDependency> dependencies) throws IOException {
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(dependencies, "dependencies");

            out.beginArray();
            for (final PluginDependency dependency : dependencies) {
                this.dependencyAdapter.write(out.name(dependency.id()), dependency);
            }
            out.endArray();
        }

        public void writeProperties(final JsonWriter out, final Map<String, Object> properties) throws IOException {
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(properties, "properties");

            out.beginObject();
            for (final Map.Entry<String, Object> entry : properties.entrySet()) {
                // TODO Allow for collections/maps in the properties map?
                out.name(entry.getKey()).value(entry.getValue().toString());
            }
            out.endObject();
        }
    }
}
