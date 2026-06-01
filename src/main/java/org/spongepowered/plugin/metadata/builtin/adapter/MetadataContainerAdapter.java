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
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.builtin.MetadataContainer;
import org.spongepowered.plugin.metadata.builtin.StandardInheritable;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;
import org.spongepowered.plugin.metadata.model.ContainerLoader;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public final class MetadataContainerAdapter implements JsonSerializer<MetadataContainer>, JsonDeserializer<MetadataContainer> {

    @Override
    public MetadataContainer deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();

        final MetadataContainer.Builder builder = new MetadataContainer.Builder()
                .loader(context.deserialize(GsonUtils.require(obj, "loader"), ContainerLoader.class))
                .license(GsonUtils.require(obj, "license").getAsString());

        final JsonElement globalElement = obj.get("global");
        @Nullable StandardInheritable inheritable = null;
        if (globalElement instanceof JsonObject) {
            inheritable = context.deserialize(globalElement, StandardInheritable.class);
            builder.globalMetadata(inheritable);
        }

        final JsonElement pluginsElement = GsonUtils.require(obj, "plugins");
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
        obj.add("loader", context.serialize(value.loader(), ContainerLoader.class));
        obj.addProperty("license", value.license());

        final JsonArray plugins = new JsonArray();
        for (final PluginMetadata metadata : value.metadata()) {
            plugins.add(context.serialize(metadata, StandardPluginMetadata.class));
        }
        obj.add("plugins", plugins);

        return obj;
    }
}
