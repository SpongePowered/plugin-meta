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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spongepowered.plugin.metadata.builtin.MetadataContainer;
import org.spongepowered.plugin.metadata.builtin.InheritableMetadata;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public final class MetadataContainerAdapter implements JsonSerializer<MetadataContainer>, JsonDeserializer<MetadataContainer> {

    @Override
    public MetadataContainer deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();

        // Read some global data in the root element for retro-compatibility
        InheritableMetadata global = InheritableMetadata.builder()
                .loader(GsonUtils.optional(obj, "loader").map(v -> context.<PluginLoaderSpecification>deserialize(v, PluginLoaderSpecification.class)).orElse(null))
                .license(GsonUtils.optional(obj, "license").map(JsonElement::getAsString).orElse(null))
                .build();

        final JsonElement globalElement = obj.get("global");
        if (globalElement instanceof JsonObject) {
            global = global.with(context.deserialize(globalElement, InheritableMetadata.class));
        }

        final List<StandardPluginMetadata> plugins = new LinkedList<>();
        if (GsonUtils.require(obj, "plugins") instanceof JsonArray pluginsArray) {
            for (final JsonElement pluginElement : pluginsArray) {
                if (pluginElement.isJsonObject()) {
                    plugins.add(context.<StandardPluginMetadata.Builder>deserialize(pluginElement, StandardPluginMetadata.Builder.class)
                            .global(global).build());
                }
            }
        }

        return new MetadataContainer(global, plugins);
    }

    @Override
    public JsonElement serialize(final MetadataContainer value, final Type type, final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        if (!value.global().equals(InheritableMetadata.none())) {
            obj.add("global", context.serialize(value.global(), InheritableMetadata.class));
        }

        final JsonArray plugins = new JsonArray();
        for (final StandardPluginMetadata plugin : value.plugins()) {
            plugins.add(context.serialize(plugin, StandardPluginMetadata.class));
        }
        obj.add("plugins", plugins);

        return obj;
    }
}
