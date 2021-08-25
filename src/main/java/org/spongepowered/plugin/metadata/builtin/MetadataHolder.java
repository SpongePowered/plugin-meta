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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Holder;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.Adapters;
import org.spongepowered.plugin.metadata.model.PluginLoader;

import java.lang.reflect.Type;
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

public final class MetadataHolder implements Holder {

    private final String license;
    private final PluginLoader loader;
    @Nullable private final Inheritable globalMetadata;
    private final Set<StandardPluginMetadata> metadata = new LinkedHashSet<>();
    private final Map<String, StandardPluginMetadata> metadataById = new LinkedHashMap<>();

    private MetadataHolder(final Builder builder) {
        this.loader = builder.loader;
        this.license = builder.license;
        this.globalMetadata = builder.globalMetadata;
        this.metadata.addAll(builder.metadata);
        for (final StandardPluginMetadata pm : this.metadata) {
            this.metadataById.put(pm.id(), pm);
            pm.setHolder(this);
        }
    }

    @Override
    public PluginLoader loader() {
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
        return Optional.ofNullable(this.metadataById.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public Set<PluginMetadata> metadata() {
        return Collections.unmodifiableSet(this.metadata);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetadataHolder.class.getSimpleName() + "[", "]")
                .add("loader=" + this.loader)
                .add("license=" + this.license)
                .add("globalMetadata=" + this.globalMetadata)
                .toString();
    }

    public static final class Builder {

        final Set<StandardPluginMetadata> metadata = new LinkedHashSet<>();
        @Nullable String license;
        @Nullable PluginLoader loader;
        @Nullable Inheritable globalMetadata;

        public Builder loader(final PluginLoader loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder globalMetadata(final Inheritable globalMetadata) {
            this.globalMetadata = Objects.requireNonNull(globalMetadata, "globalMetadata");
            return this;
        }

        public Builder metadata(final List<StandardPluginMetadata> metadata) {
            this.metadata.addAll(Objects.requireNonNull(metadata, "metadata"));
            return this;
        }

        public Builder addMetadata(final StandardPluginMetadata metadata) {
            this.metadata.add(Objects.requireNonNull(metadata, "metadata"));
            return this;
        }

        public MetadataHolder build() throws IllegalStateException, InvalidVersionSpecificationException {
            Objects.requireNonNull(this.license, "license");
            Objects.requireNonNull(this.loader, "loader");

            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A MetadataHolder must hold at least 1 PluginMetadata!");
            }

            return new MetadataHolder(this);
        }
    }

    public static final class Serializer implements JsonSerializer<MetadataHolder>, JsonDeserializer<MetadataHolder> {

        @Override
        public MetadataHolder deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject obj = element.getAsJsonObject();
            final Builder builder = new Builder()
                    .loader(Adapters.PLUGIN_LOADER.fromJsonTree(obj.get("loader")))
                    .license(obj.get("license").getAsString());

            final JsonElement globalElement = obj.get("global");
            @Nullable StandardInheritable inheritable = null;
            if (!(globalElement instanceof JsonNull)) {
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
                    pluginBuilder.from(inheritable);
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
        public JsonElement serialize(final MetadataHolder value, final Type type, final JsonSerializationContext context) {
            final JsonObject obj = new JsonObject();
            obj.add("loader", Adapters.PLUGIN_LOADER.toJsonTree(value.loader));
            obj.addProperty("license", value.license);

            // TODO Determine what properties are equal and not write all the plugin's metadata?
            final JsonArray plugins = new JsonArray();
            for (final PluginMetadata metadata : value.metadata) {
                plugins.add(context.serialize(metadata, StandardPluginMetadata.class));
            }
            obj.add("plugins", plugins);

            return obj;
        }
    }
}
