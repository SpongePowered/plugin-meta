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
package org.spongepowered.plugin.metadata.builtin.adapter;

import com.google.gson.*;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.spongepowered.plugin.metadata.builtin.InheritableMetadata;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginConflict;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public final class InheritableMetadataAdapter implements JsonSerializer<InheritableMetadata>, JsonDeserializer<InheritableMetadata> {

    @Override
    public InheritableMetadata deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        return InheritableMetadata.builder()
                .version(GsonUtils.optional(obj, "version").map(v -> context.<ArtifactVersion>deserialize(v, ArtifactVersion.class)).orElse(null))
                .loader(GsonUtils.optional(obj, "loader").map(v -> context.<PluginLoaderSpecification>deserialize(v, PluginLoaderSpecification.class)).orElse(null))
                .name(GsonUtils.optional(obj, "name").map(JsonElement::getAsString).orElse(null))
                .description(GsonUtils.optional(obj, "description").map(JsonElement::getAsString).orElse(null))
                .license(GsonUtils.optional(obj, "license").map(JsonElement::getAsString).orElse(null))
                .branding(GsonUtils.optional(obj, "branding").map(v -> context.<PluginBranding>deserialize(v, PluginBranding.class)).orElseGet(PluginBranding::none))
                .links(GsonUtils.optional(obj, "links").map(v -> context.<PluginLinks>deserialize(v, PluginLinks.class)).orElseGet(PluginLinks::none))
                .contributors(GsonUtils.stream(obj, "contributors").map(v -> context.<PluginContributor>deserialize(v, PluginContributor.class)).toList())
                .conflicts(GsonUtils.stream(obj, "conflicts").map(v -> context.<PluginConflict>deserialize(v, PluginConflict.class)).toList())
                .dependencies(GsonUtils.stream(obj, "dependencies").map(v -> context.<PluginDependency>deserialize(v, PluginDependency.class)).toList())
                .properties(GsonUtils.deserializeMap(obj.get("properties"), JsonElement::getAsString, LinkedHashMap::new))
                .build();
    }

    @Override
    public JsonElement serialize(final InheritableMetadata value, final Type type, final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        value.version().ifPresent(v -> obj.add("version", context.serialize(v, ArtifactVersion.class)));
        value.loader().ifPresent(v -> obj.add("loader", context.serialize(v, PluginLoaderSpecification.class)));
        value.name().ifPresent(v -> obj.addProperty("name", v));
        value.description().ifPresent(v -> obj.addProperty("description", v));
        value.license().ifPresent(v -> obj.addProperty("license", v));
        if (!value.branding().equals(PluginBranding.none())) {
            obj.add("branding", context.serialize(value.branding(), PluginBranding.class));
        }
        if (!value.links().equals(PluginLinks.none())) {
            obj.add("links", context.serialize(value.links(), PluginLinks.class));
        }
        if (!value.contributors().isEmpty()) {
            obj.add("contributors", GsonUtils.toArray(value.contributors().stream().map(v -> context.serialize(v, PluginContributor.class))));
        }
        if (!value.conflicts().isEmpty()) {
            obj.add("conflicts", GsonUtils.toArray(value.conflicts().stream().map(v -> context.serialize(v, PluginConflict.class))));
        }
        if (!value.dependencies().isEmpty()) {
            obj.add("dependencies", GsonUtils.toArray(value.dependencies().values().stream().map(v -> context.serialize(v, PluginDependency.class))));
        }
        if (!value.properties().isEmpty()) {
            obj.add("properties", GsonUtils.serializeMap(value.properties(), v -> new JsonPrimitive(v.toString())));
        }
        return obj;
    }
}
