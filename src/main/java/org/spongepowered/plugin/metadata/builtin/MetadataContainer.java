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
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Container;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.builtin.adapter.Adapters;
import org.spongepowered.plugin.metadata.model.ContainerLoader;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class MetadataContainer implements Container {

    private final String license;
    private final ContainerLoader loader;
    @Nullable private final Inheritable globalMetadata;
    private final Map<String, StandardPluginMetadata> metadata;

    private MetadataContainer(final Builder builder) {
        this.loader = builder.loader;
        this.license = builder.license;
        this.globalMetadata = builder.globalMetadata;
        this.metadata = Map.copyOf(builder.metadata);

        for (final StandardPluginMetadata element : this.metadata.values()) {
            element.setContainer(this);
        }
    }

    @Override
    public ContainerLoader loader() {
        return this.loader;
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
        return Optional.ofNullable(this.metadata.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public Collection<StandardPluginMetadata> metadata() {
        return this.metadata.values();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetadataContainer.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("license=" + this.license)
                .add("globalMetadata=" + this.globalMetadata)
                .toString();
    }

    public MetadataContainer.Builder toBuilder() {
        return new MetadataContainer.Builder().from(this);
    }

    public static final class Builder {

        private final Map<String, StandardPluginMetadata> metadata = new LinkedHashMap<>();
        private @MonotonicNonNull String license;
        private @MonotonicNonNull ContainerLoader loader;
        private @Nullable Inheritable globalMetadata;

        public Builder loader(final ContainerLoader loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder globalMetadata(final @Nullable Inheritable globalMetadata) {
            this.globalMetadata = globalMetadata;
            return this;
        }

        public Builder metadata(final Collection<? extends StandardPluginMetadata> metadata) {
            Objects.requireNonNull(metadata, "metadata");
            this.metadata.clear();
            return this.addMetadata(metadata);
        }

        public Builder addMetadata(final Collection<? extends StandardPluginMetadata> metadata) {
            for (final StandardPluginMetadata element : Objects.requireNonNull(metadata, "metadata")) {
                this.metadata.put(Objects.requireNonNull(element, "element").id(), element);
            }
            return this;
        }

        public Builder addMetadata(final StandardPluginMetadata metadata) {
            this.metadata.put(Objects.requireNonNull(metadata, "metadata").id(), metadata);
            return this;
        }

        public Builder from(final MetadataContainer value) {
            Objects.requireNonNull(value, "value");
            this.loader = value.loader;
            this.license = value.license;
            this.globalMetadata = value.globalMetadata;
            this.metadata.clear();
            this.metadata.putAll(value.metadata);
            return this;
        }

        public MetadataContainer build() throws IllegalStateException, InvalidVersionSpecificationException {
            Objects.requireNonNull(this.license, "license");
            Objects.requireNonNull(this.loader, "loader");

            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A MetadataHolder must hold at least 1 PluginMetadata!");
            }

            return new MetadataContainer(this);
        }
    }

    public static final class Serializer implements JsonSerializer<MetadataContainer>, JsonDeserializer<MetadataContainer> {

        @Override
        public MetadataContainer deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject obj = element.getAsJsonObject();
            if (!obj.has("loader")) {
                throw new MissingRequiredFieldException("loader");
            }
            if (!obj.has("license")) {
                throw new MissingRequiredFieldException("license");
            }
            if (!obj.has("plugins")) {
                throw new MissingRequiredFieldException("plugins");
            }

            final Builder builder = new Builder()
                    .loader(Adapters.Deserializers.CONTAINER_LOADER.fromJsonTree(obj.get("loader")).build())
                    .license(obj.get("license").getAsString());

            final JsonElement globalElement = obj.get("global");
            @Nullable StandardInheritable inheritable = null;
            if (globalElement instanceof JsonObject) {
                inheritable = context.deserialize(globalElement, StandardInheritable.class);
                builder.globalMetadata(inheritable);
            }

            final JsonElement pluginsElement = obj.get("plugins");
            final List<JsonObject> pluginObjects = new LinkedList<>();

            if (pluginsElement.isJsonArray()) {
                for (final JsonElement pluginElement : ((JsonArray) pluginsElement)) {
                    if (pluginElement.isJsonObject()) {
                        pluginObjects.add((JsonObject) pluginElement);
                    }
                }
            }

            if (pluginObjects.isEmpty()) {
                throw new JsonParseException("No plugin metadata has been specified for the 'plugins' tag!");
            }

            for (final JsonObject pluginObject : pluginObjects) {
                final StandardPluginMetadata.Builder pluginBuilder = context.deserialize(pluginObject, StandardPluginMetadata.Builder.class);
                if (inheritable != null) {
                    pluginBuilder.merge(inheritable);
                }

                builder.addMetadata(pluginBuilder.build());
            }

            try {
                return builder.build();
            } catch (final InvalidVersionSpecificationException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(final MetadataContainer value, final Type type, final JsonSerializationContext context) {
            final JsonObject obj = new JsonObject();
            obj.add("loader", Adapters.Serializers.CONTAINER_LOADER.toJsonTree(value.loader));
            obj.addProperty("license", value.license);

            final JsonArray plugins = new JsonArray();
            for (final PluginMetadata metadata : value.metadata()) {
                plugins.add(context.serialize(metadata, StandardPluginMetadata.class));
            }
            obj.add("plugins", plugins);

            return obj;
        }
    }
}
