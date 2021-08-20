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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.apache.maven.artifact.versioning.VersionRange;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Holder;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;

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

    private final String name, loader, license;
    private final VersionRange loaderVersion;
    private final String rawLoaderVersion;
    @Nullable private final Inheritable globalMetadata;
    private final Set<StandardPluginMetadata> metadata = new LinkedHashSet<>();
    private final Map<String, StandardPluginMetadata> metadataById = new LinkedHashMap<>();

    private MetadataHolder(final Builder builder) {
        this.name = builder.name;
        this.loader = builder.loader;
        this.license = builder.license;
        this.loaderVersion = builder.loaderVersion;
        this.rawLoaderVersion = builder.rawLoaderVersion;
        this.globalMetadata = builder.globalMetadata;
        this.metadata.addAll(builder.metadata);
        for (final StandardPluginMetadata pm : this.metadata) {
            this.metadataById.put(pm.id(), pm);
            pm.setHolder(this);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String loader() {
        return this.loader;
    }

    @Override
    public VersionRange loaderVersion() {
        return this.loaderVersion;
    }

    protected String rawLoaderVersion() {
        return this.rawLoaderVersion;
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
                .add("loaderVersion=" + this.rawLoaderVersion)
                .add("license=" + this.license)
                .add("globalMetadata=" + this.globalMetadata)
                .toString();
    }

    public static final class Builder {

        final Set<StandardPluginMetadata> metadata = new LinkedHashSet<>();
        @Nullable String name, loader, license;
        String rawLoaderVersion = "1.0";
        VersionRange loaderVersion = VersionRange.createFromVersion(this.rawLoaderVersion);
        @Nullable Inheritable globalMetadata;

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder loader(final String loader) {
            this.loader = Objects.requireNonNull(loader, "loader");
            return this;
        }

        public Builder license(final String license) {
            this.license = Objects.requireNonNull(license, "license");
            return this;
        }

        public Builder loaderVersion(final String loaderVersion) {
            this.loaderVersion = VersionRange.createFromVersion(Objects.requireNonNull(loaderVersion, "loaderVersion"));
            this.rawLoaderVersion = loaderVersion;
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
            Objects.requireNonNull(this.name, "name");
            Objects.requireNonNull(this.license, "license");
            Objects.requireNonNull(this.loaderVersion, "loaderVersion");

            if (this.metadata.isEmpty()) {
                throw new IllegalStateException("A PluginHolder must hold at least 1 PluginMetadata!");
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
                    .loader(obj.get("loader").getAsString())
                    .loaderVersion(obj.get("loader-version").getAsString())
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

            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(StandardPluginMetadata.class, new StandardPluginMetadata.Deserializer(inheritable));
            gsonBuilder.registerTypeAdapter(StandardInheritable.class, new StandardInheritable.Serializer());

            final Gson gson = gsonBuilder.create();

            for (final JsonObject pluginObject : pluginObjects) {
                builder.addMetadata(gson.fromJson(pluginObject, StandardPluginMetadata.class));
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
            obj.addProperty("loader", value.loader);
            obj.addProperty("loader-version", value.rawLoaderVersion);
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
