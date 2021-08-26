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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.model.Adapters;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class StandardInheritable implements Inheritable {

    protected final ArtifactVersion version;
    protected final String rawVersion;
    protected final PluginBranding branding;
    protected final PluginLinks links;
    protected final List<PluginContributor> contributors = new LinkedList<>();
    protected final Set<PluginDependency> dependencies = new LinkedHashSet<>();
    protected final Map<String, Object> properties = new LinkedHashMap<>();
    private final Map<String, PluginDependency> dependenciesById = new LinkedHashMap<>();

    protected StandardInheritable(final AbstractBuilder builder) {
        this.version = builder.version;
        this.rawVersion = builder.rawVersion;
        this.branding = builder.branding;
        this.links = builder.links;
        this.contributors.addAll(builder.contributors);
        this.dependencies.addAll(builder.dependencies);
        for (final PluginDependency dependency : this.dependencies) {
            this.dependenciesById.put(dependency.id(), dependency);
        }
        this.properties.putAll(builder.properties);
    }

    public static Builder builder() {
        return new Builder();
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
    public Set<PluginDependency> dependencies() {
        return Collections.unmodifiableSet(this.dependencies);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> property(final String key) {
        return Optional.ofNullable((T) this.properties.get(Objects.requireNonNull(key, "key")));
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
                .add("version=" + this.rawVersion)
                .add("branding=" + this.branding)
                .add("links=" + this.links)
                .add("contributors=" + this.contributors)
                .add("dependencies=" + this.dependencies)
                .add("properties=" + this.properties)
        ;
    }

    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<T extends Inheritable, B extends AbstractBuilder<T, B>> {

        final List<PluginContributor> contributors = new LinkedList<>();
        final Set<PluginDependency> dependencies = new LinkedHashSet<>();
        final Map<String, Object> properties = new LinkedHashMap<>();
        ArtifactVersion version = NullVersion.instance();
        String rawVersion = "null";
        PluginBranding branding = PluginBranding.none();
        PluginLinks links = PluginLinks.none();

        protected AbstractBuilder() {
        }

        public B version(final @Nullable String version) {
            if (version == null) {
                this.version = NullVersion.instance();
                this.rawVersion = "null";
            } else {
                this.version = new DefaultArtifactVersion(Objects.requireNonNull(version, "version"));
                this.rawVersion = version;
            }
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
            this.contributors.addAll(Objects.requireNonNull(contributors, "contributors"));
            return (B) this;
        }

        public B addContributor(final PluginContributor contributor) {
            this.contributors.add(Objects.requireNonNull(contributor, "contributor"));
            return (B) this;
        }

        public B dependencies(final Collection<PluginDependency> dependencies) {
            this.dependencies.addAll(Objects.requireNonNull(dependencies, "dependencies"));
            return (B) this;
        }

        public B addDependency(final PluginDependency dependency) {
            this.dependencies.add(Objects.requireNonNull(dependency, "dependency"));
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

        public B from(final StandardInheritable value) {
            // TODO If we have "partial" entries, do we want to do deep merging? May start to get out of control...

            // Inheritable
            if (this.version == NullVersion.instance()) {
                this.version = value.version();
                this.rawVersion = value.rawVersion;
            }
            if (this.branding == PluginBranding.none()) {
                this.branding = value.branding();
            }
            if (this.links == PluginLinks.none()) {
                this.links = value.links();
            }
            this.contributors.addAll(value.contributors());
            this.dependencies.addAll(value.dependencies());
            for (final Map.Entry<String, Object> entry : value.properties().entrySet()) {
                this.properties.putIfAbsent(entry.getKey(), entry.getValue());
            }
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

    public static final class Serializer implements JsonSerializer<StandardInheritable>, JsonDeserializer<StandardInheritable> {

        @Override
        public StandardInheritable deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {

            final JsonObject obj = element.getAsJsonObject();
            final Builder builder = StandardInheritable.builder().version(GsonUtils.<String>get(obj, "version", JsonElement::getAsString).orElse(null));
            GsonUtils.consumeIfPresent(obj, "branding", e -> builder.branding(Adapters.Deserializers.PLUGIN_BRANDING.fromJsonTree(e).build()));
            GsonUtils.consumeIfPresent(obj, "links", e -> builder.links(Adapters.Deserializers.PLUGIN_LINKS.fromJsonTree(e).build()));
            GsonUtils.consumeIfPresent(obj, "contributors", e -> builder.contributors(GsonUtils.read((JsonArray) e, Adapters.Deserializers.PLUGIN_CONTRIBUTOR, LinkedList::new).stream().map(PluginContributor.Builder::build).collect(Collectors.toList())));
            GsonUtils.consumeIfPresent(obj, "dependencies", e -> builder.dependencies(GsonUtils.read((JsonArray) e, Adapters.Deserializers.PLUGIN_DEPENDENCY, LinkedHashSet::new).stream().map(PluginDependency.Builder::build).collect(Collectors.toList())));
            GsonUtils.consumeIfPresent(obj, "properties", e -> builder.properties(GsonUtils.read((JsonObject) e, JsonElement::getAsString, LinkedHashMap::new)));

            return builder.build();
        }

        @Override
        public JsonElement serialize(final StandardInheritable value, final Type type, final JsonSerializationContext context) {
            final JsonObject obj = new JsonObject();
            GsonUtils.applyIfValid(obj, value, p -> p.version != NullVersion.instance(), (o, v) -> o.addProperty("version", v.rawVersion));
            GsonUtils.applyIfValid(obj, value, p -> p.branding != PluginBranding.none(), (o, v) -> o.add("branding", Adapters.Serializers.PLUGIN_BRANDING.toJsonTree(v.branding)));
            GsonUtils.applyIfValid(obj, value, p -> p.links != PluginLinks.none(), (o, v) -> o.add("links", Adapters.Serializers.PLUGIN_LINKS.toJsonTree(v.links)));
            GsonUtils.applyIfValid(obj, value, p -> !p.contributors.isEmpty(), (o, v) -> o.add("contributors", GsonUtils.write(Adapters.Serializers.PLUGIN_CONTRIBUTOR, v.contributors)));
            GsonUtils.applyIfValid(obj, value, p -> !p.dependencies.isEmpty(), (o, v) -> o.add("dependencies", GsonUtils.write(Adapters.Serializers.PLUGIN_DEPENDENCY, v.dependencies)));
            GsonUtils.applyIfValid(obj, value, p -> !p.properties.isEmpty(), (o, v) -> o.add("properties", GsonUtils.write(e -> new JsonPrimitive(e.toString()), v.properties)));
            return obj;
        }
    }
}
